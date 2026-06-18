package br.com.jogafacil.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Torneio implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private double premiacao;
    private LocalDate dataInicio;
    private double taxaInscricao;
    private int numeroTimes;
    private ArrayList<Usuario> times = new ArrayList<>();

    public Torneio(String nome, double premiacao, LocalDate dataInicio,
                   double taxaInscricao, int numeroTimes) {
        this.nome = nome;
        this.premiacao = premiacao;
        this.dataInicio = dataInicio;
        this.taxaInscricao = taxaInscricao;
        this.numeroTimes = numeroTimes;
    }

    public String getNome() {
        return nome;
    }

    public double getPremiacao() {
        return premiacao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public double getTaxaInscricao() {
        return taxaInscricao;
    }

    public int getNumeroTimes() {
        return numeroTimes;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPremiacao(double premiacao) {
        this.premiacao = premiacao;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setTaxaInscricao(double taxaInscricao) {
        this.taxaInscricao = taxaInscricao;
    }

    public void setNumeroTimes(int numeroTimes) {
        this.numeroTimes = numeroTimes;
    }

    public void entrar(Usuario usuario) {
        this.times.add(usuario);
        this.numeroTimes -= 1;}

    public boolean isOnTorneio(Usuario time) {
        for (Usuario usuario : this.times) {
            if (usuario.getEmail().equals(time.getEmail())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return nome + " (início: " + dataInicio + ")";
    }
}
