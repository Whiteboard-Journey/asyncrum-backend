package swm.wbj.asyncrum.domain.team.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import swm.wbj.asyncrum.domain.team.meeting.entity.Meeting;
import swm.wbj.asyncrum.domain.teammember.entity.TeamMember;
import swm.wbj.asyncrum.global.entity.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@Table(name = "team")
@Entity
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String code;

    @JsonIgnore
    @Column
    private String profileImageFileKey;

    @Column
    private String profileImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "openMeeting",
            joinColumns = @JoinColumn(name = "team_id")
    )
    @Column(name = "open_meeting_name")
    private Set<String> openMeeting = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TeamMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Meeting> meetings = new ArrayList<>();


    @Builder
    public Team(String name, String code, String profileImageUrl) {
        this.name = name;
        this.code = code;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateName(String name) {
        if(name != null) this.name = name;
    }

    public void updateProfileImage(String profileImageFileKey, String profileImageUrl) {
        if(profileImageFileKey != null) this.profileImageFileKey = profileImageFileKey;
        if(profileImageUrl != null ) this.profileImageUrl = profileImageUrl;
    }

    public void addMember(TeamMember member) {
        this.members.add(member);
    }

    public void removeMember(TeamMember member) {
        this.members.remove(member);
    }

    public void updateOpenMeeting(Set<String> openMeeting){
        if (openMeeting != null) this.openMeeting = openMeeting;
    }
}
