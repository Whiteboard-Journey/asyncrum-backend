package swm.wbj.asyncrum.domain.userteam.team.entity;

import lombok.*;
import swm.wbj.asyncrum.global.entity.BaseEntity;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;

    @Column
    String name;

    @Column(unique = true)
    String code;

    @Column
    String pictureUrl;

    @Builder
    public Team(String name, String code, String pictureUrl) {
        this.name = name;
        this.code = code;
        this.pictureUrl = pictureUrl;
    }

    public void update(String name, String pictureUrl) {
        this.name = name;
        this.pictureUrl = pictureUrl;
    }
}
