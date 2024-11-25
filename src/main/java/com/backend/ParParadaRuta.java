package com.backend;

public record ParParadaRuta(Parada parada, Ruta ruta) {

    public ParParadaRuta(Parada parada, Ruta ruta)
    {
        this.parada = parada;
        this.ruta = ruta;
    }

}
