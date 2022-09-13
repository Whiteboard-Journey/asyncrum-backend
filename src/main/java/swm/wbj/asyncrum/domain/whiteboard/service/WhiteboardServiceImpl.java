package swm.wbj.asyncrum.domain.whiteboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.domain.whiteboard.repository.WhiteboardRepository;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.io.IOException;

@RequiredArgsConstructor
@Transactional
@Service
public class WhiteboardServiceImpl implements WhiteboardService {

    private final WhiteboardRepository whiteboardRepository;
    private final MemberService memberService;
    private final AwsService awsService;

    private static final String WHITEBOARD_BUCKET_NAME = "whiteboards";
    private static final String WHITEBOARD_FILE_PREFIX ="whiteboard";

    @Override
    public WhiteboardCreateResponseDto createWhiteboard(WhiteboardCreateRequestDto requestDto) throws IOException {
        String title = requestDto.getTitle();

        if(whiteboardRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("해당 제목은 이미 사용중입니다.");
        }

        Whiteboard whiteboard = requestDto.toEntity(memberService.getCurrentMember());
        Long whiteboardId = whiteboardRepository.save(whiteboard).getId();

        String whiteboardFileKey = createWhiteboardFileKey(memberService.getCurrentMember().getId(), whiteboardId);

        String preSignedURL = awsService.generatePresignedURL(whiteboardFileKey, WHITEBOARD_BUCKET_NAME, FileType.TLDR);

        // 화이트보드 엔티티에 화이트보드 문서 파일명 저장
        whiteboard.update(null, null, whiteboardFileKey, awsService.getObjectURL(whiteboardFileKey, WHITEBOARD_BUCKET_NAME), null);

        return new WhiteboardCreateResponseDto(whiteboard.getId(), preSignedURL);
    }

    @Transactional(readOnly = true)
    @Override
    public WhiteboardReadResponseDto readWhiteboard(Long id) {
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 화이트보드 문서가 존재하지 않습니다."));

        return new WhiteboardReadResponseDto(whiteboard);
    }

    @Transactional(readOnly = true)
    @Override
    public WhiteboardReadAllResponseDto readAllWhiteboard(Integer pageIndex, Long topId, Integer sizePerPage) {
        Pageable pageable = PageRequest.of(pageIndex, sizePerPage, Sort.Direction.DESC, "id");
        Page<Whiteboard> whiteboardPage = getWhiteboardPage(topId, pageable);

        return new WhiteboardReadAllResponseDto(whiteboardPage.getContent(), whiteboardPage.getPageable(), whiteboardPage.isLast());
    }

    private Page<Whiteboard> getWhiteboardPage(Long topId, Pageable pageable) {
        Page<Whiteboard> whiteboardPage;
        if(topId == 0) {
            whiteboardPage = whiteboardRepository.findAll(pageable);
        }
        else {
            whiteboardPage = whiteboardRepository.findAllByTopId(topId, pageable);
        }
        return whiteboardPage;
    }

    @Transactional(readOnly = true)
    @Override
    public WhiteboardReadAllResponseDto readAllWhiteboard(ScopeType scope, Integer pageIndex, Long topId, Integer sizePerPage) {
        Pageable pageable = PageRequest.of(pageIndex, sizePerPage, Sort.Direction.DESC, "whiteboard_id");
        Member currentMember = memberService.getCurrentMember();
        Page<Whiteboard> whiteboardPage = getWhiteboards(scope, topId, pageable, currentMember);

        return new WhiteboardReadAllResponseDto(whiteboardPage.getContent(), whiteboardPage.getPageable(), whiteboardPage.isLast());
    }

    private Page<Whiteboard> getWhiteboards(ScopeType scope, Long topId, Pageable pageable, Member currentMember) {
        Page<Whiteboard> whiteboardPage;
        if (scope == ScopeType.TEAM && currentMember.getTeam() != null) {
            if(topId == 0) {
                whiteboardPage = whiteboardRepository.findAllByTeam(currentMember.getTeam().getId(), currentMember.getId(), pageable);
            }
            else {
                whiteboardPage = whiteboardRepository.findAllByTeamAndTopId(currentMember.getTeam().getId(), currentMember.getId(), topId, pageable);
            }
        }
        else if (scope == ScopeType.PRIVATE || currentMember.getTeam() == null) {
            if(topId == 0) {
                whiteboardPage = whiteboardRepository.findAllByAuthor(currentMember.getId(), pageable);
            }
            else {
                whiteboardPage = whiteboardRepository.findAllByAuthorAndTopId(currentMember.getId(), topId, pageable);
            }
        }
        else {
            throw new IllegalArgumentException("허용되지 않은 범위입니다.");
        }
        return whiteboardPage;
    }

    @Override
    public WhiteboardUpdateResponseDto updateWhiteboard(Long id, WhiteboardUpdateRequestDto requestDto) throws IOException {
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 화이트보드 문서가 존재하지 않습니다."));

        whiteboard.update(requestDto.getTitle(), requestDto.getDescription(), null, null, ScopeType.of(requestDto.getScope()));

        String preSignedURL = awsService.generatePresignedURL(whiteboard.getWhiteboardFileKey(), WHITEBOARD_BUCKET_NAME, FileType.TLDR);

        return new WhiteboardUpdateResponseDto(whiteboardRepository.save(whiteboard).getId(), preSignedURL);
    }

    @Override
    public void deleteWhiteboard(Long id) {
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 화이트보드 문서가 존재하지 않습니다."));

        awsService.deleteFile(whiteboard.getWhiteboardFileKey(), WHITEBOARD_BUCKET_NAME);
        whiteboardRepository.delete(whiteboard);
    }

    /**
     * 화이트보드 문서 파일명 생성 (Object Key)
     * 파일명 = "whiteboard" + Member ID + Whiteboard ID
     *     ex) whiteboard_2342_32
     */
    public String createWhiteboardFileKey(Long memberId, Long whiteboardId) {
        return WHITEBOARD_FILE_PREFIX + "_" + memberId + "_" + whiteboardId + "." + FileType.TLDR.getName();
    }
}
