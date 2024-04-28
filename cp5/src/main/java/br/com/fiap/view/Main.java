package br.com.fiap.view;


import br.com.fiap.service.OpenAiService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String livro = "João";
        int numCapitulo = 3;
        int numVersiculo = 16;

        String user = "Gere um devocional " + livro + " " + numCapitulo + ":"+ numVersiculo;
        String system = """
                Você é um professor de escola bíblica dominical e está ajudando na igreja .
                Deve gerar em até 200 palavras. Não precisa escrever os títulos. 
                Não pode escrever emojis. """;

        // OpenAiService.dispararRequisicao(user, system);

    }
}