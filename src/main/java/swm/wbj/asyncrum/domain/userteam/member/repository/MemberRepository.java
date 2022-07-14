package swm.wbj.asyncrum.domain.userteam.member.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findAll(Pageable pageable);

    @Query(
            value = "SELECT * FROM member AS m WHERE m.id <= :topId",
            countQuery = "SELECT COUNT(*) FROM member AS m WHERE m.id <= :topId",
            nativeQuery = true
    )
    Page<Member> findAllByTopId(
            @Param("topId") Long topId,
            Pageable pageable
    );
}
