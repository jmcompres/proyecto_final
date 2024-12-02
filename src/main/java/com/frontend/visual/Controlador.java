package com.frontend.visual;

import java.util.Map;

import com.backend.GestorRutas;
import com.backend.Parada;
import com.backend.Ruta;
import com.backend.Localizacion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.javafx.FxGraphRenderer;


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
    @FXML private Spinner<Double> spnLongitud;
    @FXML private Spinner<Double> spnLatitud;
    @FXML private Spinner<Double> spnLongitudM;
    @FXML private Spinner<Double> spnLatitudM;
    @FXML private Button btnAgregarP;
    @FXML private Button btnModificarP;
    @FXML private Button btnEliminarP;
    @FXML private Button btnAgregarR;
    @FXML private Button btnModificarR;
    @FXML private Button btnEliminarR;
    @FXML private Button btnBuscar;
    @FXML private Pane panelPrincipal;
    private static double latMax = 90.0d, lonMax = 180.0d;
    private MultiGraph graph = new MultiGraph("Grafo");

    public void initialize() {

        graph.setAttribute("ui.antialias", true);
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.stylesheet", "url('file:src/main/resources/Grafos.css')");

        FxViewer viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.disableAutoLayout();

        FxViewPanel panel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());

        panelPrincipal.getChildren().add(panel);
        panel.prefHeightProperty().bind(panelPrincipal.heightProperty());
        panel.prefWidthProperty().bind(panelPrincipal.widthProperty());

        panel.setOnMouseClicked(this::handleMouseClick);


        Image image = new Image(getClass().getResourceAsStream("/images/add.png"));
        Image image2 = new Image(getClass().getResourceAsStream("/images/edit.png"));
        Image image3 = new Image(getClass().getResourceAsStream("/images/delete.png"));
        Image image4 = new Image(getClass().getResourceAsStream("/images/search.png"));

        ImageView imagenAgregar = new ImageView();
        imagenAgregar.setFitWidth(30);
        imagenAgregar.setFitHeight(30);
        imagenAgregar.setImage(image);
        btnAgregarP.setGraphic(imagenAgregar);
        btnAgregarP.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);

        ImageView imagenModificar = new ImageView();
        imagenModificar.setFitWidth(30);
        imagenModificar.setFitHeight(30);
        imagenModificar.setImage(image2);
        btnModificarP.setGraphic(imagenModificar);
        btnModificarP.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);

        ImageView imagenEliminar = new ImageView();
        imagenEliminar.setFitWidth(30);
        imagenEliminar.setFitHeight(30);
        imagenEliminar.setImage(image3);
        btnEliminarP.setGraphic(imagenEliminar);
        btnEliminarP.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);

        ImageView imagenAgregar2 = new ImageView();
        imagenAgregar2.setFitWidth(30);
        imagenAgregar2.setFitHeight(30);
        imagenAgregar2.setImage(image);
        btnAgregarR.setGraphic(imagenAgregar2);
        btnAgregarR.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);

        ImageView imagenModificar2 = new ImageView();
        imagenModificar2.setFitWidth(30);
        imagenModificar2.setFitHeight(30);
        imagenModificar2.setImage(image2);
        btnModificarR.setGraphic(imagenModificar2);
        btnModificarR.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);

        ImageView imagenEliminar2 = new ImageView();
        imagenEliminar2.setFitWidth(30);
        imagenEliminar2.setFitHeight(30);
        imagenEliminar2.setImage(image3);
        btnEliminarR.setGraphic(imagenEliminar2);
        btnEliminarR.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);

        ImageView imagenBuscar = new ImageView();
        imagenBuscar.setFitWidth(30);
        imagenBuscar.setFitHeight(30);
        imagenBuscar.setImage(image4);
        btnBuscar.setGraphic(imagenBuscar);
        btnBuscar.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);

        SpinnerValueFactory<Double> longitudValueFactory = new DoubleSpinnerValueFactory(-180.0, 180.0, 0.0, 1);
        spnLongitud.setValueFactory(longitudValueFactory);
        spnLongitudM.setValueFactory(longitudValueFactory);

        SpinnerValueFactory<Double> latitudValueFactory = new DoubleSpinnerValueFactory(-90.0, 90.0, 0.0, 1);
        spnLatitud.setValueFactory(latitudValueFactory);
        spnLatitudM.setValueFactory(latitudValueFactory);


    }

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
        Localizacion neoLoca = new Localizacion(spnLongitud.getValue(), spnLatitud.getValue(), 0, txtLocalizacion.getText());
        GestorRutas.getInstance().agregarParada(txtNombre.getText(), neoLoca);
        panelAgregar.setVisible(false);
        spnLongitud.getValueFactory().setValue(0.0d);
        spnLatitud.getValueFactory().setValue(0.0d);

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
            txtLocalizacionM.setText(parada.getLocalizacion().getDescripcionDireccion());
            spnLongitudM.getValueFactory().setValue(parada.getLocalizacion().getLongitud());
            spnLatitudM.getValueFactory().setValue(parada.getLocalizacion().getLatitud());
            btnModificar.setDisable(false);
        });

    }

    public void modificarP(ActionEvent e) {
        btnModificar.setDisable(true);
        Parada parada = listaParadas.getSelectionModel().getSelectedItem();
        parada.setNombre(txtNombreM.getText());
        parada.getLocalizacion().setDescripcionDireccion(txtLocalizacionM.getText());
        parada.getLocalizacion().setLongitud(spnLongitudM.getValue());
        parada.getLocalizacion().setLatitud(spnLatitudM.getValue());
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
            SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, ruta.getCostoBruto(), 0.5f);
            spnModificarTiempo.setValueFactory(valueFactory);
            spnModificarDistancia.setValueFactory(valueFactory2);
            spnModificarCosto.setValueFactory(valueFactory3);
        });

    }

    public void modificarR(ActionEvent e) {
        Ruta ruta = listaRutas.getSelectionModel().getSelectedItem();
        ruta.setTiempo(spnModificarTiempo.getValue().floatValue());
        ruta.setDistancia(spnModificarDistancia.getValue().floatValue());
        ruta.setCostoBruto(spnModificarCosto.getValue().floatValue());
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

    private void handleMouseClick(MouseEvent event) {
        double x = event.getX(); // Coordenada X en el panel
        double y = event.getY(); // Coordenada Y en el panel

        // Agregar nodo al grafo
        String nodeId = "Node" + graph.getNodeCount();
        Node node = graph.addNode(nodeId);
        node.setAttribute("xy", x, y); // Establecer posición 2D

        System.out.println("Nodo agregado: " + nodeId + " en posición (" + x + ", " + y + ")");
    }
}
