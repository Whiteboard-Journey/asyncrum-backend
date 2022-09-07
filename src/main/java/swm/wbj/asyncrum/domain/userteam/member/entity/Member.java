package swm.wbj.asyncrum.domain.userteam.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.type.RoleType;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@Entity
public class Member extends BaseEntity {

    /**
     * Member Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    /**
     * Member Primary Key for Local login
     */
    @NotNull
    @Size(max = 512)
    @Column(name = "email", length = 512, unique = true)
    private String email;

    /**
     * Member Primary Key for OAuth login
     */
    @Column(name = "user_id", length = 64, unique = true)
    private String oauthId;

    @JsonIgnore
    @Column(name = "password", length = 128)
    private String password;

    @Size(max = 100)
    @Column(name = "fullname", length = 100)
    private String fullname;

    @JsonIgnore
    @Column
    private String profileImageFileKey;

    @Size(max = 512)
    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 20)
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", length = 20)
    private ProviderType providerType;

    @Builder(builderMethodName = "createMember")
    public Member(
            @NotNull @Size(max = 512) String email,
            @Size(max = 64) String oauthId,
            @Size(max = 128) String password,
            @Size(max = 100) String fullname,
            @NotNull RoleType roleType,
            @NotNull ProviderType providerType,
            @NotNull @Size(max = 512) String profileImageUrl
    ) {
        this.email = email;
        this.oauthId = oauthId;
        this.password = password;
        this.fullname = fullname;
        this.roleType = roleType != null ? roleType : RoleType.GUEST;
        this.providerType = providerType != null ? providerType : ProviderType.LOCAL;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateFullname(String fullname) {
        if(fullname != null) this.fullname = fullname;
    }

    public void updateProfileImage(String profileImageFileKey, String profileImageUrl) {
        if(profileImageFileKey != null) this.profileImageFileKey = profileImageFileKey;
        if(profileImageUrl != null ) this.profileImageUrl = profileImageUrl;
    }

    public void updateRole(RoleType roleType) {
        this.roleType = roleType;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.addMember(this);
    }

    public void leaveTeam() {
        this.team = null;
    }
}
