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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3 Presigned URL 생성
     */
    public String generatePresignedURL(String uploadFileKey, String dirName, FileType fileType) {
        try {
            // Set the presigned URL to expire after 2 mins
            Date expiration = new Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 2;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL Request
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucket, buildFileName(uploadFileKey, dirName))
                            .withContentType(fileType.getContentType())
                            .withMethod(HttpMethod.PUT)
                            .withExpiration(expiration);

            // Add ACL
            generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

            // Generate the presigned URL
            URL preSignedURL = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

            log.info("Pre-Signed URL: " + preSignedURL.toString());

            return preSignedURL.toString();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }

        return null;
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
