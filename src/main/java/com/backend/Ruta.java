package com.backend;

import java.io.Serializable;

public class Ruta implements Serializable{

    private static final long serialVersionUID = 1L;

    int id;
    private Parada destino;
    private Parada origen;
    private float tiempo;
    private float distancia;
    private float costoBruto;
    private float descuento;

    protected Ruta(Parada origen, Parada destino, int id, float tiempo, float distancia, float costoBruto) {
        this.origen = origen;
        this.destino = destino;
        this.id = id;
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costoBruto = costoBruto;
        this.descuento = 0;
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

    public float getCostoNeto() {
        return costoBruto*(1-descuento);
    }

    public float getCostoBruto() {
        return costoBruto;
    }

    public void setCostoBruto(float costo) {
        this.costoBruto = costo;
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

    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }


}
