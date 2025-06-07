package sa.abdulrahman.starter.services.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class SMSService {

    private final WebClient webClient;

    @Value("${msegat.username}")
    private String username;

    @Value("${msegat.api.key}")
    private String apiKey;

    @Value("${msegat.sender.id}")
    private String senderId;

    @Value("${msegat.api.url}")
    private String apiUrl;

    public SMSService() {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    @Async
    public void sendOTP(String phoneNumber, String otp) {
        try {
            webClient.post()
                    .uri("/sendsms.php")
                    .bodyValue(Map.of(
                            "apiKey", apiKey,
                            "userSender", senderId,
                            "userName", username,
                            "numbers", "966"+phoneNumber,
                            "msg", String.format("Pin Code is: %s", otp)
                    ))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
                throw new RuntimeException(e);
        }
    }

    public String getBalance() {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("userName", username);
            bodyBuilder.part("apiKey", apiKey);

            return webClient.post()
                    .uri("/Credits.php")
                    .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to check credits: " + e.getMessage());
        }
    }

}