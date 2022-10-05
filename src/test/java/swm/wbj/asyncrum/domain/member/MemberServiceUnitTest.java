package swm.wbj.asyncrum.domain.member;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberCreateRequestDto;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberUpdateRequestDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.exeception.EmailAlreadyInUseException;
import swm.wbj.asyncrum.domain.userteam.member.exeception.MemberNotExistsException;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberServiceImpl;
import swm.wbj.asyncrum.global.mail.MailService;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.oauth.utils.TokenUtil;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.utils.UrlService;

import java.util.Optional;
import java.util.TimeZone;

public class MemberServiceUnitTest {

    MemberRepository memberRepository = Mockito.mock(MemberRepository.class);

    MailService mailService = Mockito.mock(MailService.class);

    UrlService urlService = Mockito.mock(UrlService.class);

    AwsService awsService = Mockito.mock(AwsService.class);

    static MockedStatic<TokenUtil> tokenUtil;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    MemberServiceImpl memberServiceImpl =
            new MemberServiceImpl(memberRepository, passwordEncoder, mailService, urlService, awsService);

    static Member mockMember;
    static final Long MOCK_MEMBER_ID = 1L;
    static final String MOCK_MEMBER_EMAIL = "test@test.com";

    @BeforeAll
    static void tearUp() {
        mockMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_MEMBER_ID;
            }

            @Override
            public String getEmail() {
                return MOCK_MEMBER_EMAIL;
            }

            @Override
            public RoleType getRoleType() {
                return RoleType.USER;
            }
        };

        tokenUtil = Mockito.mockStatic(TokenUtil.class);
    }

    @AfterAll
    static void afterClass() {
        tokenUtil.close();
    }

    @DisplayName("멤버 생성")
    @Test
    void createMember() {
        MemberCreateRequestDto dto = new MemberCreateRequestDto();
        dto.setEmail("test@test.com");
        dto.setFullname("qwer1234!");
        dto.setPassword("테스트");

        MemberCreateRequestDto dto2 = new MemberCreateRequestDto();
        dto2.setEmail("test2@test.com");
        dto2.setFullname("qwer1234!");
        dto2.setPassword("테스트 2");

        Mockito.when(memberRepository.existsByEmail("test@test.com")).thenReturn(true);
        Mockito.when(memberRepository.existsByEmail("test2@test.com")).thenReturn(false);

        // 이메일 중복되는 경우 테스트
        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> {
            memberServiceImpl.createMember(dto);
        });

        Mockito.when(memberRepository.save(Mockito.isA(Member.class))).thenReturn(mockMember);

        // 멤버를 저장하는 경우 테스트
        memberServiceImpl.createMember(dto2);

        Mockito.verify(memberRepository).save(Mockito.isA(Member.class));
    }

    @DisplayName("JWT로 현재 멤버 정보 가져오기")
    @Test
    void getCurrentMember() {
        Mockito.when(TokenUtil.getCurrentMemberId()).thenReturn(MOCK_MEMBER_ID);

        // 멤버가 존재하지 않는 경우 테스트
        Mockito.when(memberRepository.findById(MOCK_MEMBER_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(MemberNotExistsException.class, () -> {
            memberServiceImpl.getCurrentMember();
        });

        // 멤버가 존재하는 경우 테스트
        Mockito.when(memberRepository.findById(MOCK_MEMBER_ID)).thenReturn(java.util.Optional.of(mockMember));

        Assertions.assertEquals(memberServiceImpl.getCurrentMember(), mockMember);
    }

    @DisplayName("멤버 id나 이메일로 정보 가져오기")
    @Test
    void getUserByIdOrEmail() {
        // id가 존재하면 id로 조회하는지 테스트
        Mockito.when(memberRepository.findById(MOCK_MEMBER_ID)).thenReturn(java.util.Optional.of(mockMember));

        memberServiceImpl.getUserByIdOrEmail(MOCK_MEMBER_ID, null);
        Mockito.verify(memberRepository).findById(MOCK_MEMBER_ID);

        Assertions.assertEquals(memberServiceImpl.getUserByIdOrEmail(MOCK_MEMBER_ID, null), mockMember);


        // id가 존재하지 않으면 email로 조회
        Mockito.when(memberRepository.findByEmail(MOCK_MEMBER_EMAIL)).thenReturn(java.util.Optional.of(mockMember));

        memberServiceImpl.getUserByIdOrEmail(null, MOCK_MEMBER_EMAIL);
        Mockito.verify(memberRepository).findByEmail(MOCK_MEMBER_EMAIL);

        Assertions.assertEquals(memberServiceImpl.getUserByIdOrEmail(null, MOCK_MEMBER_EMAIL), mockMember);

        // 없으면 예외 발생
        Mockito.when(memberRepository.findById(MOCK_MEMBER_ID)).thenReturn(Optional.empty());
        Mockito.when(memberRepository.findByEmail(MOCK_MEMBER_EMAIL)).thenReturn(Optional.empty());

        Assertions.assertThrows(MemberNotExistsException.class, () -> {
            memberServiceImpl.getUserByIdOrEmail(MOCK_MEMBER_ID, null);
        });

        Assertions.assertThrows(MemberNotExistsException.class, () -> {
            memberServiceImpl.getUserByIdOrEmail(null, MOCK_MEMBER_EMAIL);
        });
    }

    @DisplayName("(WIP) 멤버 정보 가져오기")
    @Test
    void readMember() {
        Mockito.when(TokenUtil.getCurrentMemberId()).thenReturn(MOCK_MEMBER_ID);
        Mockito.when(memberRepository.findById(MOCK_MEMBER_ID)).thenReturn(java.util.Optional.of(mockMember));

        memberServiceImpl.readMember(MOCK_MEMBER_ID);

        // memberServiceImpl의 getCurrentMember()가 한 번 호출되었는지를 검증
    }

    @DisplayName("멤버 업데이트")
    @Test
    void updateMember() {
        String timezone = "Asia/Seoul";
        String fullname = "Updated Name";

        MemberUpdateRequestDto dto = new MemberUpdateRequestDto();
        dto.setTimezone(timezone);
        dto.setFullname(fullname);

        Mockito.when(TokenUtil.getCurrentMemberId()).thenReturn(MOCK_MEMBER_ID);
        Mockito.when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));

        memberServiceImpl.updateMember(null, dto);

        // memberServiceImpl의 getCurrentMember()가 한 번 호출되었는지를 검증

        // 엔티티를 호출하여 진행하는 update의 수행여부는 해당 함수의 호출이 아닌, 결과값으로 비교해야 할까?
        Assertions.assertEquals(mockMember.getTimezone(), TimeZone.getTimeZone(timezone));
        Assertions.assertEquals(mockMember.getFullname(), fullname);
    }

    @DisplayName("멤버 삭제")
    @Test
    void deleteMember() {
        Mockito.when(TokenUtil.getCurrentMemberId()).thenReturn(MOCK_MEMBER_ID);
        Mockito.when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));

        memberServiceImpl.deleteMember(MOCK_MEMBER_ID);

        Mockito.verify(memberRepository).delete(mockMember);
    }

    @DisplayName("이메일 인증 보내기")
    @Test
    void sendEmailVerificationLinkByEmail() throws Exception {
        String verificationLink = "Verification Link";

        Mockito.when(TokenUtil.getCurrentMemberId()).thenReturn(MOCK_MEMBER_ID);
        Mockito.when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        Mockito.when(urlService.buildURL(MemberServiceImpl.EMAIL_VERIFICATION_URL, "memberId", MOCK_MEMBER_ID)).thenReturn(verificationLink);

        memberServiceImpl.sendEmailVerificationLinkByEmail();

        Mockito.verify(urlService).buildURL(MemberServiceImpl.EMAIL_VERIFICATION_URL, "memberId", MOCK_MEMBER_ID);
        Mockito.verify(mailService).sendMailVerificationLink(MOCK_MEMBER_EMAIL, verificationLink);
    }

    @DisplayName("이메일 인증 검증")
    @Test
    void verifyEmailVerificationLink() {
        Member newMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_MEMBER_ID;
            }
        };

        Mockito.when(memberRepository.findById(MOCK_MEMBER_ID)).thenReturn(Optional.of(newMember));

        memberServiceImpl.verifyEmailVerificationLink(MOCK_MEMBER_ID);

        Assertions.assertEquals(newMember.getRoleType(), RoleType.USER);
    }

    @DisplayName("멤버 프로필 이미지 생성")
    @Test
    void createImage() {
        String imageFileKey = AwsService.IMAGE_MEMBER_FILE_PREFIX + "_" + MOCK_MEMBER_ID + "." + FileType.PNG.getName();
        String imageFileUrl = "Test Image Url";

        Mockito.when(TokenUtil.getCurrentMemberId()).thenReturn(MOCK_MEMBER_ID);
        Mockito.when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));

        Mockito.when(awsService.generatePresignedURL(Mockito.anyString(), Mockito.anyString(), Mockito.any(FileType.class))).thenReturn(imageFileUrl);
        Mockito.when(awsService.getObjectURL(Mockito.anyString(), Mockito.anyString())).thenReturn(imageFileUrl);

        memberServiceImpl.createImage(MOCK_MEMBER_ID);

        // createImageFileKey 를 호출했는지 검증
        Mockito.verify(awsService).generatePresignedURL(imageFileKey, AwsService.IMAGE_BUCKET_NAME, FileType.PNG);
        Mockito.verify(awsService).getObjectURL(imageFileKey, AwsService.IMAGE_BUCKET_NAME);
        Assertions.assertEquals(mockMember.getProfileImageFileKey(), imageFileKey);
        Assertions.assertEquals(mockMember.getProfileImageUrl(), imageFileUrl);
    }
}