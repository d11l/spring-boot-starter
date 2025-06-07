package sa.abdulrahman.starter.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {


    @Value("${s3.space.key}")
    private String spaceKey;

    @Value("${s3.space.secret}")
    private String spaceSecret;

    @Value("${s3.space.endpoint}")
    private String spaceEndpoint;

    @Value("${s3.space.region}")
    private String spaceRegion;

    @Bean
    public AmazonS3 getS3() {
            return AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new EndpointConfiguration(spaceEndpoint, spaceRegion))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(spaceKey, spaceSecret)))
                    .withPathStyleAccessEnabled(true)
                    .build();
    }
}