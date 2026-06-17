package br.com.jogafacil.model;


public class Gerente extends Usuario {
    private static final long serialVersionUID = 1L;
    private Quadra quadra;

    public Gerente(String nome, String email, String senha, Quadra quadra) {
        super(nome, email, senha);
        this.quadra = quadra;
    }

    @Override
    public String getTipo() {
        return "GERENTE";
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }
}
