package sa.abdulrahman.starter.services.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class SMSService {

    private final WebClient webClient;

    @Value("${msegat.username}")
    private String username;

    @Value("${msegat.api.key}")
    private String apiKey;

    @Value("${msegat.sender.id}")
    private String senderId;

    @Value("${msegat.sender.ad.id}")
    private String senderAdId;

    public SMSService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://www.msegat.com/gw")
                .build();
    }

    public void sendOTP(String phone, String otp) {
        send(senderId, phone, String.format("Pin Code is: %s", otp));
    }

    public void send(String phone, String message) {
        send(senderId, phone, message);
    }

    public void sendAD(String phone, String message) {
        send(senderAdId, phone, message);
    }

    @Async
    protected void send(String sender, String phone, String message) {
        try {
            webClient.post()
                    .uri("/sendsms.php")
                    .bodyValue(Map.of(
                            "apiKey", apiKey,
                            "userName", username,
                            "userSender", sender,
                            "numbers", "966"+phone,
                            "msg", message
                    ))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage());
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