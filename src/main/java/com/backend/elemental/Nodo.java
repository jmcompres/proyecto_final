package com.backend.elemental;

import java.util.LinkedHashSet;
import java.util.Set;

public class Nodo {

    private int id;                         //id de la parada que representa el nodo
    private Set<Nodo> paradas_adyacentes;   //en orden con las rutas
    private Set<Arista> rutas;              //en orden con los nodos

    public Nodo(int id) {
        this.id = id;
        paradas_adyacentes = new LinkedHashSet<Nodo>();
        rutas = new LinkedHashSet<Arista>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Nodo> getParadasAdyacentes() {
        return paradas_adyacentes;
    }

    public void setParadasAdyacentes(Set<Nodo> paradas_adyacentes) {
        this.paradas_adyacentes = paradas_adyacentes;
    }

    public Set<Arista> getRutas() {
        return rutas;
    }

    public void setRutas(Set<Arista> rutas) {
        this.rutas = rutas;
    }

    public void agregarAdyacencia(Nodo neoAd, Arista ruta)
    {
        paradas_adyacentes.add(neoAd);
        rutas.add(ruta);
    }
    
}
