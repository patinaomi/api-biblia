package br.com.fiap.model.bo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;


/**
 * Classe de utilitários para gerenciamento de datas dentro da aplicação.
 * Provê métodos para obter a data e hora atual formatada para uso com Timestamp em banco de dados.
 */
public class GestaoData {

    /**
     * Formato padrão para a data e hora.
     */
    private static final DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Retorna a data e hora atual do sistema como um objeto {@link Timestamp}.
     * Este timestamp é útil para registrar momentos exatos de eventos ou ações no banco de dados.
     *
     * @return A data e hora atual como um {@link Timestamp}.
     */
    public static Timestamp obterDataHoraAtual() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

}