package dk.ek.gruppe2.chooseyourfate.service;

import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.elevenlabs.ElevenLabsTextToSpeechModel;
import org.springframework.ai.elevenlabs.ElevenLabsTextToSpeechOptions;
import org.springframework.ai.elevenlabs.api.ElevenLabsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TTSService {

    public byte[] textToSpeech(String text) throws IOException {
        ElevenLabsApi elevenLabsApi = ElevenLabsApi.builder()
                .apiKey(System.getenv("ELEVEN_LABS_API_KEY"))
                .build();

        ElevenLabsTextToSpeechModel elevenLabsTextToSpeechModel = ElevenLabsTextToSpeechModel.builder()
                .elevenLabsApi(elevenLabsApi)
                .defaultOptions(ElevenLabsTextToSpeechOptions.builder()
                        .model("eleven_turbo_v2_5")
                        .voiceId("JBFqnCBsd6RMkjVDRZzb") // e.g. "9BWtsMINqrJLrRacOk9x"
                        .outputFormat("mp3_44100_128")
                        .build())
                .build();

// The call will use the default options configured above.
        TextToSpeechPrompt speechPrompt = new TextToSpeechPrompt(text);
        TextToSpeechResponse response = elevenLabsTextToSpeechModel.call(speechPrompt);

        return response.getResult().getOutput();

    }
}
