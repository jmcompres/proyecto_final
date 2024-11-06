package com.backend.abstraccion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.backend.elemental.Arista;
import com.backend.elemental.Nodo;

public class GestorRutas {

    private static GestorRutas instancia = null;
    private static int capacidadInicial = 20;     //capacidad inicial para los ArrayLists con los nodos, paradas y rutas
    private static int idParadaActual = 1;        //Los ids se irán auto-asignando, así también se garantiza su orden (aún así, cuando se eliminen varias paradas y quede un hueco grande entre valores de id, se pueden actualizar sin alterar el orden)

    private List<Parada> paradas;                 //en orden con los nodos
    private List<Nodo> nodos;                     //en orden con las paradas
    private Set<String> nombresParadas;            //esto para comprobar en tiempo constante que no se repitan los nombres



    /*CONSTRUCTORES, GETTERS Y SETTERS*/

    private GestorRutas()
    {
        paradas = new ArrayList<Parada>(capacidadInicial);
        nodos = new ArrayList<Nodo>(capacidadInicial);
        nombresParadas = new HashSet<String>(capacidadInicial);
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

    public int paradaBinarySearch(int id) //retorna el índice
    {
        int left = 0, right = paradas.size()-1, mid;
        while (left<=right)
        {
            mid = (left+right)/2;
            int pMidId = paradas.get(mid).getId();
            if (pMidId == id) return mid;

            if (pMidId > id) right = mid-1;
            else left = mid+1;
        }

        return -1;
    }

    public boolean agregarParada(String nombre, String localizacion) //retorna un booleano que indica si el proceso se completó satisfactoriamente
    {
        if (nombresParadas.contains(nombre)) return false;

        Parada neoParada = new Parada(idParadaActual, nombre, localizacion);
        nombresParadas.add(nombre);
        idParadaActual += 1;

        paradas.add(neoParada);
        Nodo neoNodo = new Nodo(idParadaActual-1);
        nodos.add(neoNodo);
        neoParada.setNodo(neoNodo);
        
        return true;
    }

    public void agregarRuta(int idParadaFuente, int idParadaDestino, int tiempo, int distancia, int costo)
    {
        int pFuenteInd = paradaBinarySearch(idParadaFuente);
        int pDestinoInd = paradaBinarySearch(idParadaDestino);
        if (pFuenteInd == -1 || pDestinoInd == -1) return;

        Parada pFuente = paradas.get(pFuenteInd);
        Parada pDestino = paradas.get(pDestinoInd);
        Nodo nFuente = pFuente.getNodo();
        Nodo nDestino = pDestino.getNodo();

        Arista neoRuta = new Arista(tiempo, distancia, costo);
        nFuente.agregarAdyacencia(nDestino, neoRuta);
    }

    public void eliminarParada(int id)
    {
        int pos = paradaBinarySearch(id);
        if (pos == -1) return;

        //Primero se eliminan todas las conexiones de todos los nodos que apuntaban a la parada a eliminar
        Nodo nodoParada = nodos.get(pos);
        Set<Nodo> paradasApuntadoras = nodoParada.getParadasApuntadoras();
        for (Nodo n : paradasApuntadoras)
        {
            n.getParadasApuntadas().remove(nodoParada);
        }

        //Finalmente se elimina la parada
        nodos.remove(pos);
        paradas.remove(pos);
    }

}
