package swm.wbj.asyncrum.domain.member;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberCreateRequestDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.exeception.EmailAlreadyInUseException;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberServiceImpl;
import swm.wbj.asyncrum.global.mail.MailService;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.utils.UrlService;

public class MemberServiceUnitTest {

    @Mock
    MemberRepository memberRepository = Mockito.mock(MemberRepository.class);

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    MailService mailService;

    @Mock
    UrlService urlService;

    @Mock
    AwsService awsService;

    @InjectMocks
    MemberServiceImpl memberServiceImpl = new MemberServiceImpl(memberRepository, passwordEncoder, mailService, urlService, awsService);

    @Test
    void 멤버생성테스트() {
        MemberCreateRequestDto dto = MemberCreateRequestDto.builder().email("test@test.com").password("qwer1234!").fullname("테스트").build();

        MemberCreateRequestDto dto2 = MemberCreateRequestDto.builder().email("newUser@test.com").password("qwer1234!").fullname("테스터").build();

        //Mockito.verify(memberRepository).existsByEmail("test@test.com");
        Mockito.when(memberRepository.existsByEmail("test@test.com")).thenReturn(true);
        Mockito.when(memberRepository.existsByEmail("newUser@test.com")).thenReturn(false);

        // 이메일 중복되는 경우 테스트
        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> {
            memberServiceImpl.createMember(dto);
        });


        Member mockMember = new Member() {
            @Override
            public Long getId() {
                return 5L;
            }
        };


        Mockito.when(memberRepository.save(Mockito.isA(Member.class))).thenReturn(mockMember);

        memberServiceImpl.createMember(dto2);
        Mockito.verify(memberRepository).save(Mockito.isA(Member.class));
    }
}
