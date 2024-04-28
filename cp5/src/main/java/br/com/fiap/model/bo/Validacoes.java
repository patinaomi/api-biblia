package br.com.fiap.model.bo;
public class Validacoes {

    public static boolean validarNome(String nome) {
        if (nome.length() < 2 || nome.length() > 30) {
            System.out.println("O nome deve ter entre 2 e 30 caracteres.");
            return false;
        }

        // Regex para o nome: contém apenas letras e espaços permitidos (e opcionalmente, apóstrofos ou hífens)
        if (!nome.matches("[A-Za-zÀ-ÿ '-]+")) {
            System.out.println("O nome contém caracteres inválidos.");
            return false;
        }

        return true;
    }

    public static boolean validarEmail(String email) {
        // Regex para validar o e-mail
        String regex = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        if (email.matches(regex)) {
            return true;
        } else {
            System.out.printf("O e-mail %s é inválido.\n", email);
            return false;
        }
    }
}

