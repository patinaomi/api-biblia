package br.com.fiap.config;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Classe de configuração para carregar propriedades do arquivo config.properties.
 * Esta classe usa um bloco estático para carregar configurações durante a inicialização da classe.
 */
public class Config {
    private static Properties properties = new Properties();

    /**
     * Bloco estático para carregar as propriedades ao iniciar a classe.
     */
    static {
        loadProperties();
    }

    /**
     * Carrega propriedades do arquivo config.properties localizado no classpath.
     * Se o arquivo não puder ser encontrado ou ocorrer um erro durante a leitura, uma mensagem será impressa e o programa continuará.
     */
    private static void loadProperties() {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Recupera uma propriedade pelo nome da chave.
     *
     * @param key A chave da propriedade a ser recuperada.
     * @return O valor da propriedade como uma String, ou null se a chave não for encontrada.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}