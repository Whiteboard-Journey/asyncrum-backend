package swm.wbj.asyncrum.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import swm.wbj.asyncrum.domain.teammember.entity.TeamMember;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.type.RoleType;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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

    @NotNull
    @Column
    private TimeZone timezone;

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
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TeamMember> teams = new ArrayList<>();

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
        this.timezone = TimeZone.getDefault();
    }

    public void updateFullname(String fullname) {
        if(fullname != null) this.fullname = fullname;
    }

    public void updateProfileImage(String profileImageFileKey, String profileImageUrl) {
        if(profileImageFileKey != null) this.profileImageFileKey = profileImageFileKey;
        if(profileImageUrl != null ) this.profileImageUrl = profileImageUrl;
    }

    public void updateTimeZone(TimeZone timezone) {
        if (timezone != null) this.timezone = timezone;
    }

    public void updateRole(RoleType roleType) {
        this.roleType = roleType;
    }

    public void addTeam(TeamMember team) {
        this.teams.add(team);
    }

    public void leaveTeam(TeamMember team) {
        this.teams.remove(team);
    }
}
