package com.example.event_review.Config;

import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GoogleCloudConfig {

    @Bean
    public SpeechClient speechClient() throws IOException {
        // This loads the service account JSON from src/main/resources/creds
        // Adjust the filename if it’s different
        SpeechSettings settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(() ->
                    com.google.auth.oauth2.ServiceAccountCredentials
                        .fromStream(getClass().getResourceAsStream("/creds/chatbot-stt-and-tts.json"))
                )
                .build();

        return SpeechClient.create(settings);
    }

    @Bean
    public TextToSpeechClient textToSpeechClient() throws IOException {
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(() ->
                    com.google.auth.oauth2.ServiceAccountCredentials
                        .fromStream(getClass().getResourceAsStream("/creds/chatbot-stt-and-tts.json"))
                )
                .build();

        return TextToSpeechClient.create(settings);
    }
}
