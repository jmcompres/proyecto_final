package com.backend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class GestorRutas {

    private static GestorRutas instancia = null;
    private static int capacidadInicial = 50;           //capacidad inicial para los ArrayLists con los nodos, paradas y rutas
    private static int idParadaActual = 1;              //Los ids se irán auto-asignando

    private Map<Integer, Parada> paradas;               //la clave Integer es el atributo id de la parada
    private Set<String> nombresParadas;                 //esto para comprobar en tiempo constante que no se repitan los nombres y ubicaciones

    private GestorRutas()
    {
        paradas = new HashMap<Integer, Parada>(capacidadInicial);
        nombresParadas = new HashSet<String>(capacidadInicial);
    }

    public static GestorRutas getInstance()
    {
        if (instancia == null) instancia = new GestorRutas();
        return instancia;
    }


    /*OTROS MÉTODOS*/

    public int agregarParada(String nombre, String localizacion) //retorna el índice de la nueva parada
    {
        if (nombresParadas.contains(nombre)) return -1;

        Parada neoParada = new Parada(idParadaActual, nombre, localizacion);
        paradas.put(idParadaActual,neoParada);
        nombresParadas.add(nombre);
        idParadaActual += 1;

        return idParadaActual-1;
    }

    public void agregarRuta(int idParadaFuente, int idParadaDestino, int tiempo, int distancia, int costo)
    {
        if (!paradas.containsKey(idParadaDestino) || !paradas.containsKey(idParadaFuente)) return;

        Parada pFuente = paradas.get(idParadaFuente);
        Parada pDestino = paradas.get(idParadaDestino);

        Ruta neoRuta = new Ruta(tiempo, distancia, costo);
        pFuente.agregarAdyacencia(pDestino, neoRuta);
    }

    public void eliminarParada(int id)
    {
        if (!paradas.containsKey(id)) return;

        //Primero se eliminan todas las conexiones de todos los nodos que apuntaban a la parada a eliminar
        Parada paradaElim = paradas.get(id);
        Set<Parada> paradasApuntadoras = paradaElim.getParadasApuntadoras();
        for (Parada p : paradasApuntadoras)
        {
            p.getParadasApuntadas().remove(paradaElim);
        }

        //Finalmente se elimina la parada
        paradas.remove(id);
    }


    /*MÉTODOS PARA ENCONTRAR RUTAS */

    class ParNodoDiscriminante implements Comparable<ParNodoDiscriminante>    //Par Nodo-Discriminante para la cola de prioridad en el algoritmo de Dijkstra, el nodo sería la parada
    {
        Parada nodo;          //nodo
        float discriminante;  //distancia conocida desde el origen hasta el nodo

        public ParNodoDiscriminante(Parada nodo, float discriminante)
        {
            this.nodo = nodo;
            this.discriminante = discriminante;
        }

        @Override
        public int compareTo(ParNodoDiscriminante otroPar)
        {
            return Float.compare(this.discriminante, otroPar.discriminante);
        }
    }

    public List<Parada> dijkstra(int idOrigen, int idDestino, boolean trueDist_FalseTiempo)  //retorna una lista con los nodos en la ruta más óptima (Solo funciona con distancias y tiempo, ya que estos solo pueden ser positivos)
    {
        if (!paradas.containsKey(idOrigen) || !paradas.containsKey(idDestino)) return null;

        Map<Integer, Float> discriminantes = new HashMap<>(paradas.size());
        Map<Integer, Parada> predecesores = new HashMap<>(paradas.size());
        Map<Integer, Boolean> visitados = new HashMap<>(paradas.size());
        for (Map.Entry<Integer,Parada> p : paradas.entrySet()) 
        {
            discriminantes.put(p.getKey(), Float.MAX_VALUE);     // Integer.MAX_VALUE representa infinito
            predecesores.put(p.getKey(), null);
            visitados.put(p.getKey(), false);
        }
        discriminantes.replace(idOrigen, 0.0f);

        PriorityQueue<ParNodoDiscriminante> colaPrio = new PriorityQueue<>();
        colaPrio.add(new ParNodoDiscriminante(paradas.get(idOrigen),0.0f));

        while(!colaPrio.isEmpty())
        {
            Parada nodoActual = colaPrio.poll().nodo;
            int idNodoActual = nodoActual.getId();
            visitados.replace(nodoActual.getId(), true);
            List<Ruta> rutasAdyacentes = nodoActual.getRutas();
            int i = 0;                                                          // índice por el cual va la lista

            for (Parada nodoAdyacente : nodoActual.getParadasApuntadas())
            {
                if (!visitados.get(nodoAdyacente.getId()))
                {
                    float discriminante;
                    int idNodoAdyacente = nodoAdyacente.getId();
                    if (trueDist_FalseTiempo) discriminante = rutasAdyacentes.get(i).getDistancia();
                    else discriminante = rutasAdyacentes.get(i).getTiempo();

                    float pesoActual = discriminante + discriminantes.get(idNodoActual);
                    if (pesoActual < discriminantes.get(idNodoAdyacente))
                    {
                        discriminantes.replace(idNodoAdyacente, pesoActual);
                        predecesores.replace(idNodoAdyacente, nodoActual);
                        colaPrio.add(new ParNodoDiscriminante(nodoAdyacente, discriminantes.get(idNodoAdyacente)));
                    }
                }
                i++;
            }
        }

        LinkedList<Parada> rutaOptima = new LinkedList<>();
        Parada predecesorActual = predecesores.get(idDestino);
        rutaOptima.push(paradas.get(idDestino));
        while (predecesorActual != null)
        {
            rutaOptima.push(predecesorActual);
            predecesorActual = predecesores.get(predecesorActual.getId());
        }

        return rutaOptima;
    }

}