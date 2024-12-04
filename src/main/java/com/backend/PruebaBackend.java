package com.backend;

import java.util.List;
import java.util.Map;

public class PruebaBackend {
    public static void main(String[] args) {

        GestorRutas g = GestorRutas.getInstance();

        // int id1 = g.agregarParada("p1", null);
        // int id2 = g.agregarParada("p2", null);
        // int id3 = g.agregarParada("p3", null);
        // int id4 = g.agregarParada("p4", null);
        // int id5 = g.agregarParada("p5", null);
        // int id6 = g.agregarParada("p6", null);

        // g.agregarRuta(id1, id2, 9, 0, 0);
        // g.agregarRuta(id1, id3, 4, 1, 0);
        // g.agregarRuta(id2, id3, 2, 0, 0);
        // g.agregarRuta(id3, id2, 2, 0, 0);
        // g.agregarRuta(id2, id5, 3, 0, 0);
        // g.agregarRuta(id2, id4, 7, 0, 0);
        // g.agregarRuta(id3, id4, 1, 1, 0);
        // g.agregarRuta(id3, id5, 6, 0, 0);
        // g.agregarRuta(id4, id5, 4, 0, 0);
        // g.agregarRuta(id5, id4, 4, 0, 0);
        // g.agregarRuta(id4, id6, 8, 0, 0);
        // g.agregarRuta(id5, id6, 2, 0, 0);
        // g.agregarRuta(id5, id6, 1, 0, 0);
        // g.agregarRuta(id5, id6, 3, 0, 0);

        // Preferencias[] prefs = {Preferencias.COSTO, Preferencias.NINGUNA, Preferencias.NINGUNA, Preferencias.NINGUNA, Preferencias.NINGUNA};

        // // List<ParParadaRuta> l = g.encontrarRuta(id1, id6, prefs);
        // // for (int i = 0; i<1000000000; i++)
        // // {
        // //     i++;
        // //     i--;
        // // }
        // // prefs[2] = Preferencias.COSTO;
        // // l = g.encontrarRuta(id1, id6, prefs);
        // // l = g.encontrarRuta(id1, id6, prefs);

        // g.floydWarshall(prefs);
        // Map<Integer,Map<Integer,List<ParParadaRuta>>> omniMapa = g.getRutasFloydWarshall();
        // List<ParParadaRuta> l = omniMapa.get(1).get(6);
        // l = g.encontrarRuta(id1, id6, prefs);

        // //List<Parada> l = g.dijkstra(id3, id6, false);
        // for (ParParadaRuta p : l)
        // {
        //     System.out.print(p.parada().getId());
        //     if (p.ruta() != null) System.out.println(" :"+p.ruta().getId()+": ");
        //     else System.out.println(" end");
        // }

        // System.out.println("");

        // //g.eliminarParada(id5);
        // l = g.dijkstra(id1, id6, prefs);

        // for (ParParadaRuta p : l)
        // {
        //     System.out.print(p.parada().getId());
        //     if (p.ruta() != null) System.out.println(" :"+p.ruta().getId()+": ");
        //     else System.out.println(" end");
        // }

        // //id5 = g.agregarParada("p5", null);
        // // g.agregarRuta(id4, id5, 4, 0, 0);
        // // g.agregarRuta(id5, id4, 4, 0, 0);
        // // g.agregarRuta(id5, id6, 2, 0, 0);
        // // g.agregarRuta(id2, id5, 3, 0, 0);
        // // g.agregarRuta(id3, id5, 6, 0, 0);
        // System.out.println("");
        // l = g.rutaTransbordosMinimos(id1, id6, prefs);
        // for (ParParadaRuta p : l)
        // {
        //     System.out.print(p.parada().getId());
        //     if (p.ruta() != null) System.out.println(" :"+p.ruta().getId()+": ");
        //     else System.out.println(" end");
        // }

        // int id10 = g.agregarParada("p10", null);
        // int id11 = g.agregarParada("p11", null);
        // int id12 = g.agregarParada("p12", null);
        // int id13 = g.agregarParada("p13", null);
        // int id14 = g.agregarParada("p14", null);
        // int id15 = g.agregarParada("p15", null);

        // g.agregarRuta(id10, id12, 9, 0, 0);
        // g.agregarRuta(id10, id11, 7, 0, 0);
        // g.agregarRuta(id10, id15, 14, 0, 0);
        // g.agregarRuta(id11, id12, 10, 0, 0);
        // g.agregarRuta(id11, id13, 15, 0, 0);
        // g.agregarRuta(id12, id13, 11, 0, 0);
        // g.agregarRuta(id12, id15, 2, 0, 0);
        // g.agregarRuta(id14, id13, 6, 0, 0);
        // g.agregarRuta(id15, id14, 9, 0, 0);

        // System.out.println("");

        // //List<Parada> l2 = g.dijkstra(id10, id14, false);
        // g.floydWarshall(prefs);
        // List<ParParadaRuta> l2 = g.getRutasFloydWarshall().get(id10).get(id14);
        // for (ParParadaRuta p : l2)
        // {
        //     System.out.print(p.parada().getId());
        //     if (p.ruta() != null) System.out.println(" :"+p.ruta().getId()+": ");
        //     else System.out.println(" end");
        // }

        // int idbf1 = g.agregarParada("p1bf", null);
        // int idbf2 = g.agregarParada("p2bf", null);
        // int idbf3 = g.agregarParada("p3bf", null);
        // int idbf4 = g.agregarParada("asd", null);

        // g.agregarRuta(idbf1, idbf2, 0, 0, 500);
        // g.agregarRuta(idbf2, idbf3, 0, 0, 5);
        // g.agregarRuta(idbf3,idbf4, 0, 0, 0);
        // g.agregarRuta(idbf4, idbf2, 0, 0, 0);
        // int rutaId = g.agregarRuta(idbf3, idbf2, 0, 0, 10);

        // System.out.println("");

        // l = g.bellmanFord(1, 6, prefs);
        // for (ParParadaRuta p : l)
        // {
        //     System.out.print(p.parada().getId());
        //     if (p.ruta() != null) System.out.println(" :"+p.ruta().getId()+": ");
        //     else System.out.println(" end");
        // }

        // g.modificarDescuentoRuta(1.51f, rutaId);





        /*PROBANDO EXPANSIÓN MÍNIMA*/
        Preferencias[] prefs = {Preferencias.TIEMPO, null, null, null, null};

        int idA = g.agregarParada("A", null);
        int idB = g.agregarParada("B", null);
        int idC = g.agregarParada("C", null);
        int idD = g.agregarParada("D", null);
        int idE = g.agregarParada("E", null);
        int idF = g.agregarParada("F", null);
        int idG = g.agregarParada("G", null);
        int idH = g.agregarParada("H", null);
        int idI = g.agregarParada("I", null);
        int idJ = g.agregarParada("J", null);
        int idK = g.agregarParada("K", null);

        g.agregarRuta(idA, idB, 8, 0, 0);  //1
        g.agregarRuta(idA, idH, 10, 0, 0); //2
        g.agregarRuta(idA, idI, 6, 0, 0);  //3
        g.agregarRuta(idA, idJ, 12, 0, 0); //4
        g.agregarRuta(idA, idK, 3, 0, 0);  //5

        g.agregarRuta(idB, idC, 10, 0, 0); //6
        g.agregarRuta(idB, idE, 2, 0, 0);  //7
        g.agregarRuta(idB, idK, 7, 0, 0);  //8

        g.agregarRuta(idC, idD, 9, 0, 0);  //9
        g.agregarRuta(idC, idK, 5, 0, 0);  //10

        g.agregarRuta(idD, idE, 13, 0, 0); //11
        g.agregarRuta(idD, idF, 12, 0, 0); //12

        g.agregarRuta(idE, idF, 10, 0, 0); //13
        g.agregarRuta(idE, idG, 6, 0, 0);  //14

        g.agregarRuta(idF, idG, 8, 0, 0);  //15

        g.agregarRuta(idG, idA, 9, 0, 0);  //16
        g.agregarRuta(idG, idH, 7, 0, 0);  //17

        g.agregarRuta(idH, idI, 3, 0, 0);  //18

        g.agregarRuta(idI, idJ, 10, 0, 0); //19

        g.agregarRuta(idJ, idK, 8, 0, 0);  //20

        Map<Integer,Ruta> m = g.kruskal(prefs);
        for (Ruta r : m.values())
        {
            System.out.println(r.getId());
        }

        System.out.println("");

        List<ParParadaRuta> l = g.rutaExpMin(idJ, idA, m);
        for (ParParadaRuta p : l)
        {
            System.out.print(p.parada().getId());
            if (p.ruta() != null) System.out.println(" :"+p.ruta().getId()+": ");
            else System.out.println(" end");
        }


        

        //GestorArchivos.guardarData();

    }
}
