package swm.wbj.asyncrum.domain.member;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.global.type.RoleType;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    static String email;
    static String password;
    static String fullname;
    static String profileImageUrl;

    @BeforeAll
    public static void setUpParam() {
        email = "testEmail";
        password = "testPassword";
        fullname = "testFullname";
        profileImageUrl = "testProfileImageUrl";
    }

    @BeforeEach
    public void setUpEntity() {
        Member member = Member.createMember()
                .email(email)
                .password(password)
                .fullname(fullname)
                .profileImageUrl(profileImageUrl)
                .build();

        memberRepository.save(member);
    }

    @Test
    @DisplayName("멤버 생성")
    public void createMember() {
        // given
        String createEmail = "testCreateEmail";

        MemberCreateRequestDto requestDto = new MemberCreateRequestDto();
        requestDto.setEmail(createEmail);
        requestDto.setPassword(password);
        requestDto.setFullname(fullname);

        // when
        MemberCreateResponseDto responseDto = memberService.createMember(requestDto);

        // then
        Member createdMember = memberRepository.findById(responseDto.getId()).orElseThrow();

        assertNotNull(createdMember);
        assertEquals(createdMember.getEmail(), createEmail);
        assertEquals(createdMember.getFullname(), fullname);
    }

    @Disabled("현재 멤버 정보가 담긴 JWT 필요")
    @Test
    @DisplayName("JWT로 현재 멤버 정보 가져오기")
    public void getCurrentMember() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("멤버 id나 이메일로 정보 가져오기")
    public void getUserByIdOrEmail() {
        // given

        // when

        // then
    }

    @Disabled("현재 멤버 정보가 담긴 JWT 필요")
    @Test
    @DisplayName("멤버 정보 가져오기")
    public void readMember() {
        // given
        Member member = Member.createMember()
                .email(email)
                .password(password)
                .fullname(fullname)
                .profileImageUrl(profileImageUrl)
                .build();

        memberRepository.save(member);

        // when
        MemberReadResponseDto responseDto = memberService.readMember(member.getId());

        // then
        assertEquals(responseDto.getId(), member.getId());
        assertEquals(responseDto.getFullname(), member.getFullname());
        assertEquals(responseDto.getProfileImageUrl(), member.getProfileImageUrl());
        assertEquals(responseDto.getEmail(), member.getEmail());
        assertEquals(responseDto.getRoleType(), member.getRoleType());
    }

    @Disabled("현재 멤버 정보가 담긴 JWT 필요")
    @Test
    @DisplayName("모든 멤버 정보 가져오기")
    public void readAllMember() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("멤버 업데이트")
    public void updateMember() {
        // given
        Long updateId = memberRepository.findByEmail(email).getId();
        String updateFullname = "updated testFullname";

        MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto();
        requestDto.setFullname(updateFullname);

        // when
        MemberUpdateResponseDto responseDto = memberService.updateMember(updateId, requestDto);

        // then
        Member updatedMember = memberRepository.findById(responseDto.getId()).orElseThrow();
        assertEquals(updatedMember.getId(), updateId);
        assertEquals(updatedMember.getFullname(), updateFullname);
    }

    @Test
    @DisplayName("멤버 삭제")
    public void deleteMember() {
        // given
        Long deleteId = memberRepository.findByEmail(email).getId();

        // when
        memberService.deleteMember(deleteId);

        // then
        assertNull(memberRepository.findById(deleteId).orElse(null));
    }

    @Disabled("현재 멤버 정보가 담긴 JWT 필요")
    @Test
    @DisplayName("이메일 인증 보내기")
    public void sendEmailVerificationLinkByEmail() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("이메일 인증 검증")
    void verifyEmailVerificationLink() throws Exception {
        // given
        Long updateId = memberRepository.findByEmail(email).getId();

        // when
        memberService.verifyEmailVerificationLink(updateId);

        // then
        Member member = memberRepository.findById(updateId).orElseThrow();
        assertEquals(member.getRoleType(), RoleType.USER);
    }

    @Test
    @DisplayName("멤버 프로필 이미지 생성")
    public void createImage() throws IOException {
        // given
        Long id = memberRepository.findByEmail(email).getId();

        // when
        ImageCreateResponseDto responseDto = memberService.createImage(id);

        // then
        assertEquals(responseDto.getId(), id);
        assertTrue(responseDto.getPreSignedURL().contains("https"));
        assertTrue(responseDto.getPreSignedURL().contains(id.toString()));
    }
}
