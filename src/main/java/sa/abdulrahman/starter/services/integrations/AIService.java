package sa.abdulrahman.starter.services.integrations;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatModel chatModel;

    public ChatResponse ask(String message) {
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(userMessage));
        return chatModel.call(prompt);
    }

    public ChatResponse askAI(String message) {
        return ask(message);
    }

    public ChatResponse askWithHistory(String message, List<String> historyMessages) {
        String historyJoined = String.join("\n\n", historyMessages);
        String fullMessage = historyJoined + "\n\n" + message;

        UserMessage userMessage = new UserMessage(fullMessage);

        Prompt prompt = new Prompt(List.of(userMessage));
        return chatModel.call(prompt);
    }
}
