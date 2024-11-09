package com.backend;

public class Ruta {
    private float tiempo;
    private float distancia;
    private float costo;

    public Ruta(float tiempo, float distancia, float costo) {
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costo = costo;
    }

    public float getTiempo() {
        return tiempo;
    }

    public void setTiempo(float tiempo) {
        this.tiempo = tiempo;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

}
