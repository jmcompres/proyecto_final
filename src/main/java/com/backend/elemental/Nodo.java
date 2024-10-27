package com.backend.elemental;

import java.util.LinkedHashSet;
import java.util.Set;

public class Nodo {

    private int id; //id de la parada que representa el nodo
    private Set<Nodo> paradas_adyacentes; //en orden con las rutas
    private Set<Arista> rutas; //en orden con los nodos

    public Nodo(int id) {
        this.id = id;
        paradas_adyacentes = new LinkedHashSet<Nodo>();
        rutas = new LinkedHashSet<Arista>();
    }

    public void agregar_adyacencia(Nodo neoAd, Arista ruta)
    {
        paradas_adyacentes.add(neoAd);
        rutas.add(ruta);
    }

    public int getId()
    {
        return id;
    }
    
}
