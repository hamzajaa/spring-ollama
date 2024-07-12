package com.example.springollama;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AIController {

    private final OllamaChatModel chatModel;

    public AIController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/generate/{promptMessage}")
    public String generate(@PathVariable String promptMessage){
        ChatResponse response = chatModel.call(
                new Prompt(
                        promptMessage,
                        OllamaOptions.create()
                                .withModel("llama2")
                ));
        return response.getResult().getOutput().getContent();
    }

    @GetMapping("/generateStream/{promptMessage}")
    public Flux<String> generateStream(@PathVariable String promptMessage){
        Flux<ChatResponse> llama2 = chatModel.stream(
                new Prompt(
                        promptMessage,
                        OllamaOptions.create()
                                .withModel("llama2")
                ));
        return llama2.map(chatResponse -> chatResponse.getResult().getOutput().getContent());
    }
}
