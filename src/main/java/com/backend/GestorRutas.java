package com.backend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class GestorRutas {

    private static GestorRutas instancia = null;
    private static int capacidadInicial = 50;           //capacidad inicial para los ArrayLists con los nodos, paradas y rutas
    private static int idParadaActual = 1;              //Los ids se irán auto-asignando
    private static int idRutaActual = 1;

    private Map<Integer, Parada> paradas;               //la clave Integer es el atributo id de la parada
    private Map<Integer, Ruta> rutas;
    private Set<String> nombresParadas;                 //esto para comprobar en tiempo constante que no se repitan los nombres y ubicaciones

    private GestorRutas()
    {
        paradas = new HashMap<Integer, Parada>(capacidadInicial);
        rutas = new HashMap<Integer, Ruta>(capacidadInicial);
        nombresParadas = new HashSet<String>(capacidadInicial);
    }

    public static GestorRutas getInstance()
    {
        if (instancia == null) instancia = new GestorRutas();
        return instancia;
    }

    public Map<Integer, Parada> getParadas() {
        return paradas;
    }

    public Map<Integer, Ruta> getRutas() {
        return rutas;
    }

    /*OTROS MÉTODOS*/

    public int agregarParada(String nombre, Localizacion localizacion) //retorna el índice de la nueva parada
    {
        if (nombresParadas.contains(nombre)) return -1;

        Parada neoParada = new Parada(idParadaActual, nombre, localizacion);
        paradas.put(idParadaActual,neoParada);
        nombresParadas.add(nombre);
        idParadaActual += 1;

        return idParadaActual-1;
    }

    public void agregarRuta(int idParadaFuente, int idParadaDestino, float tiempo, float distancia, float costo)
    {
        if (!paradas.containsKey(idParadaDestino) || !paradas.containsKey(idParadaFuente)) return;

        Parada pFuente = paradas.get(idParadaFuente);
        Parada pDestino = paradas.get(idParadaDestino);

        Ruta neoRuta = new Ruta(pFuente, pDestino, idRutaActual, tiempo, distancia, costo);
        pFuente.agregarAdyacencia(pDestino, neoRuta);
        rutas.put(idRutaActual, neoRuta);
        idRutaActual++;
    }

    public void eliminarParada(int idParada)
    {
        if (!paradas.containsKey(idParada)) return;

        //Primero se eliminan todas las conexiones de todos los nodos que apuntaban a la parada a eliminar
        Parada paradaElim = paradas.get(idParada);
        Set<Parada> paradasApuntadoras = paradaElim.getParadasApuntadoras();
        for (Parada p : paradasApuntadoras)
        {
            List<Parada> paradasApuntadasActuales = p.getParadasApuntadas();
            for (int i = 0; i<paradasApuntadasActuales.size(); i++)
            {
                if (paradasApuntadasActuales.get(i) == paradaElim)
                {
                    paradasApuntadasActuales.remove(i);
                    Ruta rElim = p.getRutas().get(i);
                    rutas.remove(rElim.getId());
                    p.getRutas().remove(i);
                    i--;
                }
            }
        }

        //Finalmente se elimina la parada
        nombresParadas.remove(paradaElim.getNombre());
        paradas.remove(idParada);
    }

    public void eliminarRuta(int idRuta)
    {
        if (!rutas.containsKey(idRuta)) return;

        Ruta rutaElim = rutas.get(idRuta);
        Parada pOrigen = rutaElim.getOrigen();
        Parada pDestino = rutaElim.getDestino();

        int idRutaElim = pOrigen.getRutas().indexOf(rutaElim);  //Se tiene que trabajar con índices, ya que se pueden repetir paradas (ya que están en orden con sus respectivas rutas, y pueden haber más de una ruta a la misma parada)
        pOrigen.getRutas().remove(rutaElim);
        pOrigen.getParadasApuntadas().remove(idRutaElim);       //Está en orden con la ruta a eliminar
        if (!pOrigen.getParadasApuntadas().contains(pDestino))  //Si la ruta a la parada era la única, entonces se elimina de paradas apuntadoras en la ruta destino
            pDestino.getParadasApuntadoras().remove(pOrigen);
        rutas.remove(idRuta);
    }



    /*MÉTODOS PARA ENCONTRAR RUTAS */

    //A este método se le pasa un registro de predecesores (calculado por cualquier método) y retorna la lista de paradas hasta la parada destino
    private List<Parada> rutaOptima(Map<Integer,Parada> predecesores, int idDestino)
    {
        LinkedList<Parada> rOptima = new LinkedList<>();
        Parada predecesorActual = predecesores.get(idDestino);
        rOptima.push(paradas.get(idDestino));
        while (predecesorActual != null)
        {
            rOptima.push(predecesorActual);
            predecesorActual = predecesores.get(predecesorActual.getId());
        }

        return rOptima;
    }

    private void llenarInfoBasica(Map<Integer, Float> discriminantes, Map<Integer,Parada> predecesores, Map<Integer,Boolean> visitados, Map<Integer,Integer> distancias)  //Con distancias nos referimos en términos de transbordos
    {
        for (Map.Entry<Integer,Parada> p : paradas.entrySet()) 
        {
            discriminantes.put(p.getKey(), Float.MAX_VALUE);     // Integer.MAX_VALUE representa infinito
            predecesores.put(p.getKey(), null);
            visitados.put(p.getKey(), false);
            if (distancias != null) distancias.put(p.getKey(), Integer.MAX_VALUE);
        }
    }

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
        llenarInfoBasica(discriminantes, predecesores, visitados, null);
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
                int idNodoAdyacente = nodoAdyacente.getId();
                if (!visitados.get(idNodoAdyacente))
                {
                    float discriminante;
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

        if (predecesores.get(idDestino) == null) return null; //Si no están en un componente conectado
        return rutaOptima(predecesores,idDestino);
    }

    public List<Parada> rutaTransbordosMinimos(int idOrigen, int idDestino, PrefTransbordos pref) //Búsqueda de amplitud modificada para trabajar con preferencias
    {
        if (!paradas.containsKey(idDestino) || !paradas.containsKey(idOrigen)) return null;

        Map<Integer, Float> discriminantes = new HashMap<>(paradas.size());
        Map<Integer, Parada> predecesores = new HashMap<>(paradas.size());
        Map<Integer, Boolean> visitados = new HashMap<>(paradas.size());
        Map<Integer, Integer> distancias = new HashMap<>(paradas.size());
        llenarInfoBasica(discriminantes, predecesores, visitados, distancias);
        discriminantes.replace(idOrigen, 0.0f);
        distancias.replace(idOrigen, 0);

        Parada pOrigen = paradas.get(idOrigen);
        Queue<Parada> cola = new LinkedList<Parada>();
        cola.offer(pOrigen);
        int distanciaEncontrada = -1;    //Distancia (en términos de transbordos) a la cual se encontró la parada destino (-1 es un flag)

        while(!cola.isEmpty())
        {
            Parada paradaActual = cola.poll();
            int idActual = paradaActual.getId();
            int i = 0;                              //índice por el cual va la lista de adyacencia
            if (distanciaEncontrada >= 0 && distancias.get(idActual) >= distanciaEncontrada) break;

            for (Parada pAdyacente : paradaActual.getParadasApuntadas())
            {
                int idAdyacente = pAdyacente.getId();

                //Si aún está en la lista, entonces su peso se puede reconsiderar
                if ((visitados.get(idAdyacente) && (!cola.contains(pAdyacente) || distancias.get(idAdyacente)>=distancias.get(idActual)) && distanciaEncontrada==-1) || (distanciaEncontrada!=-1 && idAdyacente!=idDestino))
                {
                    i++;
                    continue;
                }

                distancias.replace(idAdyacente, distancias.get(idActual) + 1);
                if (idAdyacente == idDestino) distanciaEncontrada = distancias.get(idAdyacente);  //Encontrada

                float discriminanteAdyacente = 0;
                if (pref == PrefTransbordos.COSTO) discriminanteAdyacente = paradaActual.getRutas().get(i).getCosto();
                else if (pref == PrefTransbordos.TIEMPO) discriminanteAdyacente = paradaActual.getRutas().get(i).getTiempo();
                else if (pref == PrefTransbordos.DISTANCIA) discriminanteAdyacente = paradaActual.getRutas().get(i).getDistancia();

                if (discriminantes.get(idActual) + discriminanteAdyacente < discriminantes.get(idAdyacente))
                {
                    discriminantes.replace(idAdyacente, discriminantes.get(idActual) + discriminanteAdyacente);
                    predecesores.replace(idAdyacente, paradaActual);
                }
                visitados.replace(idAdyacente, true);
                if (!cola.contains(pAdyacente)) cola.offer(pAdyacente);

                i++;
            }
        }

        if (distanciaEncontrada == -1) return null; //Si no están conectadas
        return rutaOptima(predecesores, idDestino);
    }

}