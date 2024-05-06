package br.com.fiap.model.bo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class GestaoData {

    private static final DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    // Para obter a data e hora atual como Timestamp para inserção no banco
    public static Timestamp obterDataHoraAtual() {
        return Timestamp.valueOf(LocalDateTime.now());
    }


    // Para formatar Timestamp para String (para apresentar no tostring)
    public static String formatarTimestampParaString(Timestamp dataHora) {
        LocalDateTime dataHoraLocal = dataHora.toLocalDateTime();
        return dataFormatada.format(dataHoraLocal);
    }

}
