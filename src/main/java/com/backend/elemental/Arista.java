package com.backend.elemental;

public class Arista {
    private int tiempo;
    private int distancia;
    private int costo;

    public Arista(int tiempo, int distancia, int costo) {
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costo = costo;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

}
