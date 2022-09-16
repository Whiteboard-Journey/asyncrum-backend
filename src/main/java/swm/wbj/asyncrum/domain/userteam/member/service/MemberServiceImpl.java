package swm.wbj.asyncrum.domain.userteam.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.member.exeception.EmailAlreadyInUseException;
import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.userteam.member.exeception.MemberNotExistsException;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.mail.MailService;
import swm.wbj.asyncrum.global.oauth.utils.TokenUtil;
import swm.wbj.asyncrum.global.utils.UrlService;

/**
 * 사용자의 role에 따라 API policy가 달라짐
 * Role.USER : 자신의 정보만 조작 가능
 * Role.ADMIN: id로 모든 사용자 조작 가능
 *
 * TODO: API V2에서 ROLE에 따라 비즈니스 로직 분리 - 다른 API 엔드포인트로 접근하도록 재설계
 */
@RequiredArgsConstructor
@Transactional
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final UrlService urlService;
    private final AwsService awsService;

    private static final String EMAIL_VERIFICATION_URL = "/api/v1/members/email/verification";

    @Override
    public MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto) {
        String email = requestDto.getEmail();
        if(memberRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException();
        }

        Member member = requestDto.toEntity(passwordEncoder);
        return new MemberCreateResponseDto(memberRepository.save(member).getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Member getCurrentMember() {
        // JWT 토큰 -> Security Context의 Authenication -> Member id -> Member 엔티티 가져오기
        Long memberId = TokenUtil.getCurrentMemberId();

        return memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Member getUserByIdOrEmail(Long id, String email) {
        if(id != null) {
            return memberRepository.findById(id)
                    .orElseThrow(MemberNotExistsException::new);
        }
        else {
            return memberRepository.findByEmail(email);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MemberReadResponseDto readMember(Long id){
        Member member;
        Member currentMember = this.getCurrentMember();
        RoleType currentMemberRoleType = currentMember.getRoleType();

        switch (currentMemberRoleType) {
            case ADMIN:
                member = memberRepository.findById(id)
                        .orElseThrow(MemberNotExistsException::new);
                break;
            case USER:
                member = currentMember;
                break;
            case GUEST:
            default:
                throw new OperationNotAllowedException();
        }

        return new MemberReadResponseDto(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberReadAllResponseDto readAllMember(Integer pageIndex, Long topId, Integer sizePerPage) {
        Member currentMember = this.getCurrentMember();
        if(!currentMember.getRoleType().equals(RoleType.ADMIN)) {
            throw new OperationNotAllowedException();
        }

        Page<Member> memberPage;
        Pageable pageable = PageRequest.of(pageIndex, sizePerPage, Sort.Direction.DESC, "id");

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
        Member currentMember = this.getCurrentMember();
        Member member;

        if(currentMember.getRoleType().equals(RoleType.ADMIN)) {
            member = memberRepository.findById(id)
                    .orElseThrow(MemberNotExistsException::new);
        }
        else {
            member = memberRepository.findById(currentMember.getId())
                    .orElseThrow(MemberNotExistsException::new);
        }

        member.updateFullname(requestDto.getFullname());
        return new MemberUpdateResponseDto(member.getId());
    }

    @Override
    public void deleteMember(Long id) {
        Member currentMember = this.getCurrentMember();
        Member member;

        if(currentMember.getRoleType().equals(RoleType.ADMIN)) {
            member = memberRepository.findById(id)
                    .orElseThrow(MemberNotExistsException::new) ;
        }
        else {
            member = memberRepository.findById(currentMember.getId())
                    .orElseThrow(MemberNotExistsException::new);
        }

        memberRepository.delete(member);
    }

    /**
     * 메일 인증 링크 전송
     * TODO: 추후 링크 hashing 및 expire 설정 기능 추가
     */
    @Override
    public void sendEmailVerificationLinkByEmail() throws Exception {
        Member currentMember = this.getCurrentMember();
        String emailVerificationLink = urlService.buildURL(EMAIL_VERIFICATION_URL, "memberId", currentMember.getId());

        mailService.sendMailVerificationLink(currentMember.getEmail(), emailVerificationLink);
    }

    /**
     * 메일 인증 링크 검증 및 처리 (Role Update)
     * TODO: hashing된 링크 검증 및 처리하도록 변경
     * TODO: Role이 달라졌기 때문에, JWT 토큰 재발급 필요
     */
    @Override
    public void verifyEmailVerificationLink(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotExistsException::new);

        member.updateRole(RoleType.USER);
    }

    @Override
    public ImageCreateResponseDto createImage(Long id) {
        Member currentMember = this.getCurrentMember();
        Member member;

        if(currentMember.getRoleType().equals(RoleType.ADMIN)) {
            member = memberRepository.findById(id)
                    .orElseThrow(MemberNotExistsException::new);
        }
        else {
            member = currentMember;
        }

        String imageFileKey = createImageFileKey(member.getId());
        String preSignedURL = awsService.generatePresignedURL(imageFileKey, AwsService.IMAGE_BUCKET_NAME, FileType.PNG);

        member.updateProfileImage(imageFileKey, awsService.getObjectURL(imageFileKey, AwsService.IMAGE_BUCKET_NAME));

        return new ImageCreateResponseDto(member.getId(), preSignedURL);
    }

    public String createImageFileKey(Long memberId) {
        return AwsService.IMAGE_MEMBER_FILE_PREFIX + "_" + memberId + "." + FileType.PNG.getName();
    }
}
