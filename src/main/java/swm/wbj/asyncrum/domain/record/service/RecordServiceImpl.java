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
import swm.wbj.asyncrum.domain.record.repository.RecordRepository;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.entity.RoleType;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.global.media.FileType;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordServiceImpl implements RecordService{

    private final RecordRepository recordRepository;
    private final MemberService memberService;
    private final AwsService awsService;

    private static final String RECORD_BUCKET_NAME = "records";
    private static final String RECORD_FILE_PREFIX ="record";

    @Override
    @Transactional(readOnly = true)
    public RecordReadAllResponseDto readAllRecord(Integer pageIndex, Long topId) {
        int SIZE_PER_PAGE = 10;
        Page<Record> recordPage;
        Pageable pageable = PageRequest.of(pageIndex, SIZE_PER_PAGE, Sort.Direction.DESC, "record_id");

        Member member = memberService.getCurrentMember();
        RoleType memberRoleType = member.getRoleType();

        switch (memberRoleType) {
            case ADMIN:
                if(topId == 0) {
                    recordPage = recordRepository.findAll(pageable);
                }
                else {
                    recordPage = recordRepository.findAllByTopId(topId, pageable);
                }
                break;
            case USER:
                if(topId == 0) {
                    recordPage = recordRepository.findAllByAuthor(member.getId(),pageable);
                }
                else {
                    recordPage = recordRepository.findAllByAuthorAndTopId(member.getId(),topId, pageable);
                }
                break;
            case GUEST:
            default:
                throw new IllegalArgumentException("허용되지 않은 작업입니다.");
        }

        return new RecordReadAllResponseDto(recordPage.getContent(), recordPage.getPageable(), recordPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public RecordReadDailyResponseDto readDailyRecord(Long topId, Long teamId) {
        List<Record> recordPage;

        Member member = memberService.getCurrentMember();
        RoleType memberRoleType = member.getRoleType();
        switch (memberRoleType){

            case ADMIN:
            case USER:
                if(topId == 0) {
                    recordPage = recordRepository.findAll();
                }
                else {
                    recordPage = recordRepository.findAllByMoreThanTopId(topId, teamId);
                    recordPage.addAll(recordRepository.findAllByLessThanTopId(topId, teamId));
                }
                break;
            case GUEST:
            default:
                throw new IllegalArgumentException("허용되지 않은 작업입니다.");

        }
        return new RecordReadDailyResponseDto(recordPage);
    }

    @Override
    public RecordCreateResponseDto createRecord(RecordCreateRequestDto requestDto) throws IOException {
        String title = requestDto.getTitle();
        if(recordRepository.existsByTitle(title)){
            throw new IllegalArgumentException("해당 제목은 이미 사용중 입니다.");
        }
        Record record = requestDto.toEntity(memberService.getCurrentMember());
        Long recordId = recordRepository.save(record).getId();

        String recordFileKey = createRecordFileKey(memberService.getCurrentMember().getId(), recordId);

        String preSignedURL = awsService.generatePresignedURL(recordFileKey, RECORD_BUCKET_NAME, FileType.MP4);


        record.update(null, null, recordFileKey, awsService.getObjectURL(recordFileKey, RECORD_BUCKET_NAME), null);

        return new RecordCreateResponseDto(record.getId(), preSignedURL);
    }

    @Override
    public void deleteRecord(Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 녹화가 존재하지 않습니다. ")) ;
        awsService.deleteFile(record.getRecordFileKey(), RECORD_BUCKET_NAME);
        recordRepository.delete(record);
    }

    @Override
    @Transactional(readOnly = true)
    public RecordReadResponseDto readRecord(Long id){
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 녹화가 존재하지 않습니다."));

        return new RecordReadResponseDto(record);
    }

    @Override
    public RecordUpdateResponseDto updateRecord(Long id, RecordUpdateRequestDto requestDto) throws IOException {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 녹화가 존재하지 않습니다."));

        record.update(requestDto.getTitle(), requestDto.getDescription(),null,null, requestDto.getScope());
        String preSignedURL = awsService.generatePresignedURL(record.getRecordFileKey(), RECORD_BUCKET_NAME, FileType.MP4);
        return new RecordUpdateResponseDto(recordRepository.save(record).getId(), preSignedURL);
    }

    public String createRecordFileKey(Long memberId, Long recordId) {
        return RECORD_FILE_PREFIX + "_" + memberId + "_" + recordId + "." + FileType.MP4.getName();
    }
}
