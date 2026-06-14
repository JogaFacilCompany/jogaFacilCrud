package br.com.jogafacil.model;

import java.io.Serializable;

public class Equipamento implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private double valor;
    private String descricao;

    public Equipamento(String nome, double valor, String descricao) {
        this.nome = nome;
        this.valor = valor;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return nome + " (R$ " + String.format("%.2f", valor) + ")";
    }
}
