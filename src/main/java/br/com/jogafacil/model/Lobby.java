package br.com.jogafacil.model;

import java.io.Serializable;

public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    private Reserva reserva;
    private boolean precisaArbitro;
    private int jogadoresEmFalta;

    public Lobby(Reserva reserva, boolean precisaArbitro, int jogadoresEmFalta) {
        this.reserva = reserva;
        this.precisaArbitro = precisaArbitro;
        this.jogadoresEmFalta = jogadoresEmFalta;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public boolean isPrecisaArbitro() {
        return precisaArbitro;
    }

    public void setPrecisaArbitro(boolean precisaArbitro) {
        this.precisaArbitro = precisaArbitro;
    }

    public int getJogadoresEmFalta() {
        return jogadoresEmFalta;
    }

    public void setJogadoresEmFalta(int jogadoresEmFalta) {
        this.jogadoresEmFalta = jogadoresEmFalta;
    }

    @Override
    public String toString() {
        String resumoReserva = (reserva != null) ? reserva.toString() : "sem reserva";
        return "Lobby - " + resumoReserva + " (faltam " + jogadoresEmFalta + ")";
    }
}