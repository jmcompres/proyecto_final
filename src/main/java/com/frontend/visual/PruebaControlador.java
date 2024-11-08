package com.frontend.visual;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PruebaControlador{

    @FXML
    private Button boton;

    @FXML
    public void handleBotonPresionado() {
        System.out.println("¡Botón presionado!");
    }
}
