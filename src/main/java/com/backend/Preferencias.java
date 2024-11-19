package com.backend;

public enum Preferencias { //Enum con valores para facilitar el acceso a las rutas de floyd-warshall según la preferencia principal

    COSTO(0),
    DISTANCIA(1),
    TIEMPO(2),
    TRANSBORDOS(3),
    NINGUNA(4);

    private final int valor;

    Preferencias(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
