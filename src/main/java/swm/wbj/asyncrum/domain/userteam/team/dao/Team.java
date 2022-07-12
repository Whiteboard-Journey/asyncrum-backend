package swm.wbj.asyncrum.domain.userteam.team.dao;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
