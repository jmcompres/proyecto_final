package com.backend;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Parada {

    private int id;
    private String nombre;
    private String localizacion;

    private List<Parada> paradasApuntadas;   //en orden con las rutas; estas son las paradas a las que se puede dirigir desde esta parada
    private List<Ruta> rutas;                //en orden con las paradas a las que apunta
    private Set<Parada> paradasApuntadoras;  //estas son las paradas que apuntan a esta (esta lista facilitará luego eliminar los enlaces de esas paradas cuando se elimine esta parada)

    protected Parada(int id, String nombre, String localizacion) {
        this.id = id;
        this.nombre = nombre;
        this.localizacion = localizacion;
        paradasApuntadas = new ArrayList<>();
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

    public List<Parada> getParadasApuntadas() {
        return paradasApuntadas;
    }

    public void setParadasApuntadas(List<Parada> paradasApuntadas) {
        this.paradasApuntadas = paradasApuntadas;
    }

    public List<Ruta> getRutas() {
        return rutas;
    }

    public void setRutas(List<Ruta> rutas) {
        this.rutas = rutas;
    }

    public Set<Parada> getParadasApuntadoras() {
        return paradasApuntadoras;
    }

    public void setParadasApuntadoras(Set<Parada> paradas_apuntadoras) {
        this.paradasApuntadoras = paradas_apuntadoras;
    }

    public void agregarAdyacencia(Parada neoAd, Ruta ruta)
    {
        paradasApuntadas.add(neoAd);
        rutas.add(ruta);
        neoAd.getParadasApuntadoras().add(this);
    }

}
