package br.com.jogafacil.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private Locatario locatario;
    private LocalDate data;
    private String horario;
    private Quadra quadra;
    private boolean extra;
    private String extraDescricao;

    public Reserva(Locatario locatario, LocalDate data, String horario, Quadra quadra,
                   boolean extra, String extraDescricao) {
        this.locatario = locatario;
        this.data = data;
        this.horario = horario;
        this.quadra = quadra;
        this.extra = extra;
        this.extraDescricao = extraDescricao;
    }

    public Locatario getLocatario() {
        return locatario;
    }

    public void setLocatario(Locatario locatario) {
        this.locatario = locatario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public boolean isExtra() {
        return extra;
    }

    public void setExtra(boolean extra) {
        this.extra = extra;
    }

    public String getExtraDescricao() {
        return extraDescricao;
    }

    public void setExtraDescricao(String extraDescricao) {
        this.extraDescricao = extraDescricao;
    }

    @Override
    public String toString() {
        String enderecoQuadra = (quadra != null) ? quadra.getEndereco() : "sem quadra";
        return enderecoQuadra + " - " + data + " " + horario;
    }
}