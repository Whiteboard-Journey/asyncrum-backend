package swm.wbj.asyncrum.domain.record.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.record.dto.*;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.record.exception.RecordNotExistsException;
import swm.wbj.asyncrum.domain.record.exception.TitleAlreadyInUseException;
import swm.wbj.asyncrum.domain.record.repository.RecordRepository;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.util.List;

import static swm.wbj.asyncrum.global.media.AwsService.RECORD_BUCKET_NAME;
import static swm.wbj.asyncrum.global.media.AwsService.RECORD_FILE_PREFIX;

@RequiredArgsConstructor
@Transactional
@Service
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final MemberService memberService;
    private final AwsService awsService;

    @Override
    @Transactional(readOnly = true)
    public RecordReadAllResponseDto readAllRecord(ScopeType scope, Integer pageIndex, Long topId, Integer sizePerPage) {
        Member currentMember = memberService.getCurrentMember();

        Page<Record> recordPage;
        Pageable pageable = PageRequest.of(pageIndex, sizePerPage, Sort.Direction.DESC, "record_id");

        if(hasAdminRole(currentMember)) {
            recordPage = (topId == 0L) ?
                    recordRepository.findAll(pageable) :
                    recordRepository.findAllByTopId(topId, pageable);
        }
        else if(hasUserRole(currentMember)) {
            if(isTeamScopeAndTeamMember(currentMember, scope)) {
                recordPage = (topId == 0L) ?
                        recordRepository.findAllByTeam(
                                currentMember.getTeam().getId(), currentMember.getId(), pageable) :
                        recordRepository.findAllByTeamAndTopId(
                                currentMember.getTeam().getId(), currentMember.getId(), topId, pageable);
            }
            else {
                recordPage = (topId == 0L) ?
                        recordRepository.findAllByAuthor(currentMember.getId(), pageable) :
                        recordRepository.findAllByAuthorAndTopId(currentMember.getId(), topId, pageable);
            }
        }
        else {
            throw new OperationNotAllowedException();
        }

        return new RecordReadAllResponseDto(recordPage.getContent(), recordPage.getPageable(), recordPage.isLast());
    }

    @Override
    public RecordCreateResponseDto createRecord(RecordCreateRequestDto requestDto) {
        String title = requestDto.getTitle();
        if(recordRepository.existsByTitle(title)){
            throw new TitleAlreadyInUseException();
        }

        Record record = requestDto.toEntity(memberService.getCurrentMember());
        Long recordId = recordRepository.save(record).getId();

        String recordFileKey = createRecordFileKey(memberService.getCurrentMember().getId(), recordId);
        String preSignedURL = awsService.generatePresignedURL(recordFileKey, RECORD_BUCKET_NAME, FileType.MP4);

        record.updateRecordFileMetadata(recordFileKey, awsService.getObjectURL(recordFileKey, RECORD_BUCKET_NAME));

        return new RecordCreateResponseDto(record.getId(), preSignedURL);
    }

    @Override
    public void deleteRecord(Long id) {
        Member currentMember = memberService.getCurrentMember();
        Record record = recordRepository.findById(id)
                .orElseThrow(RecordNotExistsException::new);

        if (!ownsRecord(currentMember, record) && !hasAdminRole(currentMember)) {
            throw new OperationNotAllowedException();
        }

        awsService.deleteFile(record.getRecordFileKey(), RECORD_BUCKET_NAME);
        recordRepository.delete(record);
    }

    @Override
    @Transactional(readOnly = true)
    public RecordReadResponseDto readRecord(Long id){
        Member currentMember = memberService.getCurrentMember();
        Record record = recordRepository.findById(id)
                .orElseThrow(RecordNotExistsException::new);

        if (!ownsRecord(currentMember, record) && !hasAdminRole(currentMember)) {
            throw new OperationNotAllowedException();
        }

        return new RecordReadResponseDto(record);
    }

    @Override
    public RecordUpdateResponseDto updateRecord(Long id, RecordUpdateRequestDto requestDto) {
        Member currentMember = memberService.getCurrentMember();
        Record record = recordRepository.findById(id)
                .orElseThrow(RecordNotExistsException::new);

        if (!ownsRecord(currentMember, record) && !hasAdminRole(currentMember)) {
            throw new OperationNotAllowedException();
        }

        record.updateTitleAndDescription(requestDto.getTitle(), requestDto.getDescription());
        record.updateScope(ScopeType.of(requestDto.getScope()));
        String preSignedURL = awsService.generatePresignedURL(record.getRecordFileKey(), RECORD_BUCKET_NAME, FileType.MP4);

        return new RecordUpdateResponseDto(recordRepository.save(record).getId(), preSignedURL);
    }

    public String createRecordFileKey(Long memberId, Long recordId) {
        return RECORD_FILE_PREFIX + "_" + memberId + "_" + recordId + "." + FileType.MP4.getName();
    }

    public boolean hasAdminRole(Member currentMember) {
        return currentMember.getRoleType().equals(RoleType.ADMIN);
    }
    public boolean hasUserRole(Member currentMember) {
        return currentMember.getRoleType().equals(RoleType.USER);
    }

    public boolean ownsRecord(Member currentMember, Record record) {
        return record.getAuthor().equals(currentMember);
    }

    public boolean isTeamScopeAndTeamMember(Member currentMember, ScopeType scope) {
        return scope == ScopeType.TEAM && currentMember.getTeam() != null;
    }
}
