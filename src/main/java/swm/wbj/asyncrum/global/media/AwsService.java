package swm.wbj.asyncrum.global.media;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swm.wbj.asyncrum.global.type.FileType;

import java.net.URL;
import java.time.Instant;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsService {

    private final AmazonS3Client amazonS3Client;

    public static final String IMAGE_BUCKET_NAME = "images";
    public static final String IMAGE_MEMBER_FILE_PREFIX = "member_image";
    public static final String IMAGE_TEAM_FILE_PREFIX ="team_image";

    public static final String RECORD_BUCKET_NAME = "records";
    public static final String RECORD_FILE_PREFIX ="record";

    public static final String WHITEBOARD_BUCKET_NAME = "whiteboards";
    public static final String WHITEBOARD_FILE_PREFIX ="whiteboard";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3 Presigned URL 생성
     */
    public String generatePresignedURL(String uploadFileKey, String dirName, FileType fileType) {
        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = generatePresignedUrlRequest(uploadFileKey, dirName, fileType);
            URL preSignedURL = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

            return preSignedURL.toString();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Presigned URL Request 생성
     */
    private GeneratePresignedUrlRequest generatePresignedUrlRequest(String uploadFileKey, String dirName, FileType fileType) {
        // 유효 기간 설정
        Date expiration = getExpiration();

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, buildFileName(uploadFileKey, dirName))
                        .withContentType(fileType.getMimeType())
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        // 별도로 ACL 파라미터도 추가
        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

        return generatePresignedUrlRequest;
    }

    /**
     * Presigned URL의 유효 기간 설정: 2분
     */
    private Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    /**
     * S3 Object (파일) 삭제
     */
    public void deleteFile(String uploadFileKey, String dirName) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, buildFileName(uploadFileKey, dirName));
        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * S3 Object URL 조회
     */
    public String getObjectURL(String uploadFileKey, String dirName) {
        return amazonS3Client.getUrl(bucket, buildFileName(uploadFileKey, dirName)).toString();
    }

    /**
     * S3 Object Key (FileName) 생성
     */
    public String buildFileName(String uploadFileKey, String dirName) {
        return dirName + "/" + uploadFileKey;
    }
}
