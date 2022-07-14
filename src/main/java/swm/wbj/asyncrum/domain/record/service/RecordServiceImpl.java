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
import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordServiceImpl implements RecordService{
    private final RecordRepository recordRepository;

    @Override
    @Transactional(readOnly = true)
    public RecordReadAllResponseDto readAllRecord(Integer pageIndex, Long topId) {
        int SIZE_PER_PAGE = 10;
        Page<Record> recordPage;
        Pageable pageable = PageRequest.of(pageIndex, SIZE_PER_PAGE, Sort.Direction.DESC, "id");
        if(topId == 0) {
            recordPage = recordRepository.findAll(pageable);
        }
        else {
            recordPage = recordRepository.findAllByTopId(topId, pageable);
        }

        return new RecordReadAllResponseDto(recordPage.getContent(), recordPage.getPageable(), recordPage.isLast());
    }

    @Override
    public RecordCreateResponseDto createRecord(RecordCreateRequestDto requestDto) {
        Record record = requestDto.toEntity();
        return new RecordCreateResponseDto(recordRepository.save(record).getId());
    }

    @Override
    public void deleteRecord(Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 녹화가 존재하지 않습니다. ")) ;
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
    public RecordUpdateResponseDto updateRecord(Long id, RecordUpdateRequestDto requestDto) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 녹화가 존재하지 않습니다."));

        record.update(requestDto.getTitle(), requestDto.getDescription(), requestDto.getDescription());
        return new RecordUpdateResponseDto(recordRepository.save(record).getId());
    }
}
