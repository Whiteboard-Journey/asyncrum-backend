package swm.wbj.asyncrum.global.media;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    public String generatePresignedURL(String uploadFileKey, String dirName, HttpMethod httpMethod) throws IOException {
        String preSignedURL = "";
        String fileName = dirName + "/" + uploadFileKey;

        try {
            // Set the presigned URL to expire after 2 mins
            Date expiration = new Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 2;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL Request
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucket, fileName)
                            .withContentType("application/octet-stream")
                            .withMethod(httpMethod)
                            .withExpiration(expiration);

            // Add ACL
            generatePresignedUrlRequest.putCustomQueryParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

            // Generate the presigned URL
            URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
            preSignedURL = url.toString();

            log.info("Pre-Signed URL: " + url.toString());
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        return preSignedURL;
    }
}
