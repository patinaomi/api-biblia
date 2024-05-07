package br.com.fiap.service;

import br.com.fiap.config.Config;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;

import java.io.*;

/**
 * Serviço para converter texto em fala usando a API IBM Watson Text to Speech.
 * Esta classe encapsula a funcionalidade de converter strings de texto em arquivos de áudio falados.
 */
public class TextToSpeechService {
    private TextToSpeech textToSpeech;

    /**
     * Constrói uma instância de TextToSpeechService configurando o cliente IBM Watson Text to Speech.
     * A chave API e a URL do serviço são obtidas de um arquivo de configuração.
     */
    public TextToSpeechService() {
        Authenticator authenticator = new IamAuthenticator(Config.getProperty("tts.api.key"));
        textToSpeech = new TextToSpeech(authenticator);
        textToSpeech.setServiceUrl(Config.getProperty("tts.service.url"));
    }

    /**
     * Sintetiza texto para áudio e salva o resultado em um arquivo temporário.
     * O método utiliza as configurações especificadas para a voz e o formato do arquivo de saída.
     *
     * @param text O texto que será convertido em fala.
     * @return Um arquivo contendo o áudio gerado ou null se ocorrer uma falha.
     * @throws IOException Se ocorrer um erro ao criar o arquivo de áudio.
     */
    public File synthesizeToFile(String text) {
        try {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .text(text)
                    .voice("pt-BR_IsabelaV3Voice")
                    .accept("audio/mp3")
                    .build();

            InputStream inputStream = textToSpeech.synthesize(synthesizeOptions).execute().getResult();
            File tempFile = File.createTempFile("tts", ".mp3");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                return tempFile;
            }
        } catch (IOException e) {
            System.err.println("Failed to create audio file: " + e.getMessage());
            return null;
        }
    }
}