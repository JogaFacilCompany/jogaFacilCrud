package br.com.jogafacil.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validador {

    public static String validarPessoa(String nome, String email, String senha) {
        StringBuilder erros = new StringBuilder();

        if (nome == null || nome.trim().isEmpty())
            erros.append("Nome é obrigatório.\n");

        if (email == null || !email.contains("@") || !email.contains("."))
            erros.append("Email inválido.\n");

        if (senha == null || senha.length() < 4)
            erros.append("Senha deve ter pelo menos 4 caracteres.\n");

        return erros.toString();
    }

    public static boolean isNumeroValido(String texto) {
        if (texto == null || texto.trim().isEmpty()) return false;
        try {
            Double.parseDouble(texto.trim().replace(",", "."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteiroPositivo(String texto) {
        if (texto == null || texto.trim().isEmpty()) return false;
        try {
            int valor = Integer.parseInt(texto.trim());
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDataValida(String texto) {
        if (texto == null || texto.trim().isEmpty()) return false;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(texto.trim(), fmt);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDate parseData(String texto) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(texto.trim(), fmt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static double parseDouble(String texto) {
        return Double.parseDouble(texto.trim().replace(",", "."));
    }
}