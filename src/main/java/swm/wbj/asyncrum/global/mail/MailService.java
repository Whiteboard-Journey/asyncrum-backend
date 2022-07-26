package swm.wbj.asyncrum.global.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final UrlService urlService;

    /**
     * 회원가입 이후 메일 인증 링크 전송
     */
    public void sendMailVerificationLink() throws Exception {

    }

    /**
     * 팀원 초대 메일 링크 전송
     */
    public void sendTeamMemberInvitationLink() throws Exception {

    }

    /**
     * 메일 메시지 작성
     */
    public MimeMessage createMailMessage(String to, String link) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to); //보내는 대상
        message.setSubject("Asyncrum 메일 인증 링크"); //제목

        String verificationMessage="";
        verificationMessage += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 10px 0 10px;\"><img src=\"https://avatars.githubusercontent.com/u/108519207?s=400&u=ff3dba1bdb8a1d521924daf8b76c98aee059025f&v=4\" width=\"60px\" height=\"60px\" loading=\"lazy\"></div>";
        verificationMessage += "<div style=\"border-bottom: 0.5px solid gray; padding-right: 30px; padding-left: 30px; margin: 10px 30px 10px 30px;\"></div>";
        verificationMessage += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">메일 인증 링크</h1>";
        verificationMessage += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 인증 링크를 눌러 메일 인증을 완료하세요.</p>";
        verificationMessage += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 10px 0 10px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        verificationMessage += link;
        verificationMessage += "</td></tr></tbody></table></div>";
        verificationMessage += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">만약, 본인이 요청한 것이 아니라면 본 이메일을 무시하십시오.</p><br/>";
        verificationMessage += "<div style=\"border-bottom: 0.5px solid gray; padding-right: 30px; padding-left: 30px; margin: 10px 30px 10px 30px;\"></div>";
        verificationMessage += "<p style=\"font-size: 13px; color: gray; padding-right: 30px; padding-left: 30px;\">본 이메일은 발신전용입니다.</p>";
        verificationMessage += "<p style=\"font-size: 13px; color: gray; padding-right: 30px; padding-left: 30px;\">©Asyncrum. All Rights Reserved.</p><br/>";

        message.setText(verificationMessage, "utf-8", "html"); //내용
        message.setFrom(new InternetAddress("asyncrum@gmail.com","Asyncrum")); //보내는 사람

        return message;
    }
}
