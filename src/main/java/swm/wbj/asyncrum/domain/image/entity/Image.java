package swm.wbj.asyncrum.domain.image.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id")
    private Long id;

    @Column
    @JsonIgnore
    private String imageFileKey;

    @Column
    private String imageFileUrl;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private ScopeType scope;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id")
    private Member author;

    @Builder(builderMethodName = "createImage")
    public Image(String title, String description, ScopeType scope, Member author) {
        this.title = title;
        this.description = description;
        this.scope = scope;
        this.author = author;
    }

    public void update(String title, String description, String imageFileKey, String imageFileUrl, ScopeType scope) {
        if(title != null ) this.title = title;
        if(description != null ) this.description = description;
        if(imageFileKey != null ) this.imageFileKey = imageFileKey;
        if(imageFileUrl != null ) this.imageFileUrl = imageFileUrl;
        if(scope != null ) this.scope = scope;
    }

}
