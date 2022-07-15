package swm.wbj.asyncrum.domain.userteam.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.oauth.entity.RoleType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "member")
public class Member extends BaseEntity {

    /**
     * Member Primary Key
     */
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Member Primary Key for Local login
     */
    @Column(name = "email", length = 512, unique = true)
    @NotNull
    @Size(max = 512)
    private String email;

    /**
     * Member Primary Key for OAuth login
     */
    @Column(name = "user_id", length = 64, unique = true)
    @NotNull
    private String oauthId;

    @JsonIgnore
    @Column(name = "password", length = 128)
    @NotNull
    private String password;

    @Column(name = "nickname", length = 100)
    @NotNull
    @Size(max = 100)
    private String nickname;

    @Column(name = "profile_image_url", length = 512)
    @NotNull
    @Size(max = 512)
    private String profileImageUrl;

    @Column(name = "role_type", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Record> records = new ArrayList<>();

    @Column(name = "provider_type", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Column(name = "username", length = 100)
    @NotNull
    @Size(max = 100)
    private String username;

    @Builder
    public Member(
            @NotNull @Size(max = 512) String email,
            @NotNull @Size(max = 64) String oauthId,
            @NotNull @Size(max = 128) String password,
            @NotNull @Size(max = 100) String username,
            @NotNull @Size(max = 100) String nickname,
            @NotNull @Size(max = 512) String profileImageUrl,
            @NotNull RoleType roleType,
            @NotNull ProviderType providerType
    ) {
        this.email = email;
        this.oauthId = oauthId != null ? oauthId : "NO_OAUTH_ID";
        this.password = password != null ? password : "NO_PASSWORD";
        this.username = username != null ? username : "NO_USERNAME";
        this.nickname = nickname != null ? nickname : "NO_NICKNAME";
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl : "NO_PROFILE_IMAGE_URL";
        this.roleType = roleType != null ? roleType : RoleType.GUEST;
        this.providerType = providerType != null ? providerType : ProviderType.LOCAL;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateNickname(String nickname) { this.nickname = nickname; }
}
