package swm.wbj.asyncrum.domain.userteam.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swm.wbj.asyncrum.domain.userteam.member.entity.MemberRefreshToken;

public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

    MemberRefreshToken findByMemberId(String memberId);
    MemberRefreshToken findByMemberIdAndRefreshToken(String memberId, String refreshToken);

}