package sa.abdulrahman.starter.services.integrations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    @Value("${s3.space.bucket}")
    private String bucket;
    @Value("${s3.space.base-url}")
    private String BASE_URL;

    public String saveFileToCloud(MultipartFile file, String folder, boolean isPublic) throws IOException {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String originalFilename = file.getOriginalFilename().replaceAll("[\\\\/:*?\"<>|\\s]", "_");
        originalFilename = originalFilename.substring(0, originalFilename.length()-4);

        int maxFilenameLength = 50;

        // Truncate the original filename, if it's too long
        if (originalFilename.length() > maxFilenameLength) {
            originalFilename = originalFilename.substring(0, maxFilenameLength);
        }

        String filename = originalFilename + "-" + UUID.randomUUID() + "." + extension;
        String key = folder + "/" + filename;

        save(file, key, isPublic);

        return isPublic ? BASE_URL + key : key;
    }

    @Async
    public void delete(String key) {
        s3Client.deleteObject(bucket, key);
    }

    @Async
    public void deleteAll(String[] keys) {
        s3Client.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(keys));
    }

    @Async
    public void deleteByURL(String url) {
        String key = extractKeyFromURL(url);
        s3Client.deleteObject(bucket, key);
    }

    public URL getFileURL(String key) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime()+ 1000 * 60 * 60; // 1 hour
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, key).withExpiration(expiration);

        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }


    @Async
    protected void save(MultipartFile file, String key, boolean isPublic) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getInputStream().available());
        if (file.getContentType() != null && !"".equals(file.getContentType())) {
            metadata.setContentType(file.getContentType());
        }
        s3Client.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), metadata)
                .withCannedAcl(isPublic ? CannedAccessControlList.PublicRead : CannedAccessControlList.AuthenticatedRead));
    }

    private String extractKeyFromURL(String url) {
        if (url.startsWith(BASE_URL)) {
            return url.substring(BASE_URL.length());
        } else {
            throw new IllegalArgumentException("URL does not start with the expected base URL.");
        }
    }

}