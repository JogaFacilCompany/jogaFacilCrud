package br.com.jogafacil.model;

import java.io.Serializable;

public class Arbitro implements Serializable{
    private static final long serialVersionUID = 1L;
    private String nome;
    private String cpf;
    private String telefone;
    private double valorPartida;

    public Arbitro(String nome, String cpf, String telefone, double valorPartida) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.valorPartida = valorPartida;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public double getValorPartida() {
        return valorPartida;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setValorPartida(double valorPartida) {
        this.valorPartida = valorPartida;
    }

    @Override
    public String toString() {
        return nome + " - CPF: " + cpf;
    }
}
