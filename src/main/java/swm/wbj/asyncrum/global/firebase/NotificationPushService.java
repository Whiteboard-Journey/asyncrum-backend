package swm.wbj.asyncrum.global.firebase;

import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import swm.wbj.asyncrum.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationPushService {

    @Value("${logo.url}")
    private String logoUrl;

    @Async
    public void sendToSingleClient(String title, String body, Member pushSubjectAccount) throws FirebaseMessagingException {
        // FCM 토큰이 존재하고 알림이 ON 인 경우에만 알림 보내기
        if(pushSubjectAccount.getFcmRegistrationToken() != null && pushSubjectAccount.getNotification()) {
            // 알림 객체 설정
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .setImage(logoUrl)
                    .build();

            // 메세지 객체 설정
            Message message = Message.builder()
                    .setNotification(notification)
                    .setToken(pushSubjectAccount.getFcmRegistrationToken())
                    .build();

            // 메세지 FCM 서버에 보내기
            String response = FirebaseMessaging.getInstance().send(message);

            // Response 받기
            System.out.println("Successfully sent message: " + response);
        }
    }

    @Async
    public void sendToMultipleClient(String title, String body, List<Member> pushSubjectAccounts) throws FirebaseMessagingException {
        // FCM 토큰이 존재하고 알림이 ON 인 유저들의 FCM 토큰만 가져오기
        List<String> fcmRegistrationTokens = pushSubjectAccounts.stream()
                .filter(account -> account.getFcmRegistrationToken() != null)
                .filter(Member::getNotification)
                .map(Member::getFcmRegistrationToken)
                .collect(Collectors.toList());

        // FCM 토큰 리스트가 비어 있지 않은 경우에만 알림 보내기
        if(!fcmRegistrationTokens.isEmpty()) {
            // 알림 객체 설정
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // 멀티캐스트 메세지 객체 설정
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .addAllTokens(fcmRegistrationTokens)
                    .build();

            // 메세지 FCM 서버에 보내기
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            // 성공적으로 보낸 메시지 출력
            System.out.println(response.getSuccessCount() + " messages were sent successfully");

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();

                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(fcmRegistrationTokens.get(i));
                    }
                }
                // 전송 실패한 토큰들 출력
                System.out.println("List of tokens that caused failures: " + failedTokens);
            }
        }
    }
}
