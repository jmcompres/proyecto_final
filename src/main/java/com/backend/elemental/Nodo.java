package com.backend.elemental;

import java.util.LinkedHashSet;
import java.util.Set;

public class Nodo {

    private int id;                         //id de la parada que representa el nodo
    private Set<Nodo> paradas_apuntadas;    //en orden con las rutas; estas son las paradas a las que se puede dirigir desde esta parada
    private Set<Arista> rutas;              //en orden con los nodos
    private Set<Nodo> paradas_apuntadoras;  //estas son las paradas que apuntan a esta (esta lista facilitará luego eliminar los enlaces de esas paradas cuando se elimine esta parada)

    public Nodo(int id) {
        this.id = id;
        paradas_apuntadas = new LinkedHashSet<Nodo>();
        rutas = new LinkedHashSet<Arista>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Nodo> getParadasApuntadas() {
        return paradas_apuntadas;
    }

    public Set<Arista> getRutas() {
        return rutas;
    }

    public void setRutas(Set<Arista> rutas) {
        this.rutas = rutas;
    }

    public Set<Nodo> getParadasApuntadoras() {
        return paradas_apuntadoras;
    }


    /*OTROS MÉTODOS */

    public void agregarAdyacencia(Nodo neoAd, Arista ruta)
    {
        paradas_apuntadas.add(neoAd);
        rutas.add(ruta);
    }
    
}
