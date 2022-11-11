package swm.wbj.asyncrum.domain.meeting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.domain.team.entity.Team;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "meeting")
@Entity
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    @ManyToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    private Team team;

    @Column
    private String meetingName;

    @Column(nullable = false)
    private Boolean status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "participants",
            joinColumns = @JoinColumn(name = "meeting_id")
    )
    @Column(name = "participants")
    private List<String> participants = new ArrayList<>();

    @JsonIgnore
    @Column
    private String meetingFileKey;
    @Column
    private String meetingFileUrl;

    @Builder(builderMethodName = "createMeeting")
    public Meeting(Team team,
                   String meetingName
    ){
        this.team = team;
        this.meetingName = meetingName;
        this.status = true;
    }


    public void updateMeetingStatus(Boolean status){
        if(status != null) this.status = status;
    }

    public void updateMeetingFileMetadata(String meetingFileKey, String meetingFileUrl) {
        if(meetingFileKey != null) this.meetingFileKey = meetingFileKey;
        if(meetingFileUrl != null) this.meetingFileUrl = meetingFileUrl;
    }

    public void updateParticipants(List<String> participants){
        if (participants != null) this.participants = participants;
    }



}
