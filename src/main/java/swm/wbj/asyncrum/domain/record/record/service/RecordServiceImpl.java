package swm.wbj.asyncrum.domain.record.record.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.record.record.dto.*;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.record.record.exception.RecordNotExistsException;
import swm.wbj.asyncrum.domain.record.record.repository.RecordRepository;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.userteam.team.service.TeamService;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.util.*;

import static swm.wbj.asyncrum.global.media.AwsService.RECORD_BUCKET_NAME;
import static swm.wbj.asyncrum.global.media.AwsService.RECORD_FILE_PREFIX;
import static swm.wbj.asyncrum.global.type.ScopeType.isTeamScope;

@RequiredArgsConstructor
@Transactional
@Service
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final MemberService memberService;
    private final TeamService teamService;
    private final AwsService awsService;

    @Override
    public RecordCreateResponseDto createRecord(RecordCreateRequestDto requestDto) {
        Member currentMember = memberService.getCurrentMember();
        Team currentTeam = validateRecordTeamMember(requestDto.getTeamId(), currentMember);

        Record record = requestDto.toEntity(currentMember, currentTeam);
        record.updateSeenMember(Set.of(currentMember.getId()));
        Long recordId = recordRepository.save(record).getId();

        String recordFileKey = createRecordFileKey(currentTeam.getId(), currentMember.getId(), recordId);
        String preSignedURL = awsService.generatePresignedURL(recordFileKey, RECORD_BUCKET_NAME, FileType.MP4);
        record.updateRecordFileMetadata(recordFileKey, awsService.getObjectURL(recordFileKey, RECORD_BUCKET_NAME));

        return new RecordCreateResponseDto(record.getId(), preSignedURL);
    }

    @Override
    @Transactional(readOnly = true)
    public Record getCurrentRecord(Long id) {
        Member currentMember = memberService.getCurrentMember();
        Record record = recordRepository.findById(id)
                .orElseThrow(RecordNotExistsException::new);

        validateRecordTeamMember(record.getTeam().getId(), currentMember);
        return record;
    }

    @Override
    @Transactional(readOnly = true)
    public RecordReadResponseDto readRecord(Long id){
        Record record = getCurrentRecord(id);

        return new RecordReadResponseDto(record);
    }

    @Override
    @Transactional(readOnly = true)
    public RecordReadAllResponseDto readAllRecord(Long teamId, ScopeType scope, Integer pageIndex,
                                                  Long topId, Integer sizePerPage) {
        Member currentMember = memberService.getCurrentMember();
        Team currentTeam = validateRecordTeamMember(teamId, currentMember);

        Page<Record> recordPage;
        Pageable pageable = PageRequest.of(pageIndex, sizePerPage, Sort.Direction.DESC, "id");

        if(isTeamScope(scope)) {
            recordPage = (topId == 0L) ?
                    recordRepository.findAllByTeam(currentTeam, currentMember, pageable) :
                    recordRepository.findAllByTeamAndTopId(currentTeam, currentMember, topId, pageable);
        }
        else {
            recordPage = (topId == 0L) ?
                    recordRepository.findAllByTeamAndMember(currentTeam, currentMember, pageable) :
                    recordRepository.findAllByTeamAndMemberAndTopId(currentTeam, currentMember, topId, pageable);
        }

        return new RecordReadAllResponseDto(recordPage.getContent(), recordPage.getPageable(), recordPage.isLast());
    }

    @Override
    public RecordUpdateResponseDto updateRecord(Long id, RecordUpdateRequestDto requestDto) {
        Member currentMember = memberService.getCurrentMember();
        Record record = getRecord(id).orElseThrow(RecordNotExistsException::new);

        if (ownsRecord(currentMember, record)) {
            // 오너
            record.updateTitleAndDescription(requestDto.getTitle(), requestDto.getDescription());
            record.updateScope(ScopeType.of(requestDto.getScope()));
        } else if (teamService.getTeamWithTeamMemberValidation(record.getTeam().getId(), currentMember) != null) {
            // 멤버
            Set<Long> seenMemberIds = Optional.of(record.getSeenMember()).orElse(new HashSet<>());
            seenMemberIds.add(memberService.getCurrentMember().getId());
            record.updateSeenMember(seenMemberIds);
        } else {
            throw new OperationNotAllowedException();
        }

        String preSignedURL = awsService.generatePresignedURL(
                record.getRecordFileKey(), RECORD_BUCKET_NAME, FileType.MP4);

        return new RecordUpdateResponseDto(recordRepository.save(record).getId(), preSignedURL);
    }

    @Override
    public void deleteRecord(Long id) {
        Record record = getMemberRecord(id);

        awsService.deleteFile(record.getRecordFileKey(), RECORD_BUCKET_NAME);
        recordRepository.delete(record);
    }

    private Team validateRecordTeamMember(Long teamId, Member currentMember) {
        return teamService.getTeamWithTeamMemberValidation(teamId, currentMember);
    }

    private Optional<Record> getRecord(Long id) {
        return recordRepository.findById(id);
    }

    private Record getMemberRecord(Long id) {
        Member currentMember = memberService.getCurrentMember();
        Record record = recordRepository.findById(id)
                .orElseThrow(RecordNotExistsException::new);

        if (!ownsRecord(currentMember, record)) {
            throw new OperationNotAllowedException();
        }

        return record;
    }

    private boolean ownsRecord(Member currentMember, Record record) {
        return record.getMember().equals(currentMember);
    }

    private String createRecordFileKey(Long teamId, Long memberId, Long recordId) {
        return RECORD_FILE_PREFIX + "_" + teamId + "_" + memberId + "_" + recordId + "." + FileType.MP4.getName();
    }
}
