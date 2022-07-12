package swm.wbj.asyncrum.domain.userteam.member.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}
