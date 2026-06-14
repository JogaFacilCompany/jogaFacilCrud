package br.com.jogafacil.model;

import java.io.Serializable;

public class Modalidade implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private int qtdJogadores;
    private String descricao;

    public Modalidade(String nome, int qtdJogadores, String descricao) {
        this.nome = nome;
        this.qtdJogadores = qtdJogadores;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQtdJogadores() {
        return qtdJogadores;
    }

    public void setQtdJogadores(int qtdJogadores) {
        this.qtdJogadores = qtdJogadores;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return nome + " (" + qtdJogadores + " jogadores)";
    }
}
