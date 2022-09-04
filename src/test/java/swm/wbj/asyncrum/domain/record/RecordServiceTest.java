package swm.wbj.asyncrum.domain.record;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.record.dto.*;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.record.repository.RecordRepository;
import swm.wbj.asyncrum.domain.record.service.RecordService;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class RecordServiceTest {

    @Autowired RecordService recordService;
    @Autowired RecordRepository recordRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager entityManager;

    final static String title = "title";
    final static String description = "description";
    final static ScopeType privateScope = ScopeType.PRIVATE;
    final static String CURRENT_MEMBER_ID = "1";
    final static Long CURRENT_RECORD_ID = 1L;
    final static String CURRENT_MEMBER_ROLE_TYPE = "USER";

    @BeforeEach
    public void setUpEntity() throws IOException {
        Member member = Member.createMember()
                .email("email")
                .password("password")
                .fullname("fullname")
                .profileImageUrl("profileImageUrl")
                .roleType(RoleType.USER)
                .build();

        memberRepository.save(member);

//        Record record = Record.createRecord()
//                .title(title)
//                .description(description)
//                .scope(privateScope)
//                .author(member)
//                .build();

//        recordRepository.save(record);

        RecordCreateRequestDto requestDto = new RecordCreateRequestDto();
        requestDto.setTitle(title);
        requestDto.setDescription(description);
        requestDto.setScope(privateScope.getScope());

        recordService.createRecord(requestDto);
    }

    @AfterEach
    public void tearDown() {
        memberRepository.deleteAll();
        this.entityManager
                .createNativeQuery("ALTER TABLE member ALTER COLUMN `member_id` RESTART WITH 1")
                .executeUpdate();

        recordRepository.deleteAll();
        this.entityManager
                .createNativeQuery("ALTER TABLE record ALTER COLUMN `record_id` RESTART WITH 1")
                .executeUpdate();
    }

    @DisplayName("새 녹화본 생성")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void createRecord() throws IOException {
        // given
        String title = "title2";
        String description = "description2";

        RecordCreateRequestDto requestDto = new RecordCreateRequestDto();
        requestDto.setTitle(title);
        requestDto.setDescription(description);
        requestDto.setScope(privateScope.getScope());

        // when
        RecordCreateResponseDto responseDto = recordService.createRecord(requestDto);

        // then
        Record createdRecord = recordRepository.findById(responseDto.getId()).orElseThrow();
        assertNotNull(createdRecord);
        assertEquals(createdRecord.getTitle(), title);
        assertEquals(createdRecord.getDescription(), description);
        assertEquals(createdRecord.getScope(), ScopeType.PRIVATE);
        assertEquals(createdRecord.getAuthor().getId(), Long.parseLong(CURRENT_MEMBER_ID));
        assertTrue(createdRecord.getRecordFileKey().contains(CURRENT_MEMBER_ID));
    }

    @DisplayName("단일 녹화본 가져오기")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void readRecord() throws Exception {
        // given
        Member member = memberRepository.findById(Long.parseLong(CURRENT_MEMBER_ID)).orElseThrow();

        // when
        RecordReadResponseDto responseDto = recordService.readRecord(CURRENT_RECORD_ID);

        // then
        assertEquals(responseDto.getTitle(), title);
        assertEquals(responseDto.getDescription(), description);
        assertEquals(responseDto.getScope(), privateScope);
        assertTrue(responseDto.getRecordUrl().contains(CURRENT_RECORD_ID.toString()));

    }

    @DisplayName("본인이 녹화한 녹화본 리스트 가져오기")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void readAllRecord() {
        // given
        Integer pageIndex = 0;
        Long topId = 0L;
        int RECORD_LIST_SIZE = 1;

        // when
        RecordReadAllResponseDto responseDto = recordService.readAllRecord(privateScope, pageIndex, topId);

        // then
        assertEquals(responseDto.getRecords().size(), RECORD_LIST_SIZE);

        Record record = responseDto.getRecords().get(0);
        assertNotNull(record);
        assertEquals(record.getTitle(), title);
        assertEquals(record.getDescription(), description);
        assertEquals(record.getScope(), privateScope);
        assertTrue(record.getRecordFileKey().contains(CURRENT_RECORD_ID.toString()));
    }

    @DisplayName("녹화본 정보 업데이트")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void updateRecord() throws IOException {
        // given
        String updatedTitle = "update title";
        String updatedScope = "team";

        RecordUpdateRequestDto requestDto = new RecordUpdateRequestDto();
        requestDto.setTitle(updatedTitle);
        requestDto.setDescription(null);
        requestDto.setScope(updatedScope);

        // when
        RecordUpdateResponseDto responseDto = recordService.updateRecord(CURRENT_RECORD_ID, requestDto);

        // then
        Record updatedRecord = recordRepository.findById(responseDto.getId()).orElseThrow();
        assertEquals(updatedRecord.getTitle(), updatedTitle);
        assertNotNull(updatedRecord.getDescription());
        assertEquals(updatedRecord.getScope(), ScopeType.of(updatedScope));
    }

    @DisplayName("녹화본 삭제")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void deleteRecord() {
        // given
        // CURRENT_RECORD_ID

        // when
        recordService.deleteRecord(CURRENT_RECORD_ID);

        // then
        assertNull(recordRepository.findById(CURRENT_RECORD_ID).orElse(null));
    }
}
