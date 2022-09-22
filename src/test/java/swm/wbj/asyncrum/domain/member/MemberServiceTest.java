package swm.wbj.asyncrum.domain.member;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.type.RoleType;

import javax.persistence.EntityManager;

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
    @Autowired
    EntityManager entityManager;

    static String email;
    static String password;
    static String fullname;
    static String profileImageUrl;
    final static String CURRENT_MEMBER_ID = "1";
    final static String CURRENT_MEMBER_ROLE_TYPE = "USER";

    @BeforeAll
    public static void setUpParam() {
        email = "email";
        password = "password";
        fullname = "fullname";
        profileImageUrl = "profileImageUrl";
    }

    @BeforeEach
    public void setUpEntity() {
        Member member = Member.createMember()
                .email(email)
                .password(password)
                .fullname(fullname)
                .profileImageUrl(profileImageUrl)
                .roleType(RoleType.USER)
                .build();

        memberRepository.save(member);
    }

    @AfterEach
    public void tearDown() {
        memberRepository.deleteAll();
        this.entityManager
                .createNativeQuery("ALTER TABLE member ALTER COLUMN `member_id` RESTART WITH 1")
                .executeUpdate();
    }

    @DisplayName("멤버 생성")
    @Test
    public void createMember() {
        // given
        String createEmail = "email2";
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

    @DisplayName("JWT로 현재 멤버 정보 가져오기")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void getCurrentMember() {
        // given
        Long memberId = Long.parseLong(CURRENT_MEMBER_ID);
        Member member = memberRepository.findById(memberId).orElseThrow();

        // when
        Member currentMember = memberService.getCurrentMember();

        // then
        assertNotNull(currentMember);
        assertEquals(currentMember.getId(), Long.parseLong(CURRENT_MEMBER_ID));
        assertEquals(currentMember.getEmail(), member.getEmail());
        assertEquals(currentMember.getFullname(), member.getFullname());
        assertEquals(currentMember.getRoleType(), member.getRoleType());
    }

    @Test
    @DisplayName("멤버 id나 이메일로 정보 가져오기")
    public void getUserByIdOrEmail() {
        // given
        Long memberId = Long.parseLong(CURRENT_MEMBER_ID);
        Member member = memberRepository.findById(memberId).orElseThrow();

        // when
        Member findMember = memberService.getUserByIdOrEmail(memberId, email);

        // then
        assertNotNull(findMember);
        assertEquals(findMember.getEmail(), member.getEmail());
        assertEquals(findMember.getFullname(), member.getFullname());
    }

    @DisplayName("멤버 정보 가져오기")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void readMember() {
        // given
        Long memberId = Long.parseLong(CURRENT_MEMBER_ID);
        Member member = memberRepository.findById(memberId).orElseThrow();

        // when
        MemberReadResponseDto responseDto = memberService.readMember(memberId);

        // then
        assertEquals(responseDto.getId(), member.getId());
        assertEquals(responseDto.getFullname(), member.getFullname());
        assertEquals(responseDto.getProfileImageUrl(), member.getProfileImageUrl());
        assertEquals(responseDto.getEmail(), member.getEmail());
        assertEquals(responseDto.getRoleType(), member.getRoleType());
    }

    @DisplayName("모든 멤버 정보 가져오기 (ADMIN ONLY)")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void readAllMember() {
        // given
        // @WithMockUser

        // when
        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class,
                () -> memberService.readAllMember(0, 0L, 0));

        // then
        assertEquals(exception.getMessage(), "허용되지 않은 작업입니다.");
    }

    @DisplayName("멤버 업데이트")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void updateMember() {
        // given
        Long updateId = Long.parseLong(CURRENT_MEMBER_ID);
        String updateFullname = "updated fullname";

        MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto();
        requestDto.setFullname(updateFullname);

        // when
        MemberUpdateResponseDto responseDto = memberService.updateMember(updateId, requestDto);

        // then
        Member updatedMember = memberRepository.findById(responseDto.getId()).orElseThrow();
        assertEquals(updatedMember.getId(), updateId);
        assertEquals(updatedMember.getFullname(), updateFullname);
    }

    @DisplayName("멤버 삭제")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void deleteMember() {
        // given
        Long deleteId = Long.parseLong(CURRENT_MEMBER_ID);

        // when
        memberService.deleteMember(deleteId);

        // then
        assertNull(memberRepository.findById(deleteId).orElse(null));
    }

    @Disabled("EmailService와의 연계 필요")
    @Test
    @DisplayName("이메일 인증 보내기")
    public void sendEmailVerificationLinkByEmail() throws Exception { }

    @DisplayName("이메일 인증 검증")
    @Test
    void verifyEmailVerificationLink() throws Exception {
        // given
        Member member2 = Member.createMember()
                .email("email2")
                .password(password)
                .fullname(fullname)
                .profileImageUrl(profileImageUrl)
                .build();

        memberRepository.save(member2);

        // when
        memberService.verifyEmailVerificationLink(member2.getId());

        // then
        assertEquals(member2.getRoleType(), RoleType.USER);
    }

    @DisplayName("멤버 프로필 이미지 생성")
    @WithMockUser(username = CURRENT_MEMBER_ID, roles = CURRENT_MEMBER_ROLE_TYPE)
    @Test
    public void createImage() throws IOException {
        // given
        Long memberId = Long.parseLong(CURRENT_MEMBER_ID);
        Member member = memberRepository.findById(memberId).orElseThrow();

        // when
        ImageCreateResponseDto responseDto = memberService.createImage(member.getId());

        // then
        assertEquals(responseDto.getId(), memberId);
        assertTrue(responseDto.getPreSignedURL().contains("https"));
        assertTrue(responseDto.getPreSignedURL().contains(memberId.toString()));
    }
}
