package com.backend.abstraccion;

import java.util.ArrayList;
import java.util.List;

import com.backend.elemental.Arista;
import com.backend.elemental.Nodo;

public class GestorRutas {

    private static GestorRutas instancia = null;
    private static int capacidadInicial = 20; //capacidad inicial para los ArrayLists con los nodos, paradas y rutas
    private static int idParadaActual = 1;    //Los ids se irán auto-asignando, así también se garantiza su orden (aún así, cuando se eliminen varias paradas y quede un hueco grande entre valores de id, se pueden actualizar sin alterar el orden)

    private List<Parada> paradas;  //en orden con los nodos
    private List<Nodo> nodos;      //en orden con las paradas



    /*CONSTRUCTORES, GETTERS Y SETTERS*/

    private GestorRutas()
    {
        paradas = new ArrayList<Parada>(capacidadInicial);
        nodos = new ArrayList<Nodo>(capacidadInicial);
    }

    public GestorRutas getInstance()
    {
        if (instancia == null) instancia = new GestorRutas();
        return instancia;
    }

    public List<Parada> getParadas() {
        return paradas;
    }

    public void setParadas(List<Parada> paradas) {
        this.paradas = paradas;
    }

    public List<Nodo> getNodos() {
        return nodos;
    }

    public void setNodos(List<Nodo> nodos) {
        this.nodos = nodos;
    }



    /*OTROS MÉTODOS*/

    private int ParadaBinarySearch(int id) //retorna el índice
    {
        int left = 0, right = paradas.size()-1, mid;
        while (left<=right)
        {
            mid = (left+right)/2;
            int pMidId = paradas.get(mid).getId();
            if (pMidId == id) return mid;

            if (pMidId > id) left = mid+1;
            else right = mid-1;
        }

        return -1;
    }

    public boolean agregarParada(String nombre, String localizacion) //retorna un booleano que indica si el proceso se completó satisfactoriamente
    {
        /*AGREGAR COMPROBACIONES DE NOMBRES Y LOCALIZACIONES*/
        //if (false) return false;
        //if (false) return false;

        Parada neoParada = new Parada(idParadaActual, nombre, localizacion);
        idParadaActual += 1;

        paradas.add(neoParada);
        Nodo neoNodo = new Nodo(idParadaActual-1);
        nodos.add(neoNodo);
        neoParada.setNodo(neoNodo);
        
        return true;
    }

    public void agregarRuta(int idParadaFuente, int idParadaDestino, int tiempo, int distancia, int costo)
    {
        int pFuenteInd = ParadaBinarySearch(idParadaFuente);
        int pDestinoInd = ParadaBinarySearch(idParadaDestino);
        if (pFuenteInd == -1 || pDestinoInd == -1) return;

        Parada pFuente = paradas.get(pFuenteInd);
        Parada pDestino = paradas.get(pDestinoInd);
        Nodo nFuente = pFuente.getNodo();
        Nodo nDestino = pDestino.getNodo();

        Arista neoRuta = new Arista(tiempo, distancia, costo);
        nFuente.getRutas().add(neoRuta);
        nFuente.getParadasAdyacentes().add(nDestino);
    }

}
