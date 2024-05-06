package br.com.fiap.service;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

import java.io.*;

public class TextToSpeechService {
    private TextToSpeech textToSpeech;

    public TextToSpeechService() {
        Authenticator authenticator = new IamAuthenticator("C9cIgsQ-MuG7vqV-mPuWN95MYPskr92hxRubA5Zhb924");
        textToSpeech = new TextToSpeech(authenticator);
        textToSpeech.setServiceUrl("https://api.us-south.text-to-speech.watson.cloud.ibm.com/instances/458b7925-06df-45d2-a072-5232f50bb04d");
    }

    public InputStream synthesize(String text) {
        SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                .text(text)
                .voice("pt-BR_IsabelaV3Voice")
                .accept("audio/mp3")
                .build();

        InputStream inputStream = textToSpeech.synthesize(synthesizeOptions).execute().getResult();
        return inputStream; // Retorna o InputStream diretamente
    }

}