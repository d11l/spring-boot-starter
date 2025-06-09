package sa.abdulrahman.starter.services.integrations;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import sa.abdulrahman.starter.records.HistoryMessage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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

    public ChatResponse askWithHistory(String message, List<HistoryMessage> historyMessages) {

        if (historyMessages == null) {
            return ask(message);
        }

        // Convert history messages to conversation messages
        List<Message> conversationMessages = historyMessages.stream()
                .filter(Objects::nonNull)
                .map(hm -> switch (hm.role()) {
                    case USER -> new UserMessage(hm.content());
                    case ASSISTANT -> new AssistantMessage(hm.content());
                })
                .collect(Collectors.toList());

        // Add the current user message
        conversationMessages.add(new UserMessage(message.trim()));

        Prompt prompt = new Prompt(conversationMessages);
        return chatModel.call(prompt);
    }
}
