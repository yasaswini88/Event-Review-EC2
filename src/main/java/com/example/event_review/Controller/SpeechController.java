package com.example.event_review.Controller;

import com.google.cloud.speech.v1.*;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeechController.class);

    @Autowired
    private SpeechClient speechClient;

    @Autowired
    private TextToSpeechClient textToSpeechClient;

    /**
     * 1) Speech-to-Text endpoint
     *    Expects a base64-encoded audio string from the frontend.
     */
    @PostMapping("/speech-to-text")
    public String speechToText(@RequestBody SpeechRequest request) {
        try {
            // 1. Decode base64
            byte[] audioBytes = Base64.getDecoder().decode(request.getAudioContent());
            ByteString audioByteString = ByteString.copyFrom(audioBytes);

            // 2. Configure recognition
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    // .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16) // or whatever format you send
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(44100)
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioByteString)
                    .build();

            // 3. Perform Transcription
            RecognizeResponse response = speechClient.recognize(config, audio);
            StringBuilder transcription = new StringBuilder();

            for (SpeechRecognitionResult result : response.getResultsList()) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcription.append(alternative.getTranscript()).append(" ");
            }

            String finalTranscript = transcription.toString().trim();
            LOGGER.info("Transcription: {}", finalTranscript);

            return finalTranscript;

        } catch (Exception e) {
            LOGGER.error("Error in speechToText:", e);
            return "Error transcribing audio: " + e.getMessage();
        }
    }

    /**
     * 2) Text-to-Speech endpoint
     *    Expects text from the frontend, returns base64-encoded MP3 (by default).
     */
    @PostMapping("/text-to-speech")
    public TtsResponse textToSpeech(@RequestBody TtsRequest request) {
        TtsResponse result = new TtsResponse();
        try {
            // 1. Prepare input text
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(request.getText())
                    .build();

            // 2. Build the voice config
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // 3. Configure audio output (MP3, LINEAR16, OGG, etc.)
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // 4. Perform the text-to-speech request
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // 5. Convert audio content to base64 so frontend can play it
            byte[] audioBytes = response.getAudioContent().toByteArray();
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);
            LOGGER.info("TTS generated successfully, size: {} bytes", audioBytes.length);

            // 6. Return JSON with the base64
            result.setAudioContent(base64Audio);
            return result;

        } catch (Exception e) {
            LOGGER.error("Error in textToSpeech:", e);
            result.setError("Error generating speech: " + e.getMessage());
            return result;
        }
    }

    // --------------- Request and Response DTOs ---------------
    public static class SpeechRequest {
        // base64-encoded audio data
        private String audioContent;

        public String getAudioContent() {
            return audioContent;
        }
        public void setAudioContent(String audioContent) {
            this.audioContent = audioContent;
        }
    }

    public static class TtsRequest {
        // text to be converted
        private String text;

        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
    }

    public static class TtsResponse {
        // base64-encoded MP3 result
        private String audioContent;
        // store any error message if needed
        private String error;

        public String getAudioContent() {
            return audioContent;
        }
        public void setAudioContent(String audioContent) {
            this.audioContent = audioContent;
        }

        public String getError() {
            return error;
        }
        public void setError(String error) {
            this.error = error;
        }
    }
}
