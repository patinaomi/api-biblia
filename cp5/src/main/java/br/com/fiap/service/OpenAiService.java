package br.com.fiap.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;

import java.util.Arrays;

public class OpenAiService {
   public static void dispararRequisicao(String user, String system) {
       String token = "tokenaqui";

       com.theokanning.openai.service.OpenAiService service = new com.theokanning.openai.service.OpenAiService(token);
       ChatCompletionRequest completionRequest = ChatCompletionRequest
               .builder()
               .model("gpt-3.5-turbo")
               .messages(Arrays.asList(
                       new ChatMessage(ChatMessageRole.USER.value(), user),
                       new ChatMessage(ChatMessageRole.SYSTEM.value(), system)
               ))
               .build();
       service
               .createChatCompletion(completionRequest)
               .getChoices()
               .forEach(c -> System.out.println(c.getMessage().getContent()));

   }
}
