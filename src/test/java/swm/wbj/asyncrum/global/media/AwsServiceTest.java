package swm.wbj.asyncrum.global.media;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import swm.wbj.asyncrum.global.type.FileType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SpringBoot 통합 테스트: AWS S3
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AwsServiceTest {

    @Autowired AwsService awsService;

    @Test
    public void S3_Presigned_URL_생성() {
        // given
        String uploadFileKey = "testFile";
        String dirName = "testDir";
        FileType fileType = FileType.ANY;

        // when
        String preSignedUrl = awsService.generatePresignedURL(uploadFileKey, dirName, fileType);

        // then
        assertNotNull(preSignedUrl);
        assertTrue(preSignedUrl.contains("https"));
        assertTrue(preSignedUrl.contains(uploadFileKey));
        assertTrue(preSignedUrl.contains(dirName));
    }
}
