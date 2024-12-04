package com.backend;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class GestorRutas implements Serializable{

    private static final long serialVersionUID = 1L;

    private static GestorRutas instancia = null;
    private static int capacidadInicial = 50;           //capacidad inicial para los ArrayLists con los nodos, paradas y rutas
    private static int idParadaActual = 1;              //Los ids se irán auto-asignando
    private static int idRutaActual = 1;

    private Map<Integer, Parada> paradas;               //la clave Integer es el atributo id de la parada
    private Map<Integer, Ruta> rutas;
    private Set<String> nombresParadas;                 //esto para comprobar en tiempo constante que no se repitan los nombres y ubicaciones

    //Atributos especiales para floyd-warshall
    private static Preferencias[] prefFWListo;
    private static AtomicBoolean fwlisto;
    private static AtomicBoolean fwEnProgreso;
    private static Map<Integer,Map<Integer,List<ParParadaRuta>>> rutasFloydWarshall;  //un mapa para cada discriminante como prioridad principal (excluyendo transbordos mínimos)

    //Expansión Mínima
    private static Map<Integer,Ruta> mstActual = null;
    private static boolean expMinActivado = false;


    /*CONSTRUCTOR, GETTERS Y SETTERS*/

    private GestorRutas()
    {
        paradas = new HashMap<Integer, Parada>(capacidadInicial);
        rutas = new HashMap<Integer, Ruta>(capacidadInicial);
        nombresParadas = new HashSet<String>(capacidadInicial);
        rutasFloydWarshall = new HashMap<Integer, Map<Integer, List<ParParadaRuta>>>(capacidadInicial);
        prefFWListo = null;
        fwlisto = new AtomicBoolean(false);
        fwEnProgreso = new AtomicBoolean(false);
    }

    public static GestorRutas getInstance()
    {
        //if (instancia == null) instancia = GestorArchivos.cargarData();
        if (instancia == null) instancia = new GestorRutas();
        return instancia;
    }

    public Map<Integer, Parada> getParadas() {
        return paradas;
    }

    public Map<Integer, Ruta> getRutas() {
        return rutas;
    }

    public int getIdParadaActual() {return idParadaActual;}

    public Map<Integer, Map<Integer, List<ParParadaRuta>>> getRutasFloydWarshall() {
        return rutasFloydWarshall;
    }

    public boolean getExpMinActivado() {
        return expMinActivado;
    }

    public void setExpMin(boolean val)
    {
        expMinActivado = val;
    }

    

    /*MÉTODOS DE GESTIÓN DE PARADAS/RUTAS*/

    public int agregarParada(String nombre, Localizacion localizacion) //retorna el índice de la nueva parada
    {
        if (nombresParadas.contains(nombre)) return -1;

        Parada neoParada = new Parada(idParadaActual, nombre, localizacion);
        paradas.put(idParadaActual,neoParada);
        nombresParadas.add(nombre);
        idParadaActual += 1;

        return idParadaActual-1;
    }

    public int agregarRuta(int idParadaFuente, int idParadaDestino, float tiempo, float distancia, float costo)
    {
        if (!paradas.containsKey(idParadaDestino) || !paradas.containsKey(idParadaFuente)) return -1;

        Parada pFuente = paradas.get(idParadaFuente);
        Parada pDestino = paradas.get(idParadaDestino);

        Ruta neoRuta = new Ruta(pFuente, pDestino, idRutaActual, tiempo, distancia, costo);
        pFuente.agregarAdyacencia(pDestino, neoRuta);
        rutas.put(idRutaActual, neoRuta);
        idRutaActual++;
        return neoRuta.getId();
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

    public boolean modificarDescuentoRuta(float neoDescuento, int idRuta)
    {
        if (!rutas.containsKey(idRuta)) return false;

        Ruta ruta = rutas.get(idRuta);
        Parada pOrigen = ruta.getOrigen();
        Parada pDestino = ruta.getDestino();
        float descOrg = ruta.getDescuento();

        //Se prueba si el descuento genera un ciclo negativo (se puede confiar el bellman-ford para esto, ya que la existencia de la ruta asegura que exista un camino entre las dos paradas, entonces la única manera de que retorne null es si hay un ciclo negativo)
        ruta.setDescuento(neoDescuento);
        Preferencias[] prefsAux = {Preferencias.COSTO, Preferencias.TIEMPO, Preferencias.NINGUNA, Preferencias.NINGUNA, Preferencias.NINGUNA};
        if (bellmanFord(pOrigen.getId(), pDestino.getId(), prefsAux) == null)
        {
            //Si genera un ciclo negativo, entonces no se agrega el descuento
            System.out.println("No se puede agregar tal descuento, pues habría un ciclo negativo");
            ruta.setDescuento(descOrg);
            return false;
        }
        return true;
    }





    
    /*MÉTODOS PARA ENCONTRAR RUTAS */

    //A este método se le pasa un registro de predecesores (calculado por cualquier método) y retorna la lista de paradas hasta la parada destino
    private List<ParParadaRuta> rutaOptima(Map<Integer,ParParadaRuta> predecesores, int idDestino)
    {
        if (predecesores.get(idDestino) == null) return null; //Si no están en un componente conectado

        LinkedList<ParParadaRuta> rOptima = new LinkedList<>();
        rOptima.push(new ParParadaRuta(paradas.get(idDestino), null));
        ParParadaRuta predecesorActual = predecesores.get(idDestino);
        while (predecesorActual != null)
        {
            rOptima.push(predecesorActual);
            predecesorActual = predecesores.get(predecesorActual.parada().getId());
        }

        return rOptima;
    }

    private void llenarInfoBasica(Map<Integer, RegistroDiscriminates> discriminantes, Map<Integer,ParParadaRuta> predecesores, Map<Integer,Boolean> visitados, Map<Integer,Integer> distancias)  //Con distancias nos referimos en términos de transbordos
    {
        for (Map.Entry<Integer,Parada> p : paradas.entrySet()) 
        {
            discriminantes.put(p.getKey(), new RegistroDiscriminates(new Ruta(null,null,-1,Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE), null, true, false));     // Integer.MAX_VALUE representa infinito
            predecesores.put(p.getKey(), null);
            visitados.put(p.getKey(), false);
            if (distancias != null) distancias.put(p.getKey(), Integer.MAX_VALUE);
        }
    }

    //Esto se puede colocar en un for
    private int compararMultiPrefs(RegistroDiscriminates discr1, RegistroDiscriminates discr2, Preferencias[] prefs)
    {
        int res = Float.compare(discr1.discrs[prefs[0].getValor()], discr2.discrs[prefs[0].getValor()]);
        if (res != 0 || prefs[1] == Preferencias.NINGUNA || prefs[1] == null) return res;
        res = Float.compare(discr1.discrs[prefs[1].getValor()], discr2.discrs[prefs[1].getValor()]);
        if (res != 0 || prefs[2] == Preferencias.NINGUNA || prefs[2] == null) return res;
        res = Float.compare(discr1.discrs[prefs[2].getValor()], discr2.discrs[prefs[2].getValor()]);
        if (res != 0 || prefs[3] == Preferencias.NINGUNA || prefs[3] == null) return res;
        res = Float.compare(discr1.discrs[prefs[3].getValor()], discr2.discrs[prefs[3].getValor()]);
        return res; //son máximo cuatro preferencias
    }


    private class ParNodoDiscriminante implements Comparable<ParNodoDiscriminante>    //Par Nodo-Discriminante para la cola de prioridad en el algoritmo de Dijkstra, el nodo sería la parada
    {
        Parada nodo;          //nodo
        RegistroDiscriminates discriminante;  //distancia conocida desde el origen hasta el nodo
        Preferencias[] preferencias;

        public ParNodoDiscriminante(Parada nodo, RegistroDiscriminates discriminante, Preferencias[] preferencias)
        {
            this.nodo = nodo;
            this.discriminante = discriminante;
            this.preferencias = preferencias;
        }

        @Override
        public int compareTo(ParNodoDiscriminante otroPar)
        {
            return compararMultiPrefs(this.discriminante, otroPar.discriminante, preferencias);
        }
    }

    private record RegistroDiscriminates(float[] discrs) {

        //Constructor para inicializar en base a una ruta
        public RegistroDiscriminates(Ruta r, RegistroDiscriminates regPrevio, boolean relleno, boolean origen) {
            this(new float[] {
                (r == null? 0 : r.getCostoNeto()) + (regPrevio == null ? 0 : regPrevio.discrs[0]),
                (r == null? 0 : r.getDistancia()) + (regPrevio == null ? 0 : regPrevio.discrs[1]),
                (r == null? 0 : r.getTiempo()) + (regPrevio == null ? 0 : regPrevio.discrs[2]),
                (regPrevio == null ? 0 : regPrevio.discrs[3]) + (relleno? Float.MAX_VALUE : (origen? 0 : 1)),
                (r == null? 0 : r.getTiempo()) + (regPrevio == null ? 0 : regPrevio.discrs[4])                 //Si se elige NINGUNA como prioridad, entonces se toma por defecto el tiempo
            });
        }

        //Constructor para inicializar en base a dos registros
        public RegistroDiscriminates(RegistroDiscriminates reg1, RegistroDiscriminates reg2) {
            this(new float[] {
                (reg1 == null? 0 : reg1.discrs[0]) + (reg2 == null? 0 : reg2.discrs[0]),
                (reg1 == null? 0 : reg1.discrs[1]) + (reg2 == null? 0 : reg2.discrs[1]),
                (reg1 == null? 0 : reg1.discrs[2]) + (reg2 == null? 0 : reg2.discrs[2]),
                (reg1 == null? 0 : reg1.discrs[3]) + (reg2 == null? 0 : reg2.discrs[3]),
                (reg1 == null? 0 : reg1.discrs[4]) + (reg2 == null? 0 : reg2.discrs[4]),                 //Si se elige NINGUNA como prioridad, entonces se toma por defecto el tiempo
            });
        }
    }

    public List<ParParadaRuta> dijkstra(int idOrigen, int idDestino, Preferencias preferencias[])  //retorna una lista con los nodos en la ruta más óptima (Solo funciona con distancias y tiempo, ya que estos solo pueden ser positivos)
    {
        if (!paradas.containsKey(idOrigen) || !paradas.containsKey(idDestino)) return null;

        Map<Integer, RegistroDiscriminates> discriminantes = new HashMap<>(paradas.size());
        Map<Integer, ParParadaRuta> predecesores = new HashMap<>(paradas.size());
        Map<Integer, Boolean> visitados = new HashMap<>(paradas.size());
        llenarInfoBasica(discriminantes, predecesores, visitados, null);
        discriminantes.replace(idOrigen, new RegistroDiscriminates(null,null,false,true));

        PriorityQueue<ParNodoDiscriminante> colaPrio = new PriorityQueue<>();
        colaPrio.add(new ParNodoDiscriminante(paradas.get(idOrigen),discriminantes.get(idOrigen), preferencias));

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
                    RegistroDiscriminates discriminanteActual = new RegistroDiscriminates(rutasAdyacentes.get(i), discriminantes.get(idNodoActual), false, false);
                    if (compararMultiPrefs(discriminanteActual, discriminantes.get(idNodoAdyacente), preferencias)<0)
                    {
                        discriminantes.replace(idNodoAdyacente, discriminanteActual);
                        predecesores.replace(idNodoAdyacente, new ParParadaRuta(nodoActual, rutasAdyacentes.get(i)));
                        colaPrio.add(new ParNodoDiscriminante(nodoAdyacente, discriminantes.get(idNodoAdyacente), preferencias));
                    }
                }
                i++;
            }
        }

        return rutaOptima(predecesores,idDestino);
    }

    public List<ParParadaRuta> rutaTransbordosMinimos(int idOrigen, int idDestino, Preferencias[] preferencias) //Búsqueda de amplitud modificada para trabajar con preferencias
    {
        if (!paradas.containsKey(idDestino) || !paradas.containsKey(idOrigen)) return null;

        Map<Integer, RegistroDiscriminates> discriminantes = new HashMap<>(paradas.size());
        Map<Integer, ParParadaRuta> predecesores = new HashMap<>(paradas.size());
        Map<Integer, Boolean> visitados = new HashMap<>(paradas.size());
        Map<Integer, Integer> distancias = new HashMap<>(paradas.size());
        llenarInfoBasica(discriminantes, predecesores, visitados, distancias);
        discriminantes.replace(idOrigen, new RegistroDiscriminates(null, null, false, true));
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
            if (distanciaEncontrada >= 0 && distancias.get(idActual) > distanciaEncontrada) break;

            for (Parada pAdyacente : paradaActual.getParadasApuntadas())
            {
                int idAdyacente = pAdyacente.getId();

                //Si aún está en la lista, entonces su peso se puede reconsiderar
                if ((visitados.get(idAdyacente) && (!cola.contains(pAdyacente) || distancias.get(idAdyacente)<=distancias.get(idActual)) && distanciaEncontrada==-1) || (distanciaEncontrada!=-1 && idAdyacente!=idDestino))
                {
                    i++;
                    continue;
                }

                distancias.replace(idAdyacente, distancias.get(idActual) + 1);
                if (idAdyacente == idDestino) distanciaEncontrada = distancias.get(idAdyacente);  //Encontrada

                RegistroDiscriminates discriminanteActual = new RegistroDiscriminates(paradaActual.getRutas().get(i), discriminantes.get(idActual), false, false);
                if (compararMultiPrefs(discriminanteActual, discriminantes.get(idAdyacente), preferencias) < 0)
                {
                    discriminantes.replace(idAdyacente, discriminanteActual);
                    predecesores.replace(idAdyacente, new ParParadaRuta(paradaActual, paradaActual.getRutas().get(i)));
                }
                visitados.replace(idAdyacente, true);
                if (!cola.contains(pAdyacente)) cola.offer(pAdyacente);

                i++;
            }
        }

        return rutaOptima(predecesores, idDestino);
    }

    public List<ParParadaRuta> bellmanFord(int idOrigen, int idDestino, Preferencias[] preferencias){
        Map<Integer, RegistroDiscriminates> discriminantes = new HashMap<>(paradas.size());
        Map<Integer, ParParadaRuta> predecesores = new HashMap<>(paradas.size());
        Map<Integer, Boolean> enCola = new HashMap<>(paradas.size());
        llenarInfoBasica(discriminantes, predecesores, enCola, null);

        discriminantes.replace(idOrigen, new RegistroDiscriminates(null, null, false, true));

        for (int i = 0; i<paradas.size()-1; i++)
        {
            for (Ruta r : rutas.values())
            {
                int idOrigenActual = r.getOrigen().getId();
                int idDestinoActual = r.getDestino().getId();
                RegistroDiscriminates discrActual = new RegistroDiscriminates(r, discriminantes.get(idOrigenActual), false, false);
                if (compararMultiPrefs(discrActual, discriminantes.get(idDestinoActual), preferencias)<0)
                {
                    discriminantes.replace(idDestinoActual, discrActual);
                    predecesores.replace(idDestinoActual, new ParParadaRuta(paradas.get(idOrigenActual), r));
                }
            }
        }

        //comprobar si existen ciclos negativos
        for (Ruta r : rutas.values())
        {
            RegistroDiscriminates discrActual = new RegistroDiscriminates(r, discriminantes.get(r.getOrigen().getId()), false, false);
            if (compararMultiPrefs(discrActual, discriminantes.get(r.getDestino().getId()), preferencias) < 0)
            {
                System.out.println("Existe un ciclo negativo");
                return null;
            }
        }

        return rutaOptima(predecesores, idDestino);
    }

    public synchronized void floydWarshall(Preferencias[] preferencias) {
        fwEnProgreso.set(true);
        // Información básica para el algoritmo
        Map<Integer,Map<Integer,List<ParParadaRuta>>> mapaPreferencia = new HashMap<Integer,Map<Integer,List<ParParadaRuta>>>();
        Map<Integer, Map<Integer, RegistroDiscriminates>> discriminantes = new HashMap<>(paradas.size());
    
        // Inicialización de información básica para el algoritmo
        for (Parada p : paradas.values()) {
            int idP = p.getId();
            mapaPreferencia.put(idP, new HashMap<>());
            discriminantes.put(idP, new HashMap<>());
    
            // Distancia a sí mismo es 0
            discriminantes.get(idP).put(idP, new RegistroDiscriminates(null,null,false,true));
            mapaPreferencia.get(idP).put(idP, new LinkedList<>());
            mapaPreferencia.get(idP).get(idP).add(new ParParadaRuta(p,null));

            for (Parada p2 : paradas.values()) //incializar con todas las paradas
            {
                int idp2 = p2.getId();
                discriminantes.get(idP).put(idp2, new RegistroDiscriminates(new Ruta(null,null,-1,Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE), null, true, false)); //infinito
                mapaPreferencia.get(idP).put(idp2, new LinkedList<>());
            }
            
            int i = 0;
            for (Parada pAdyacente : p.getParadasApuntadas()) {
                int idpa = pAdyacente.getId();
                // Inicializar rutas de paradas adyacentes

                // Inicializar discriminantes
                Ruta r = p.getRutas().get(i);
                RegistroDiscriminates discr = new RegistroDiscriminates(r, null, false, false);

                if (mapaPreferencia.get(idP).get(idpa).isEmpty() || (!mapaPreferencia.get(idP).get(idpa).isEmpty() && (compararMultiPrefs(discr, discriminantes.get(idP).get(idpa), preferencias)<0))) //ya que puede haber más de una ruta entre dos nodos, entonces se escoge la menor
                {
                    mapaPreferencia.get(idP).replace(idpa, new LinkedList<>());
                    mapaPreferencia.get(idP).get(idpa).add(new ParParadaRuta(p, p.getRutas().get(i))); // Incluye el origen
                    mapaPreferencia.get(idP).get(idpa).add(new ParParadaRuta(pAdyacente, null)); // Incluye el destino
                    discriminantes.get(idP).put(idpa, discr);
                }

                i++;
            }
        }
    
        // Algoritmo
        for (Parada pk : paradas.values()) {
            int k = pk.getId();
            for (Parada pi : paradas.values()) {
                int i = pi.getId();
                for (Parada pj : paradas.values()) {
                    int j = pj.getId();
                    RegistroDiscriminates discrActual = new RegistroDiscriminates(discriminantes.get(i).get(k), discriminantes.get(k).get(j));
                    if (compararMultiPrefs(discrActual, discriminantes.get(i).get(j), preferencias)<0) {
                        discriminantes.get(i).replace(j, discrActual);
                        List<ParParadaRuta> nuevaRuta = new LinkedList<>(mapaPreferencia.get(i).get(k));
                        nuevaRuta.remove(nuevaRuta.size()-1); // Para que no se repita, por ser la última de la primera ruta, y la primera de la segunda ruta (hablando de las rutas a fusionar)
                        nuevaRuta.addAll(mapaPreferencia.get(k).get(j));
                        mapaPreferencia.get(i).replace(j, nuevaRuta);
                    }
                }
            }
        }

        rutasFloydWarshall = mapaPreferencia;
        prefFWListo = preferencias.clone();
        fwlisto.set(true);
        fwEnProgreso.set(false);
    }


    /*ALGORITMOS DE EXPANSIÓN MÍNIMA */

    //Este algoritmo está implementado, pero no se utiliza en el proyecto, pues el grafo es dirigido y este algoritmo no funciona para grafos dirigidos
    public Map<Integer,Ruta> prim(Preferencias[] preferencias){
        Map<Integer, RegistroDiscriminates> discriminantes = new HashMap<>(paradas.size());
        Map<Integer, ParParadaRuta> predecesores = new HashMap<>(paradas.size());
        Map<Integer, Boolean> enMST = new HashMap<>(paradas.size());
        Map<Integer,Ruta> rutasMST = new HashMap<>(paradas.size());
        llenarInfoBasica(discriminantes, predecesores, enMST, null);

        Parada paradaInicial = paradas.values().iterator().next(); // Comenzar desde el nodo 1
        discriminantes.replace(paradaInicial.getId(), new RegistroDiscriminates(null, null, false, true));

        PriorityQueue<ParNodoDiscriminante> cola = new PriorityQueue<>();
        cola.add(new ParNodoDiscriminante(paradaInicial,discriminantes.get(paradaInicial.getId()), preferencias));

        while(!cola.isEmpty())
        {
            Parada nodoActual = cola.poll().nodo;
            int idNodoActual = nodoActual.getId();

            if(!enMST.get(idNodoActual)){
                enMST.replace(idNodoActual, true);

                Parada predecesor;
                if (idNodoActual == paradaInicial.getId()) predecesor = null;
                else predecesor = predecesores.get(idNodoActual).parada();

                if (predecesor != null) {
                    for (Ruta ruta : predecesor.getRutas()) {
                        if (ruta.getDestino().equals(nodoActual)) {
                            rutasMST.put(ruta.getId(), ruta);
                            break;
                        }
                    }
                }

                for(Ruta ruta: nodoActual.getRutas())
                {
                    Parada destino = ruta.getDestino();
                    RegistroDiscriminates peso = new RegistroDiscriminates(ruta, null, false, false);

                    if(!enMST.get(destino.getId()) && (compararMultiPrefs(peso, discriminantes.get(destino.getId()), preferencias)<0) )
                    {
                        discriminantes.replace(destino.getId(), peso);
                        predecesores.replace(destino.getId(), new ParParadaRuta(nodoActual, ruta));
                        cola.add(new ParNodoDiscriminante(destino, peso, preferencias));
                    }
                }
            }
        }
        return rutasMST;
    }

    class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public boolean union(int x, int y) {
            int xRoot = find(x);
            int yRoot = find(y);
            if (xRoot == yRoot) {
                return false;
            }
            if (rank[xRoot] < rank[yRoot]) {
                parent[xRoot] = yRoot;
            } else if (rank[xRoot] > rank[yRoot]) {
                parent[yRoot] = xRoot;
            } else {
                parent[yRoot] = xRoot;
                rank[xRoot]++;
            }
            return true;
        }
    }

    private class ParRutaDiscriminante implements Comparable<ParRutaDiscriminante>    //Par Nodo-Discriminante para la cola de prioridad en el algoritmo de Dijkstra, el nodo sería la parada
    {
        Ruta ruta;
        RegistroDiscriminates discriminante;
        Preferencias[] preferencias;

        public ParRutaDiscriminante(Ruta ruta, RegistroDiscriminates discriminante, Preferencias[] preferencias)
        {
            this.ruta = ruta;
            this.discriminante = discriminante;
            this.preferencias = preferencias;
        }

        @Override
        public int compareTo(ParRutaDiscriminante otroPar)
        {
            return compararMultiPrefs(this.discriminante, otroPar.discriminante, preferencias);
        }
    }

    public Map<Integer,Ruta> kruskal(Preferencias[] preferencias){
        Map<Integer, Ruta> mst = new HashMap<>();
        PriorityQueue<ParRutaDiscriminante> pq = new PriorityQueue<>();
        for (Ruta r : rutas.values()) pq.offer(new ParRutaDiscriminante(r, new RegistroDiscriminates(r, null, false, false), preferencias));
        UnionFind uf = new UnionFind(paradas.size());

        while(mst.size() < paradas.size() - 1 && !pq.isEmpty()){
            Ruta ruta = pq.poll().ruta;
            int origen = ruta.getOrigen().getId()-1;
            int destino = ruta.getDestino().getId()-1;
            if(uf.union(origen, destino)){
                mst.put(ruta.getId(), ruta);
                uf.union(origen, destino);
            }
        }

        return mst;
    }

    //Método para comprobar si las preferencias con las que se están una ruta óptima son iguales a las del mapa floyd-warshall generado
    private boolean prefsIguales(Preferencias prefs[])
    {
        if (prefFWListo == null) return false;
        boolean respuesta = true;

        for (int i = 0; i<5; i++) //5 es el número de preferencias posibles
        {
            if (prefs[i] != prefFWListo[i])
            {
                respuesta = false;
                break;
            }
        }

        return respuesta;
    }

    //Método para encontrar ruta óptima en expansión mínima (ya que lo que queda es un árbol, solo hay una ruta entre paradas, por lo que es un simple DFS)
    public List<ParParadaRuta> rutaExpMin(int idOrigen, int idDestino, Map<Integer, Ruta> arbolExpMin)
    {
        if (!paradas.containsKey(idOrigen) || !paradas.containsKey(idDestino)) return null;

        List<ParParadaRuta> ruta = new LinkedList<ParParadaRuta>();
        Map<Integer, Boolean> paradasVisitadas = new HashMap<Integer, Boolean>();
        for (Parada p : paradas.values())
            paradasVisitadas.put(p.getId(), false);
        paradasVisitadas.replace(idOrigen, true);

        ruta.addAll(recursionRutaExpMin(idOrigen, idDestino, arbolExpMin, paradasVisitadas));

        if (ruta.isEmpty()) return null;
        return ruta;
    }
    public List<ParParadaRuta> recursionRutaExpMin(int idActual, int idDestino, Map<Integer, Ruta> arbolExpMin, Map<Integer, Boolean> paradasVisitadas)
    {
        paradasVisitadas.replace(idActual, true);

        Parada pActual = paradas.get(idActual);

        //El árbol de expansión mínima se tratará como grafo no dirigido, ya que kruskal no da resultados coherentes para grafos dirigidos, por eso se itera por las paradas apuntadas y las apuntadoras, y no se toma en cuenta la dirección de la ruta

        List<ParParadaRuta> listaTemp = new LinkedList<>();
        int i = 0; //ínidice por el cuál va la iteración de paradas
        for (Parada p : pActual.getParadasApuntadas())
        {
            if (!arbolExpMin.containsKey(pActual.getRutas().get(i).getId()) || paradasVisitadas.get(p.getId()))
            {
                i++;
                continue;
            }

            listaTemp.add(new ParParadaRuta(pActual, pActual.getRutas().get(i)));
            if (p.getId() == idDestino)
            {
                listaTemp.add(new ParParadaRuta(p, null));
                return listaTemp;
            }
            listaTemp.addAll(recursionRutaExpMin(p.getId(), idDestino, arbolExpMin, paradasVisitadas));
            if (listaTemp.size()>1) return listaTemp; //si el tamaño aumenta, entonces se encontró la ruta
            listaTemp.remove(0); //si
            i++;
        }
        for (Parada p : pActual.getParadasApuntadoras())
        {
            i = 0;
            if (paradasVisitadas.get(p.getId())) continue;
            for (Parada subp : p.getParadasApuntadas())
            {
                if (subp.getId() != pActual.getId() || !arbolExpMin.containsKey(p.getRutas().get(i).getId()))
                {
                    i++;
                    continue;
                }

                listaTemp.add(new ParParadaRuta(pActual, p.getRutas().get(i)));
                if (p.getId() == idDestino)
                {
                    listaTemp.add(new ParParadaRuta(p, p.getRutas().get(i)));
                    return listaTemp;
                }
                listaTemp.addAll(recursionRutaExpMin(p.getId(), idDestino, arbolExpMin, paradasVisitadas));
                if (listaTemp.size()>1) return listaTemp;
                listaTemp.remove(0);
                i++;
            }
        }

        return new LinkedList<>(); //si no se encontró por este lado, entonces se retorna una lista vación para indicar que no se encontró
    }


    //Método definitivo
    public List<ParParadaRuta> encontrarRuta(int idOrigen, int idDestino, Preferencias preferencias[])
    {
        if (expMinActivado)
        {
            if (mstActual == null) mstActual = kruskal(preferencias);
            return rutaExpMin(idOrigen, idDestino, mstActual);
        }

        if(!prefsIguales(preferencias)) fwlisto.set(false);
        if (prefFWListo!=null && fwlisto.get()) return rutasFloydWarshall.get(idOrigen).get(idDestino);
        else if (!fwEnProgreso.get()) //evitar que hayan dos hilos haciendo la misma tarea y modificando la misma info
        {
            Thread alistarFW = new Thread(()-> floydWarshall(preferencias));
            alistarFW.start();
        }

        List<ParParadaRuta> ruta;
        switch (preferencias[0]) {
            case COSTO:
                ruta = bellmanFord(idOrigen, idDestino, preferencias);
                break;
            case TRANSBORDOS:
                ruta = rutaTransbordosMinimos(idOrigen, idDestino, preferencias);
                break;
            default:
                ruta = dijkstra(idOrigen, idDestino, preferencias);
                break;
        }

        return ruta;
    }
    
}