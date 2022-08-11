package swm.wbj.asyncrum.domain.userteam.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.whiteboard.dto.WhiteboardCreateResponseDto;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.mail.MailService;
import swm.wbj.asyncrum.global.oauth.utils.TokenUtil;
import swm.wbj.asyncrum.global.utils.UrlService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final UrlService urlService;
    private final AwsService awsService;

    private static final String IMAGE_BUCKET_NAME = "images";
    private static final String IMAGE_FILE_PREFIX ="member_image";
    @Override
    public MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto) {
        String email = requestDto.getEmail();
        if(memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("해당 이메일은 이미 사용중입니다.");
        }

        Member member = requestDto.toEntity(passwordEncoder);
        return new MemberCreateResponseDto(memberRepository.save(member).getId());
    }

    /**
     * 요청을 보낸 사용자의 정보 가져오기
     */
    @Override
    @Transactional(readOnly = true)
    public Member getCurrentMember() {
        // JWT 토큰 -> Security Context의 Authenication -> Member id -> Member 엔티티 가져오기
        Long memberId = TokenUtil.getCurrentMemberId();

        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Member getUserByIdOrEmail(Long id, String email) {
        if(id != null) {
            return memberRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        }
        else {
            return memberRepository.findByEmail(email);
        }
    }

    /**
     * 사용자 조희
     * 사용자의 role에 따라 fetch policy가 달라짐
     * Role.USER : 자신의 정보만 조회
     * Role.ADMIN: id로 특정 사용자 조회
     */
    @Override
    @Transactional(readOnly = true)
    public MemberReadResponseDto readMember(Long id){
        Member member;
        Member currentMember = this.getCurrentMember();
        RoleType memberRoleType = currentMember.getRoleType();

        switch (memberRoleType) {
            case ADMIN:
                member = memberRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
                break;
            case USER:
                member = currentMember;
                break;
            case GUEST:
            default:
                throw new IllegalArgumentException("허용되지 않은 작업입니다.");
        }

        return new MemberReadResponseDto(member);
    }

    // TODO: Role에 따른 Member Fetch Policy 추후 개선하기
    /**
     * 사용자 전체 조희
     * 사용자의 role에 따라 fetch policy가 달라짐
     * Role.USER : 허용되지 않은 작업으로 처리
     * Role.ADMIN : 사용자 리스트 전체 조회
     */
    @Override
    @Transactional(readOnly = true)
    public MemberReadAllResponseDto readAllMember(Integer pageIndex, Long topId) {
        Member currentMember = this.getCurrentMember();
        if(!currentMember.getRoleType().equals(RoleType.ADMIN)) {
            throw new IllegalArgumentException("허용되지 않은 작업입니다.");
        }

        int SIZE_PER_PAGE = 12;
        Page<Member> memberPage;
        Pageable pageable = PageRequest.of(pageIndex, SIZE_PER_PAGE, Sort.Direction.DESC, "id");

        if(topId == 0) {
            memberPage = memberRepository.findAll(pageable);
        }
        else {
            memberPage = memberRepository.findAllByTopId(topId, pageable);
        }

        return new MemberReadAllResponseDto(memberPage.getContent(), memberPage.getPageable(), memberPage.isLast());
    }

    @Override
    public MemberUpdateResponseDto updateMember(Long id, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        member.update(requestDto.getFullname(), null, null);
        return new MemberUpdateResponseDto(memberRepository.save(member).getId());
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다. ")) ;
        memberRepository.delete(member);
    }

    /**
     * 메일 인증 링크 전송
     * TODO: 추후 링크 hashing 및 expire 설정 기능 추가
     */
    @Override
    public void sendEmailVerificationLinkByEmail() throws Exception {
        Member member = this.getCurrentMember();
        String emailVerificationLink = urlService.buildURL("/api/v1/members/email/verification", "memberId", member.getId());
        mailService.sendMailVerificationLink(member.getEmail(), emailVerificationLink);
    }

    /**
     * 메일 인증 링크 검증 및 처리 (Role Update)
     * TODO: hashing된 링크 검증 및 처리하도록 변경
     */
    @Override
    public void verifyEmailVerificationLink(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        member.updateRole(RoleType.USER);
        memberRepository.save(member);
    }

    @Override
    public ImageCreateResponseDto createImage(Long id) throws IOException {


        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        String imageFileKey = createImageFileKey(member.getId());
        String preSignedURL = awsService.generatePresignedURL(imageFileKey, IMAGE_BUCKET_NAME, FileType.PNG);
        member.update(null, imageFileKey, awsService.getObjectURL(imageFileKey, IMAGE_BUCKET_NAME));
        return new ImageCreateResponseDto(id, preSignedURL);
    }

    public String createImageFileKey(Long memberId) {
        return IMAGE_FILE_PREFIX + "_" + memberId + "." + FileType.PNG.getName();
    }
}
