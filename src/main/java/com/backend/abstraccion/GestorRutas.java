package com.backend.abstraccion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.backend.elemental.Arista;
import com.backend.elemental.Nodo;

public class GestorRutas {

    private static GestorRutas instancia = null;
    private static int capacidadInicial = 20;           //capacidad inicial para los ArrayLists con los nodos, paradas y rutas
    private static int idParadaActual = 1;              //Los ids se irán auto-asignando, así también se garantiza su orden (aún así, cuando se eliminen varias paradas y quede un hueco grande entre valores de id, se pueden actualizar sin alterar el orden)

    private List<Parada> paradas;                       //en orden con los nodos
    private List<Nodo> nodos;                           //en orden con las paradas
    private Set<String> nombresParadas;                 //esto para comprobar en tiempo constante que no se repitan los nombres

    class ParNodoDiscriminante implements Comparable<ParNodoDiscriminante>    //Par Nodo-Discriminante para la cola de prioridad en el algoritmo de Dijkstra
    {
        Nodo nodo;          //nodo
        float discriminante;  //distancia conocida desde el origen hasta el nodo

        public ParNodoDiscriminante(Nodo nodo, float discriminante)
        {
            this.nodo = nodo;
            this.discriminante = discriminante;
        }

        public int compareTo(ParNodoDiscriminante otroPar)
        {
            return Float.compare(this.discriminante, otroPar.discriminante);
        }
    }



    /*CONSTRUCTORES, GETTERS Y SETTERS*/

    private GestorRutas()
    {
        paradas = new ArrayList<Parada>(capacidadInicial);
        nodos = new ArrayList<Nodo>(capacidadInicial);
        nombresParadas = new HashSet<String>(capacidadInicial);
    }

    public static GestorRutas getInstance()
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

    public int agregarParada(String nombre, String localizacion) //retorna el índice de la nueva parada
    {
        if (nombresParadas.contains(nombre)) return -1;

        Parada neoParada = new Parada(idParadaActual, nombre, localizacion);
        nombresParadas.add(nombre);
        idParadaActual += 1;

        paradas.add(neoParada);
        Nodo neoNodo = new Nodo(idParadaActual-1);
        nodos.add(neoNodo);
        neoParada.setNodo(neoNodo);

        return idParadaActual-1;
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

    public List<Nodo> dijkstra(int idOrigen, int idDestino, boolean trueDist_FalseTiempo)  //retorna una lista con los nodos en la ruta más óptima (Solo funciona con distancias y tiempo, ya que estos solo pueden ser positivos)
    {

        int intIdOrigen = paradaBinarySearch(idOrigen);
        int intIdDestino = paradaBinarySearch(idDestino);
        if (intIdDestino == -1 || intIdOrigen == -1) return null;
        Nodo nOrigen = nodos.get(intIdOrigen);

        float[] discriminantes = new float[nodos.size()];
        int[] predecesores = new int[nodos.size()];
        boolean[] visitados = new boolean[nodos.size()];
        Arrays.fill(discriminantes, Float.MAX_VALUE);                      // Integer.MAX_VALUE representa infinito
        Arrays.fill(predecesores, -1);                                   // -1 es un flag
        Arrays.fill(visitados, false);
        discriminantes[intIdOrigen] = 0;

        PriorityQueue<ParNodoDiscriminante> colaPrio = new PriorityQueue<>();
        colaPrio.add(new ParNodoDiscriminante(nOrigen, 0));

        while(!colaPrio.isEmpty())
        {
            Nodo nodoActual = colaPrio.poll().nodo;
            int idNodoActual = paradaBinarySearch(nodoActual.getId());                                 //Esto se puede mejorar si se actualizan los id cada vez que se elimina una parada (creo que será lo mejor hacer eso, porque no serán muchas las veces que se eliminarán paradas, y añadirá eficiencia a este algoritmo)
            visitados[idNodoActual] = true;
            List<Arista> rutasAdyacentes = nodoActual.getRutas();
            int i = 0;                                                          // índice por el cual va la lista

            for (Nodo nodoAdyacente : nodoActual.getParadasApuntadas())
            {
                int idNodoAdyacente = paradaBinarySearch(nodoAdyacente.getId());
                if (!visitados[idNodoAdyacente])
                {
                    float discriminante;
                    if (trueDist_FalseTiempo) discriminante = rutasAdyacentes.get(i).getDistancia();
                    else discriminante = rutasAdyacentes.get(i).getTiempo();

                    if (discriminantes[idNodoActual] + discriminante < discriminantes[idNodoAdyacente])
                    {
                        discriminantes[idNodoAdyacente] = discriminante + discriminantes[idNodoActual];
                        predecesores[idNodoAdyacente] = idNodoActual;
                        colaPrio.add(new ParNodoDiscriminante(nodoAdyacente, discriminantes[idNodoAdyacente]));
                    }
                }
                i++;
            }
        }

        LinkedList<Nodo> rutaOptima = new LinkedList<>();
        int predecesorActual = predecesores[intIdDestino];
        rutaOptima.push(nodos.get(intIdDestino));
        while (predecesorActual != -1)
        {
            Nodo nodoPredecesor = nodos.get(predecesorActual);
            rutaOptima.push(nodoPredecesor);
            predecesorActual = predecesores[predecesorActual];
        }

        return rutaOptima;
    }

}