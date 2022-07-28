package swm.wbj.asyncrum.domain.record.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.record.dto.RecordReadDailyResponseDto;
import swm.wbj.asyncrum.domain.record.entity.Record;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findAll();

    @Query(
            value = "SELECT * FROM record AS r WHERE r.record_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE r.record_id <= :topId",
            nativeQuery = true
    )
    Page<Record> findAllByTopId(
            @Param("topId") Long topId,
            Pageable pageable
    );

    // 팀에 속한 멤버들의 레코드 리스트 반환 (특정 아이디보다 큰 값들만 반환)
    @Query(
            value = "select * from record as r where r.member_id in (select member_id from member as m where m.team_id = :teamId) and (r.record_id > :topId)",
            nativeQuery = true
    )
    List<Record> findAllByMoreThanTopId(
            @Param("topId") Long topId,
            @Param("teamId") Long teamId

    );


    @Query(
            value = "select * from record as r where r.member_id in (select member_id from member as m where m.team_id = :teamId) and (r.record_id <= :topId)",
            nativeQuery = true
    )
    List<Record> findAllByLessThanTopId(
            @Param("topId") Long topId,
            @Param("teamId") Long teamId
    );



    @Query(
            value = "SELECT * FROM record AS r WHERE r.member_id = :memberId",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE r.member_id = :memberId",
            nativeQuery = true
    )
    Page<Record> findAllByAuthor(
            @Param("memberId") Long memberId,
            Pageable pageable
    );


    @Query(
            value = "SELECT * FROM record AS r WHERE r.member_id = :memberId AND r.record_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE r.member_id = :memberId AND r.record_id <= :topId",
            nativeQuery = true
    )
    Page<Record> findAllByAuthorAndTopId(
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );



    Boolean existsByTitle(String title);
}
