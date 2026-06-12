package br.com.jogafacil.util;

import java.io.*;
import java.util.ArrayList;

public class Arquivo {

    private static final String PASTA = "dados/";

    public static <T> void salvar(ArrayList<T> lista, String nomeArquivo) {
        // Cria a pasta "dados/" se ela não existir
        new File(PASTA).mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(PASTA + nomeArquivo))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> carregar(String nomeArquivo) {
        File arquivo = new File(PASTA + nomeArquivo);

        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (ArrayList<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar " + nomeArquivo + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}