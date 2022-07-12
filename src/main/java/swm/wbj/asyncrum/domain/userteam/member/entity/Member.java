package swm.wbj.asyncrum.domain.userteam.member.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(value= AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "member")
public class Member{

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String email;

    private String pictureUrl;

    private String phone;

    private String nickname;

    public void update(String name, String email, String pictureUrl, String phone, String nickname){
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.phone = phone;
        this.nickname = nickname;
    }
}
