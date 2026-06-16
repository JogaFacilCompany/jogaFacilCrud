package br.com.jogafacil.model;

import java.io.Serializable;

public class Quadra implements Serializable {
    private static final long serialVersionUID = 1L;
    private Modalidade modalidade;
    private String cnpj;
    private String endereco;
    private double valor;

    public Quadra(Modalidade modalidade, String cnpj, String endereco, double valor){
        this.modalidade = modalidade;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.valor = valor;
    }

    public Modalidade getModalidade(){
        return modalidade;
    }

    public String getCnpj(){
        return cnpj;
    }

    public String getEndereco(){
        return endereco;
    }

    public double getValor(){
        return valor;
    }

    public void setModalidade(Modalidade modalidade){
        this.modalidade = modalidade;
    }
    public void setCnpj(String cnpj){
        this.cnpj = cnpj;
    }
    public void setEndereco(String endereco){
        this.endereco = endereco;
    }
    public void setValor(double valor){
        this.valor = valor;
    }

}

