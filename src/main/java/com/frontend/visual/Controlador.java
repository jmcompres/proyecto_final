package com.frontend.visual;

import com.backend.GestorRutas;
import com.backend.Parada;
import com.backend.Ruta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.ListView;


public class Controlador {


    @FXML private Pane panelAgregar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtLocalizacion;
    @FXML private Pane panelModificar;
    @FXML private ListView<Parada> listaParadas;
    @FXML private Button btnModificar;
    @FXML private TextField txtNombreM;
    @FXML private TextField txtLocalizacionM;
    @FXML private Pane panelEliminar;
    @FXML private ListView<Parada> listaParadasEliminar;
    @FXML private Button btnEliminar;
    @FXML private Pane panelAgregarRuta;
    @FXML private ListView<Parada> listaOrigen;
    @FXML private ListView<Parada> listaDestino;
    @FXML private Spinner<Double> spnTiempo;
    @FXML private Spinner<Double> spnDistancia;
    @FXML private Spinner<Double> spnCosto;
    @FXML private Button btnAgregarRuta;
    @FXML private Button btnModificarRuta;
    @FXML private Pane panelModificarRuta;
    @FXML private ListView<Ruta> listaRutas;
    @FXML private Spinner<Double> spnModificarTiempo;
    @FXML private Spinner<Double> spnModificarDistancia;
    @FXML private Spinner<Double> spnModificarCosto;
    @FXML private Pane panelEliminarRuta;
    @FXML private ListView<Ruta> listaEliminarRuta;
    @FXML private Button btnEliminarRuta;

    public void agregarParada(ActionEvent e){
        panelModificar.setVisible(false);
        panelEliminar.setVisible(false);
        panelAgregarRuta.setVisible(false);
        panelModificarRuta.setVisible(false);
        panelEliminarRuta.setVisible(false);
        panelAgregar.setVisible(true);

        txtNombre.setText("");
        txtLocalizacion.setText("");
    }

    public void agregarP(ActionEvent e) {
        GestorRutas.getInstance().agregarParada(txtNombre.getText(), txtLocalizacion.getText());
        panelAgregar.setVisible(false);
    }

    public void modificarParada(ActionEvent e) {
        panelAgregar.setVisible(false);
        panelEliminar.setVisible(false);
        panelAgregarRuta.setVisible(false);
        panelModificarRuta.setVisible(false);
        panelEliminarRuta.setVisible(false);
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
        listaParadas.getSelectionModel().clearSelection();
        panelModificar.setVisible(false);
    }

    public void eliminarParada(ActionEvent e) {
        panelModificar.setVisible(false);
        panelAgregar.setVisible(false);
        panelAgregarRuta.setVisible(false);
        panelModificarRuta.setVisible(false);
        panelEliminarRuta.setVisible(false);
        panelEliminar.setVisible(true);

        listaParadasEliminar.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Parada> paradasE = FXCollections.observableArrayList(GestorRutas.getInstance().getParadas().values());
        listaParadasEliminar.setCellFactory(param -> new ListCell<Parada>() {
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
        listaParadasEliminar.setItems(paradasE);

        listaParadasEliminar.setOnMouseClicked(event -> {
            btnEliminar.setDisable(false);
        });

    }

    public void eliminarP(ActionEvent e) {
        btnEliminar.setDisable(true);
        Parada parada = listaParadasEliminar.getSelectionModel().getSelectedItem();
        GestorRutas.getInstance().eliminarParada(parada.getId());
        listaParadasEliminar.getItems().remove(parada);
        panelEliminar.setVisible(false);
    }

    public void agregarRuta(ActionEvent e) {
        panelModificar.setVisible(false);
        panelAgregar.setVisible(false);
        panelEliminar.setVisible(false);
        panelModificarRuta.setVisible(false);
        panelEliminarRuta.setVisible(false);
        panelAgregarRuta.setVisible(true);

        listaOrigen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ObservableList<Parada> paradas = FXCollections.observableArrayList(GestorRutas.getInstance().getParadas().values());
        listaOrigen.setCellFactory(param -> new ListCell<>() {
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
        listaOrigen.setItems(paradas);

        listaOrigen.setOnMouseClicked(event -> {
            Parada paradaOrigen = listaOrigen.getSelectionModel().getSelectedItem();
            ObservableList<Parada> paradas2 = FXCollections.observableArrayList(GestorRutas.getInstance().getParadas().values());
            listaDestino.setItems(paradas2);
            listaDestino.getItems().remove(paradaOrigen);
            listaDestino.setDisable(false);
            listaOrigen.setDisable(true);
        });

        listaDestino.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listaDestino.setCellFactory(param -> new ListCell<>() {
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

        listaDestino.setOnMouseClicked(event -> {
            spnTiempo.setDisable(false);
            spnDistancia.setDisable(false);
            spnCosto.setDisable(false);
            btnAgregarRuta.setDisable(false);
        });

        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        spnTiempo.setValueFactory(valueFactory);
        spnDistancia.setValueFactory(valueFactory2);
        spnCosto.setValueFactory(valueFactory3);


    }

    public void agregarR(ActionEvent e) {
        Parada origen = listaOrigen.getSelectionModel().getSelectedItem();
        Parada destino = listaDestino.getSelectionModel().getSelectedItem();
        float tiempo = spnTiempo.getValue().floatValue();
        float distancia = spnDistancia.getValue().floatValue();
        float costo = spnCosto.getValue().floatValue();
        GestorRutas.getInstance().agregarRuta(origen.getId(), destino.getId(), tiempo, distancia, costo);
        listaOrigen.getSelectionModel().clearSelection();
        listaOrigen.setDisable(false);
        listaDestino.getSelectionModel().clearSelection();
        listaDestino.setDisable(true);
        spnTiempo.setDisable(true);
        spnDistancia.setDisable(true);
        spnCosto.setDisable(true);
        btnAgregarRuta.setDisable(true);
        panelAgregarRuta.setVisible(false);
    }

    public void modificarRuta(ActionEvent e) {
        panelModificar.setVisible(false);
        panelAgregar.setVisible(false);
        panelEliminar.setVisible(false);
        panelAgregarRuta.setVisible(false);
        panelEliminarRuta.setVisible(false);
        panelModificarRuta.setVisible(true);

        listaRutas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Ruta> rutas = FXCollections.observableArrayList(GestorRutas.getInstance().getRutas().values());

        listaRutas.setCellFactory(param -> new ListCell<Ruta>() {
            @Override
            protected void updateItem(Ruta ruta, boolean empty) {
                super.updateItem(ruta, empty);
                if (empty || ruta == null) {
                    setText(null);
                } else {
                    String origenNombre = ruta.getOrigen() != null ? ruta.getOrigen().getNombre() : "Desconocido";
                    String destinoNombre = ruta.getDestino() != null ? ruta.getDestino().getNombre() : "Desconocido";
                    setText(origenNombre + " - " + destinoNombre);
                }
            }
        });
        listaRutas.setItems(rutas);

        listaRutas.setOnMouseClicked(event -> {
            Ruta ruta = listaRutas.getSelectionModel().getSelectedItem();
            spnModificarTiempo.setDisable(false);
            spnModificarDistancia.setDisable(false);
            spnModificarCosto.setDisable(false);
            btnModificarRuta.setDisable(false);
            SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, ruta.getTiempo(), 0.5f);
            SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, ruta.getDistancia(), 0.5f);
            SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, ruta.getCosto(), 0.5f);
            spnModificarTiempo.setValueFactory(valueFactory);
            spnModificarDistancia.setValueFactory(valueFactory2);
            spnModificarCosto.setValueFactory(valueFactory3);
        });

    }

    public void modificarR(ActionEvent e) {
        Ruta ruta = listaRutas.getSelectionModel().getSelectedItem();
        ruta.setTiempo(spnModificarTiempo.getValue().floatValue());
        ruta.setDistancia(spnModificarDistancia.getValue().floatValue());
        ruta.setCosto(spnModificarCosto.getValue().floatValue());
        listaRutas.getSelectionModel().clearSelection();
        spnModificarTiempo.setDisable(true);
        spnModificarDistancia.setDisable(true);
        spnModificarCosto.setDisable(true);
        btnModificarRuta.setDisable(true);
        panelModificarRuta.setVisible(false);
    }

    public void eliminarRuta(ActionEvent e) {
        panelModificar.setVisible(false);
        panelAgregar.setVisible(false);
        panelEliminar.setVisible(false);
        panelAgregarRuta.setVisible(false);
        panelModificarRuta.setVisible(false);
        panelEliminarRuta.setVisible(true);

        listaEliminarRuta.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Ruta> rutas2 = FXCollections.observableArrayList(GestorRutas.getInstance().getRutas().values());
        listaEliminarRuta.setCellFactory(param -> new ListCell<Ruta>() {
            @Override
            protected void updateItem(Ruta ruta, boolean empty) {
                super.updateItem(ruta, empty);
                if (empty || ruta == null) {
                    setText(null);
                } else {
                    String origenNombre = ruta.getOrigen() != null ? ruta.getOrigen().getNombre() : "Desconocido";
                    String destinoNombre = ruta.getDestino() != null ? ruta.getDestino().getNombre() : "Desconocido";
                    setText(origenNombre + " - " + destinoNombre);
                }
            }
        });
        listaEliminarRuta.setItems(rutas2);

        listaEliminarRuta.setOnMouseClicked(event -> {
            btnEliminarRuta.setDisable(false);
        });
    }

    public void eliminarR(ActionEvent e) {
        btnEliminarRuta.setDisable(true);
        Ruta ruta = listaEliminarRuta.getSelectionModel().getSelectedItem();
        GestorRutas.getInstance().eliminarRuta(ruta.getId());
        listaEliminarRuta.getItems().remove(ruta);
        panelEliminarRuta.setVisible(false);
    }


}
