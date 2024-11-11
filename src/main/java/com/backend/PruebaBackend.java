package com.backend;

import java.util.List;

public class PruebaBackend {
    public static void main(String[] args) {

        GestorRutas g = GestorRutas.getInstance();

        int id1 = g.agregarParada("p1", null);
        int id2 = g.agregarParada("p2", null);
        int id3 = g.agregarParada("p3", null);
        int id4 = g.agregarParada("p4", null);
        int id5 = g.agregarParada("p5", null);
        int id6 = g.agregarParada("p6", null);

        g.agregarRuta(id1, id2, 9, 0, 0);
        g.agregarRuta(id1, id3, 4, 0, 0);
        g.agregarRuta(id2, id3, 2, 0, 0);
        g.agregarRuta(id3, id2, 2, 0, 0);
        g.agregarRuta(id2, id5, 3, 0, 0);
        g.agregarRuta(id2, id4, 7, 0, 0);
        g.agregarRuta(id3, id4, 1, 0, 0);
        g.agregarRuta(id3, id5, 6, 0, 0);
        g.agregarRuta(id4, id5, 4, 0, 0);
        g.agregarRuta(id5, id4, 4, 0, 0);
        g.agregarRuta(id4, id6, 8, 0, 0);
        g.agregarRuta(id5, id6, 2, 0, 0);

        List<Parada> l = g.dijkstra(id1, id6, false);
        for (Parada p : l)
        {
            System.out.println(p.getId());
        }

        System.out.println("");

        g.eliminarParada(id5);
        l = g.dijkstra(id1, id6, false);

        for (Parada p : l)
        {
            System.out.println(p.getId());
        }

        id5 = g.agregarParada("p5", null);
        g.agregarRuta(id4, id5, 4, 0, 0);
        g.agregarRuta(id5, id4, 4, 0, 0);
        g.agregarRuta(id5, id6, 2, 0, 0);
        g.agregarRuta(id2, id5, 3, 0, 0);
        g.agregarRuta(id3, id5, 6, 0, 0);
        System.out.println("");
        l = g.rutaTransbordosMinimos(id1, id6, PrefTransbordos.TIEMPO);
        for (Parada p : l)
        {
            System.out.println(p.getId());
        }

    }
}
