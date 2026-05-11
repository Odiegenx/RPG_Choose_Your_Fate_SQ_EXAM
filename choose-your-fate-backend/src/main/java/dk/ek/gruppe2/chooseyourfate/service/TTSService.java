package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.model.mysql.CharacterPath;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.CharacterPathRepository;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.elevenlabs.ElevenLabsTextToSpeechModel;
import org.springframework.ai.elevenlabs.ElevenLabsTextToSpeechOptions;
import org.springframework.ai.elevenlabs.api.ElevenLabsApi;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TTSService {

    public final CharacterPathRepository characterPathRepository;

    public TTSService(CharacterPathRepository characterPathRepository) {
        this.characterPathRepository = characterPathRepository;
    }

    public byte[] textToSpeech(Integer characterId) {
        CharacterPath characterPath = characterPathRepository.findByCharacter_Id(characterId);
        if (characterPath.getAudioBlob() != null) {
            return characterPath.getAudioBlob();
        }
        else {
            byte[] audioBlob = createAudioBlob(characterPath.getSummary());
            characterPath.setAudioBlob(audioBlob);
            characterPathRepository.save(characterPath);
            return audioBlob;
        }
    }

    public byte[] createAudioBlob(String text) {
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
