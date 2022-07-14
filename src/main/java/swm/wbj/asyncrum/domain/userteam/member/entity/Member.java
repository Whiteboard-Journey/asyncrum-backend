package swm.wbj.asyncrum.domain.userteam.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.oauth.entity.RoleType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO: 추후 SEQUENCE 형태로 변경
    private Long id;

    private String username;

    private String email;

    private String pictureUrl;

    private String phone;

    private String nickname;

    @JsonIgnore
    @Column(length = 128)
    // @NotNull
    // TODO: 추후 서비스 내 회원가입 관련 추가
    private String password;

    // OAuth 관련 추가
    @Column(length = 64, unique = true)
    @NotNull
    private String userId;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Record> records = new ArrayList<>();

    public void update(String phone, String nickname){
        this.phone = phone;
        this.nickname = nickname;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updatePictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
