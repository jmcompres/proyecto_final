package com.frontend.visual;

import com.backend.GestorRutas;
import com.backend.Parada;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

public class Controlador {


    @FXML
    Pane panelAgregar;
    @FXML
    TextField txtNombre;
    @FXML
    TextField txtLocalizacion;
    @FXML
    Pane panelModificar;
    @FXML
    ListView<Parada> listaParadas;
    @FXML
    Button btnModificar;
    @FXML
    TextField txtNombreM;
    @FXML
    TextField txtLocalizacionM;

    public void agregarParada(ActionEvent e){
        panelModificar.setVisible(false);
        panelAgregar.setVisible(true);
    }

    public void agregarP(ActionEvent e) {
        GestorRutas.getInstance().agregarParada(txtNombre.getText(), txtLocalizacion.getText());
        panelAgregar.setVisible(false);
    }

    public void modificarParada(ActionEvent e) {
        panelAgregar.setVisible(false);
        panelModificar.setVisible(true);

        listaParadas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Parada> paradas = FXCollections.observableArrayList(GestorRutas.getInstance().getParadas().values());
        listaParadas.setCellFactory(param -> new ListCell<Parada>() {
            @Override
            protected void updateItem(Parada parada, boolean empty) {
                super.updateItem(parada, empty);
                if (empty || parada == null) {
                    setText(null);
                } else {
                    setText(parada.getNombre());
                }
            }
        });
        listaParadas.setItems(paradas);

        listaParadas.setOnMouseClicked(event -> {
            Parada parada = listaParadas.getSelectionModel().getSelectedItem();
            txtNombreM.setText(parada.getNombre());
            txtLocalizacionM.setText(parada.getLocalizacion());
            btnModificar.setDisable(false);
        });

    }

    public void modificarP(ActionEvent e) {
        btnModificar.setDisable(true);
        Parada parada = listaParadas.getSelectionModel().getSelectedItem();
        parada.setNombre(txtNombreM.getText());
        parada.setLocalizacion(txtLocalizacionM.getText());
        panelModificar.setVisible(false);
    }

    public void eliminarParada(ActionEvent e) {
        System.out.println("¡EliminarParada!");
    }
}
