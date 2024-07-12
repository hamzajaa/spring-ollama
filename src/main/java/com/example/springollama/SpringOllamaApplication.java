package com.example.springollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class SpringOllamaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringOllamaApplication.class, args);
    }

    private ChatClient chatClient;

    @Bean
    ApplicationRunner init(OllamaChatModel chatModel) {
        return args -> {
            System.out.println(
                    chatModel.call(
                            new Prompt(
                                    "What is the capital of Morocco?",
                                    OllamaOptions.create()
                                            .withModel("llama2")
                            )
                    ).getResult().getOutput().getContent()
            );

            System.out.println("********************************");
            OllamaChatModel chatModel2 = new OllamaChatModel(
                    new OllamaApi("http://localhost:11434/"),
                    OllamaOptions.create()
                            .withModel("llama3")
            );
            this.chatClient = ChatClient.builder(chatModel2).build();

            String content = chatClient.prompt()
                    .user("What is the capital of Saudi Arabia?")
                    .call()
                    .content();
            System.out.println(content);

            System.out.println("********************************");

            Player player = chatClient.prompt()
                    .user("give me the Best player in the world in 2023")
                    .call()
                    .entity(Player.class);
            System.out.println("player = " + player);

            System.out.println("********************************");

            List<Player> players = chatClient.prompt()
                    .user("give me the Best 10 players in the world in 2023")
                    .call()
                    .entity(new ParameterizedTypeReference<List<Player>>() {});
            System.out.println("players = " + players);

            System.out.println("********************************");

            // This is the code that will be used to explain the image
            ClassPathResource classPathResource = new ClassPathResource("Spring batch Archt.png");
            UserMessage userMessage = new UserMessage("Explain what do you see on this picture?",
                    List.of(new Media(MimeTypeUtils.IMAGE_PNG, classPathResource)));

            ChatResponse response = chatModel.call(
                    new Prompt(
                            userMessage,
                            OllamaOptions.create()
                                    .withModel("llava")
                    )
            );
            System.out.println(response.getResult().getOutput().getContent());


        };
    }

    record Player(String name, String nationality, String club) {
    }
}
