package com.frontend.visual;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class Controlador {

    @FXML
    public void insertar(ActionEvent e) {
        System.out.println("¡Insertar!");
    }

    public void modificar(ActionEvent e) {
        System.out.println("¡Modificar!");
    }

    public void eliminar(ActionEvent e) {
        System.out.println("¡Eliminar!");
    }
}
