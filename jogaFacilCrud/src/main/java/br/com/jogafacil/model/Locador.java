package br.com.jogafacil.model;


public class Locador extends Usuario {

    private static final long serialVersionUID = 1L;


    public Locador(String nome, String email, String senha) {
        super(nome, email, senha);
    }

    @Override
    public String getTipo() {
        return "LOCADOR";
    }
}