package com.backend;

public class Localizacion {
    
    private double latitud;
    private double longitud;
    private double altitud;
    private String descripcionDireccion;
    
    public Localizacion(double latitud, double longitud, double altitud, String descripcionDireccion) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.altitud = altitud;
        this.descripcionDireccion = descripcionDireccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getAltitud() {
        return altitud;
    }

    public void setAltitud(double altitud) {
        this.altitud = altitud;
    }

    public String getDescripcionDireccion() {
        return descripcionDireccion;
    }

    public void setDescripcionDireccion(String descripcionDireccion) {
        this.descripcionDireccion = descripcionDireccion;
    }

    

}