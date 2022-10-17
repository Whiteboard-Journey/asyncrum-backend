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
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.userteam.team.service.TeamService;
import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.domain.whiteboard.exception.WhiteboardNotExistsException;
import swm.wbj.asyncrum.domain.whiteboard.repository.WhiteboardRepository;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.ScopeType;

import static swm.wbj.asyncrum.global.media.AwsService.WHITEBOARD_BUCKET_NAME;
import static swm.wbj.asyncrum.global.media.AwsService.WHITEBOARD_FILE_PREFIX;
import static swm.wbj.asyncrum.global.type.ScopeType.isTeamScope;

@RequiredArgsConstructor
@Transactional
@Service
public class WhiteboardServiceImpl implements WhiteboardService {

    private final WhiteboardRepository whiteboardRepository;
    private final MemberService memberService;
    private final TeamService teamService;
    private final AwsService awsService;

    @Override
    public WhiteboardCreateResponseDto createWhiteboard(WhiteboardCreateRequestDto requestDto) {
        Member currentMember = memberService.getCurrentMember();
        Team currentTeam = validateWhiteboardTeamMember(requestDto.getTeamId(), currentMember);

        Whiteboard whiteboard = requestDto.toEntity(currentMember, currentTeam);
        Long whiteboardId = whiteboardRepository.save(whiteboard).getId();

        String whiteboardFileKey = createWhiteboardFileKey(currentTeam.getId(), currentMember.getId(), whiteboardId);
        String preSignedURL = awsService.generatePresignedURL(whiteboardFileKey, WHITEBOARD_BUCKET_NAME, FileType.TLDR);
        whiteboard.updateWhiteboardFileMetadata(
                whiteboardFileKey, awsService.getObjectURL(whiteboardFileKey, WHITEBOARD_BUCKET_NAME));

        return new WhiteboardCreateResponseDto(whiteboard.getId(), preSignedURL);
    }

    @Override
    public Whiteboard getCurrentWhiteboard(Long id) {
        Member currentMember = memberService.getCurrentMember();
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(WhiteboardNotExistsException::new);
        validateWhiteboardTeamMember(whiteboard.getTeam().getId(), currentMember);
        return whiteboard;
    }

    @Transactional(readOnly = true)
    @Override
    public WhiteboardReadAllResponseDto readAllWhiteboard(Long teamId, ScopeType scope, Integer pageIndex,
                                                          Long topId, Integer sizePerPage) {
        Member currentMember = memberService.getCurrentMember();
        Team currentTeam = validateWhiteboardTeamMember(teamId, currentMember);

        Page<Whiteboard> whiteboardPage;
        Pageable pageable = PageRequest.of(pageIndex, sizePerPage, Sort.Direction.DESC, "id");

        if(isTeamScope(scope)) {
            whiteboardPage = (topId == 0L) ?
                    whiteboardRepository.findAllByTeam(currentTeam, currentMember, pageable) :
                    whiteboardRepository.findAllByTeamWithTopId(currentTeam, currentMember, topId, pageable);
        }
        else {
            whiteboardPage = (topId == 0L) ?
                    whiteboardRepository.findAllByTeamAndMember(currentTeam, currentMember, pageable) :
                    whiteboardRepository.findAllByTeamAndMemberWithTopId(currentTeam, currentMember, topId, pageable);
        }

        return new WhiteboardReadAllResponseDto(
                whiteboardPage.getContent(), whiteboardPage.getPageable(), whiteboardPage.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public WhiteboardReadResponseDto readWhiteboard(Long id) {

        Whiteboard whiteboard = getCurrentWhiteboard(id);

        return new WhiteboardReadResponseDto(whiteboard);
    }



    @Override
    public WhiteboardUpdateResponseDto updateWhiteboard(Long id, WhiteboardUpdateRequestDto requestDto) {
        Whiteboard whiteboard = getMemberWhiteboard(id);

        whiteboard.updateTitleAndDescription(requestDto.getTitle(), requestDto.getDescription());
        whiteboard.updateScope(ScopeType.of(requestDto.getScope()));

        String preSignedURL = awsService.generatePresignedURL(
                whiteboard.getWhiteboardFileKey(), WHITEBOARD_BUCKET_NAME, FileType.TLDR);

        return new WhiteboardUpdateResponseDto(whiteboardRepository.save(whiteboard).getId(), preSignedURL);
    }

    @Override
    public void deleteWhiteboard(Long id) {
        Whiteboard whiteboard = getMemberWhiteboard(id);

        awsService.deleteFile(whiteboard.getWhiteboardFileKey(), WHITEBOARD_BUCKET_NAME);
        whiteboardRepository.delete(whiteboard);
    }

    private Team validateWhiteboardTeamMember(Long teamId, Member currentMember) {
        return teamService.getTeamWithTeamMemberValidation(teamId, currentMember);
    }

    private Whiteboard getMemberWhiteboard(Long id) {
        Member currentMember = memberService.getCurrentMember();
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(WhiteboardNotExistsException::new);

        if(!ownsWhiteboard(currentMember, whiteboard)) {
            throw new OperationNotAllowedException();
        }
        return whiteboard;
    }

    private boolean ownsWhiteboard(Member currentMember, Whiteboard whiteboard) {
        return whiteboard.getMember().equals(currentMember);
    }

    public String createWhiteboardFileKey(Long teamId, Long memberId, Long whiteboardId) {
        return WHITEBOARD_FILE_PREFIX + "_" + teamId + "_" + memberId + "_" + whiteboardId + "." + FileType.TLDR.getName();
    }
}
