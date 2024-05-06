package br.com.fiap.model.bo;
public class Validacoes {

    public static boolean validarUsuario(String nome) {
        if (nome.length() < 2 || nome.length() > 30) {
            System.out.println("O nome deve ter entre 2 e 30 caracteres.");
            return false;
        }
        if (!nome.matches("^[A-Za-zÀ-ÿ0-9]+$")) { //Não pode ter espaço o usuário!
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

    public static boolean validarSenha(String senha) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!*@#$%^&+=])(?=\\S+$).{6,}$";

        if (senha.matches(regex)) {
            return true;
        } else {
            System.out.println("A senha deve conter no mínimo 6 caracteres, incluindo pelo menos um número, uma letra maiúscula, uma letra minúscula e um caractere especial.");
            return false;
        }
    }
}

