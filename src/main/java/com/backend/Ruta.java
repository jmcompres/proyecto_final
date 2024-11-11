package com.backend;

public class Ruta {

    int id;
    private Parada destino;
    private Parada origen;
    private float tiempo;
    private float distancia;
    private float costo;

    public Ruta(Parada origen, Parada destino, int id, float tiempo, float distancia, float costo) {
        this.origen = origen;
        this.destino = destino;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Parada getDestino() {
        return destino;
    }

    public void setDestino(Parada destino) {
        this.destino = destino;
    }

    public Parada getOrigen() {
        return origen;
    }

    public void setOrigen(Parada origen) {
        this.origen = origen;
    }

}
