package com.frontend.visual;

import com.backend.GestorRutas;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.awt.*;

public class Controlador {


    @FXML
    Pane panelAgregar;
    @FXML
    TextField txtNombre;
    @FXML
    TextField txtLocalizacion;

    public void agregarParada(ActionEvent e){
        panelAgregar.setVisible(true);
    }

    public void agregarP(ActionEvent e) {
        GestorRutas.getInstance().agregarParada(txtNombre.getText(), txtLocalizacion.getText());
    }

    public void modificarParada(ActionEvent e) {
        System.out.println("¡ModificarParada!");
    }

    public void eliminarParada(ActionEvent e) {
        System.out.println("¡EliminarParada!");
    }
}
