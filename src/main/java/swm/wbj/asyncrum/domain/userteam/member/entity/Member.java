package swm.wbj.asyncrum.domain.userteam.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.type.ScopeType;


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
    private String oauthId;

    @JsonIgnore
    @Column(name = "password", length = 128)
    private String password;

    @Column(name = "fullname", length = 100)
    @Size(max = 100)
    private String fullname;

    @Column(name = "nickname", length = 100)
    @Size(max = 100)
    private String nickname;

    @Column(name = "profile_image_url", length = 512)
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
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Record> records = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Whiteboard> whiteboards = new ArrayList<>();

    @Column(name = "provider_type", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Builder(builderMethodName = "createMember")
    public Member(
            @NotNull @Size(max = 512) String email,
            @Size(max = 64) String oauthId,
            @Size(max = 128) String password,
            @Size(max = 100) String fullname,
            @Size(max = 100) String nickname,
            @Size(max = 512) String profileImageUrl,
            @NotNull RoleType roleType,
            @NotNull ProviderType providerType
    ) {
        this.email = email;
        this.oauthId = oauthId;
        this.password = password;
        this.fullname = fullname;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.roleType = roleType != null ? roleType : RoleType.GUEST;
        this.providerType = providerType != null ? providerType : ProviderType.LOCAL;
    }

    public void update(String fullname, String profileImageUrl) {
        if(fullname != null ) this.fullname = fullname;
        if(profileImageUrl != null ) this.profileImageUrl = profileImageUrl;
    }

    public void updateRole(RoleType roleType) {
        this.roleType = roleType;
    }

    public void updateTeam(Team team) {
        this.team = team;
    }
}
