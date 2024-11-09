package com.backend.abstraccion;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.backend.elemental.Arista;

public class Parada {

    private int id;
    private String nombre;
    private String localizacion;

    private Set<Parada> paradasApuntadas;    //en orden con las rutas; estas son las paradas a las que se puede dirigir desde esta parada
    private List<Arista> rutas;               //en orden con los nodos
    private Set<Parada> paradasApuntadoras;  //estas son las paradas que apuntan a esta (esta lista facilitará luego eliminar los enlaces de esas paradas cuando se elimine esta parada)

    protected Parada(int id, String nombre, String localizacion) {
        this.id = id;
        this.nombre = nombre;
        this.localizacion = localizacion;
        paradasApuntadas = new LinkedHashSet<>();
        paradasApuntadoras = new LinkedHashSet<>();
        rutas = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public Set<Parada> getParadasApuntadas() {
        return paradasApuntadas;
    }

    public void setParadasApuntadas(Set<Parada> paradasApuntadas) {
        this.paradasApuntadas = paradasApuntadas;
    }

    public List<Arista> getRutas() {
        return rutas;
    }

    public void setRutas(List<Arista> rutas) {
        this.rutas = rutas;
    }

    public Set<Parada> getParadasApuntadoras() {
        return paradasApuntadoras;
    }

    public void setParadasApuntadoras(Set<Parada> paradas_apuntadoras) {
        this.paradasApuntadoras = paradas_apuntadoras;
    }

    public void agregarAdyacencia(Parada neoAd, Arista ruta)
    {
        paradasApuntadas.add(neoAd);
        rutas.add(ruta);
        neoAd.getParadasApuntadoras().add(this);
    }

}
