package com.backend.abstraccion;

public class Parada {
    int id;
    String nombre;
    String localizacion;

    protected Parada(int id, String nombre, String localizacion) {
        this.id = id;
        this.nombre = nombre;
        this.localizacion = localizacion;
    }

}
