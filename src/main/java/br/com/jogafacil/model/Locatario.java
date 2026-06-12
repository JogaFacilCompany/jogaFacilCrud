package br.com.jogafacil.model;


public class Locatario extends Usuario {

    private static final long serialVersionUID = 1L;

    public Locatario(String nome, String email, String senha) {
        super(nome, email, senha);
    }

    @Override
    public String getTipo() {
        return "LOCATARIO";
    }
}