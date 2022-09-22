package swm.wbj.asyncrum.domain.userteam.member.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberCreateResponseDtoTest {
    @Test
    public void MemberCreateResponseDtoTest(){
        //given
        Long id = 1L;

        //when
        MemberCreateResponseDto memberCreateResponseDto = new MemberCreateResponseDto(id);

        //then
        assertThat(memberCreateResponseDto.getId()).isEqualTo(id);
    }

}