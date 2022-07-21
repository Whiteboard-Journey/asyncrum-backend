package swm.wbj.asyncrum.domain.whiteboard.service;

import com.amazonaws.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamReadAllResponseDto;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.domain.whiteboard.repository.WhiteboardRepository;
import swm.wbj.asyncrum.global.media.AwsService;

import java.io.IOException;

@RequiredArgsConstructor
@Transactional
@Service
public class WhiteboardServiceImpl implements WhiteboardService {

    private final WhiteboardRepository whiteboardRepository;
    private final MemberService memberService;
    private final AwsService awsService;

    private static final String AWS_S3_WHITEBOARD_FILE_COLLECTION_NAME = "whiteboards";
    private static final String WHITEBOARD_FILE_PREFIX ="whiteboard";

    /**
     * 화이트보드 문서 생성
     */
    @Override
    public WhiteboardCreateResponseDto createWhiteboard(WhiteboardCreateRequestDto requestDto) throws IOException {
        String title = requestDto.getTitle();

        if(whiteboardRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("해당 제목은 이미 사용중입니다.");
        }

        Whiteboard whiteboard = requestDto.toEntity(memberService.getCurrentMember());
        Long whiteboardId = whiteboardRepository.save(whiteboard).getId();

        String whiteboardFileKey = createWhiteboardFileKey(memberService.getCurrentMember().getId(), whiteboardId);

        String preSignedURL = awsService.generatePresignedURL(whiteboardFileKey, AWS_S3_WHITEBOARD_FILE_COLLECTION_NAME, HttpMethod.PUT);

        // 화이트보드 엔티티에 화이트보드 문서 파일명 저장
        whiteboard.update(null, null, whiteboardFileKey, null);

        return new WhiteboardCreateResponseDto(whiteboard.getId(), preSignedURL);
    }

    /**
     * 화이트보드 문서 개별 조회
     */
    @Transactional(readOnly = true)
    @Override
    public WhiteboardReadResponseDto readWhiteboard(Long id) throws IOException {
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 화이트보드 문서가 존재하지 않습니다."));

        String preSignedURL = awsService.generatePresignedURL(whiteboard.getWhiteboardFileKey(), AWS_S3_WHITEBOARD_FILE_COLLECTION_NAME, HttpMethod.GET);

        return new WhiteboardReadResponseDto(whiteboard);
    }

    /**
     * 화이트보드 문서 전체 조희
     */
    @Transactional(readOnly = true)
    @Override
    public WhiteboardReadAllResponseDto readAllWhiteboard(Integer pageIndex, Long topId) {
        int SIZE_PER_PAGE = 10;

        Page<Whiteboard> whiteboardPage;
        Pageable pageable = PageRequest.of(pageIndex, SIZE_PER_PAGE, Sort.Direction.DESC, "id");

        if(topId == 0) {
            whiteboardPage = whiteboardRepository.findAll(pageable);
        }
        else {
            whiteboardPage = whiteboardRepository.findAllByTopId(topId, pageable);
        }

        return new WhiteboardReadAllResponseDto(whiteboardPage.getContent(), whiteboardPage.getPageable(), whiteboardPage.isLast());
    }

    /**
     * 화이트보드 문서 정보 업데이트
     */
    @Override
    public WhiteboardUpdateResponseDto updateWhiteboard(Long id, WhiteboardUpdateRequestDto requestDto) {
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 화이트보드 문서가 존재하지 않습니다."));

        whiteboard.update(requestDto.getTitle(), requestDto.getDescription(), null, requestDto.getScope());

        return new WhiteboardUpdateResponseDto(whiteboardRepository.save(whiteboard).getId());
    }

    /**
     * 화이트보드 문서 삭제
     */
    @Override
    public void deleteWhiteboard(Long id) {
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 화이트보드 문서가 존재하지 않습니다."));

        whiteboardRepository.delete(whiteboard);
    }

    /**
     * 화이트보드 문서 파일명 생성 (Object Key)
     * 파일명 = "whiteboard" + Member ID + Whiteboard ID
     *     ex) whiteboard_2342_32
     */
    public String createWhiteboardFileKey(Long memberId, Long whiteboardId) {
        return WHITEBOARD_FILE_PREFIX + "_" + memberId + "_" + whiteboardId + ".tldr";
    }
}
