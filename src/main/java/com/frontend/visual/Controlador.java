package com.frontend.visual;

import java.util.*;

import com.backend.GestorRutas;
import com.backend.Parada;

import com.backend.Preferencias;
import com.backend.Ruta;
import com.backend.Localizacion;
import com.backend.ParParadaRuta;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.javafx.FxGraphRenderer;


public class Controlador {


    @FXML private Pane panelAgregar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtLocalizacion;
    @FXML private Pane panelModificar;
    @FXML private Button btnModificar;
    @FXML private TextField txtNombreM;
    @FXML private TextField txtLocalizacionM;
    @FXML private Pane panelAgregarRuta;
    @FXML private Spinner<Double> spnTiempo;
    @FXML private Spinner<Double> spnDistancia;
    @FXML private Spinner<Double> spnCosto;
    @FXML private Button btnAgregarRuta;
    @FXML private Button btnModificarRuta;
    @FXML private Pane panelModificarRuta;
    @FXML private Spinner<Double> spnModificarTiempo;
    @FXML private Spinner<Double> spnModificarDistancia;
    @FXML private Spinner<Double> spnModificarCosto;
    @FXML private Button btnAgregarP;
    @FXML private Button btnModificarP;
    @FXML private Button btnEliminarP;
    @FXML private Button btnAgregarR;
    @FXML private Button btnModificarR;
    @FXML private Button btnEliminarR;
    @FXML private VBox menuLateral;
    @FXML private Pane panelLateralModificarRuta;
    @FXML private Pane panelLateralEliminarRuta;
    @FXML private TableView<Ruta> tablaModificarRuta;
    @FXML private TableColumn<Ruta,String> columnaIdModificar;
    @FXML private TableColumn<Ruta,String> columnaOrigenModificar;
    @FXML private TableColumn<Ruta,String> columnaDestinoModificar;
    @FXML private TableView<Ruta> tablaEliminarRuta;
    @FXML private TableColumn<Ruta,String> columnaIdEliminar;
    @FXML private TableColumn<Ruta,String> columnaOrigenEliminar;
    @FXML private TableColumn<Ruta,String> columnaDestinoEliminar;
    @FXML private Button btnLateralModificar;
    @FXML private Button btnLateralEliminar;
    @FXML private Button btnBuscar;
    @FXML private ComboBox<String> cmbPref1;
    @FXML private ComboBox<String> cmbPref2;
    @FXML private ComboBox<String> cmbPref3;
    @FXML private ComboBox<String> cmbPref4;
    private ObservableList<String> opcionesPrefs;
    private final Set<String> prefsSeleccionadas = new HashSet<>();

    @FXML private CheckBox cbxExpMin;

    @FXML private Pane panelPrincipal;
    @FXML private Pane panelConfirmacion;

    private MultiGraph graph = new MultiGraph("Grafo");
    FxViewer viewer;
    FxViewPanel panel;
    private Node nodoSeleccionado1;
    private Node nodoSeleccionado2;
    private Parada paradaSeleccionada1 = null;
    private Parada paradaSeleccionada2 = null;
    private Accion accionActual = Accion.NINGUNA;
    private Ruta rutaSeleccionada = null;

    private Double posX;
    private Double posY;
    private Ruta rutaEncontrada = null;
    private boolean parada_Ruta = false;

    /**/
    private Double longitud;
    private Double latitud;
    private Double gPosX;
    private Double gPosY;
    /**/

    /**///atributos para mostrar las coordenadas del mouse
    @FXML
    private Label labelCoordenadas;
    private static boolean mouseTracking = false;
    /**/

    /**/
    private Map<Integer, Edge> aristasDelGrafo;
    private Map<Integer, Node> nodosDelGrafo;
    private List<ParParadaRuta> ultimaRutaCalculada;
    private Map<Integer, Ruta> mst;
    /**/

    /**/
    @FXML private Spinner<Double> spnModificarDescuento;
    /**/


    public void initialize() {


        aristasDelGrafo = new HashMap<Integer,Edge>();
        nodosDelGrafo = new HashMap<Integer, Node>();
        ultimaRutaCalculada = null;

        tablaModificarRuta.getStylesheets().add(getClass().getResource("/Personalizacion.css").toExternalForm());
        tablaEliminarRuta.getStylesheets().add(getClass().getResource("/Personalizacion.css").toExternalForm());

        columnaIdModificar.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaOrigenModificar.setCellValueFactory(cellData -> {
            Ruta ruta = cellData.getValue();
            if (ruta.getOrigen() != null) {
                return new SimpleStringProperty(ruta.getOrigen().getNombre());
            }
            return new SimpleStringProperty("N/A");
        });
        columnaDestinoModificar.setCellValueFactory(cellData -> {
            Ruta ruta = cellData.getValue();
            if (ruta.getDestino() != null) {
                return new SimpleStringProperty(ruta.getDestino().getNombre());
            }
            return new SimpleStringProperty("N/A");
        });

        columnaIdEliminar.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaOrigenEliminar.setCellValueFactory(cellData -> {
            Ruta ruta = cellData.getValue();
            if (ruta.getOrigen() != null) {
                return new SimpleStringProperty(ruta.getOrigen().getNombre());
            }
            return new SimpleStringProperty("N/A");
        });

        columnaDestinoEliminar.setCellValueFactory(cellData -> {
            Ruta ruta = cellData.getValue();
            if (ruta.getDestino() != null) {
                return new SimpleStringProperty(ruta.getDestino().getNombre());
            }
            return new SimpleStringProperty("N/A");
        });


        graph.setAttribute("ui.antialias", true);
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.stylesheet", "url('file:src/main/resources/Grafos.css')");
        graph.setAttribute("layout.force", false);

        viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();

        panel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());
        panel.getCamera().setAutoFitView(false);
        panel.getCamera().setGraphViewport(-5, 0, panel.widthProperty().doubleValue(), panel.heightProperty().doubleValue());
        panel.getCamera().setViewCenter(0,0,0);
        panel.getCamera().setViewPercent(1);

        panelPrincipal.getChildren().add(panel);
        panel.prefHeightProperty().bind(panelPrincipal.heightProperty());
        panel.prefWidthProperty().bind(panelPrincipal.widthProperty());


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

        //Iniciar los comboboxes
        String opcionDefault = "Ninguna";
        opcionesPrefs = FXCollections.observableArrayList("Costo", "Distancia", "Tiempo", "Transbordos", opcionDefault);

        List<ComboBox<String>> comboBoxes = Arrays.asList(cmbPref1, cmbPref2, cmbPref3, cmbPref4);
        for (ComboBox<String> comboBox : comboBoxes) {
            comboBox.setItems(FXCollections.observableArrayList(opcionesPrefs));
            comboBox.setValue(opcionDefault);
            configurarCellFactory(comboBox);
        }
        for (ComboBox<String> comboBox : comboBoxes) {
            agregarListener(comboBox, comboBoxes, opcionDefault);
        }
        agregarHabilitadores(cmbPref1, cmbPref2, opcionDefault);
        agregarHabilitadores(cmbPref2, cmbPref3, opcionDefault);
        agregarHabilitadores(cmbPref3, cmbPref4, opcionDefault);

        /**/
        panelPrincipal.setOnMouseMoved((MouseEvent event) -> {
            if (mouseTracking)
            {
                longitud = (event.getX()-(panelPrincipal.widthProperty().doubleValue()/2))/((panelPrincipal.widthProperty().doubleValue()/2))*180;
                latitud = (event.getY()-(panelPrincipal.heightProperty().doubleValue()/2))/((panelPrincipal.heightProperty().doubleValue()/2))*90;
                labelCoordenadas.setText(String.format("Latitud: %.2f°, Longitud: %.2f°", -latitud, longitud));
            }
        });
        /**/
    }


    /*MÉTODOS DE INICIALIZACIÓN*/
    private void configurarCellFactory(ComboBox<String> comboBox) {
        comboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {
                    setText(item);
                    boolean deshabilitado = prefsSeleccionadas.contains(item) && !item.equals(comboBox.getValue());
                    setDisable(deshabilitado);
                    setStyle(deshabilitado ? "-fx-text-fill: gray; -fx-opacity: 0.5;" : "");
                }
            }
        });
    }
    
    private void agregarListener(ComboBox<String> fuente, List<ComboBox<String>> todos, String opcionDefault) {
        fuente.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
            {
                GestorRutas.getInstance().desactivarExpMin();
                cbxExpMin.setSelected(false);
            }
            if (oldValue != null && !oldValue.equals(opcionDefault)) {
                prefsSeleccionadas.remove(oldValue);
            }
            if (newValue != null && !newValue.equals(opcionDefault)) {
                prefsSeleccionadas.add(newValue);
            }
            if (cmbPref1.getSelectionModel().getSelectedIndex() == 4)
            {
                btnBuscar.setDisable(true);
                cbxExpMin.setDisable(true);
            }
            else
            {
                btnBuscar.setDisable(false);
                cbxExpMin.setDisable(false);
            }
    
            // Actualizar solo las celdas sin modificar los ítems
            for (ComboBox<String> comboBox : todos) {
                comboBox.setCellFactory(lv -> new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setDisable(false);
                            setStyle("");
                        } else {
                            setText(item);
                            boolean deshabilitado = prefsSeleccionadas.contains(item) && !item.equals(comboBox.getValue());
                            setDisable(deshabilitado);
                            setStyle(deshabilitado ? "-fx-text-fill: gray; -fx-opacity: 0.5;" : "");
                        }
                    }
                });
            }
        });
    }
    
    private void agregarHabilitadores(ComboBox<String> fuente, ComboBox<String> siguiente, String opcionDefault) {
        fuente.valueProperty().addListener((obs, antiguoVal, neoVal) -> {
            if (neoVal.equals(opcionDefault)) {
                // Desactivar y restablecer el siguiente cmb si la opción por defecto está seleccionada
                siguiente.setDisable(true);
                siguiente.setValue(opcionDefault);
            } else {
                // Activar el siguiente cmb si la opción por defecto no está seleccionada
                siguiente.setDisable(false);
            }
        });
    }



    public void setAccionActual(Accion accion) {
        /**/
        if (nodoSeleccionado1 != null) deselectNodo(nodoSeleccionado1);
        if (nodoSeleccionado2 != null) deselectNodo(nodoSeleccionado2);
        if (ultimaRutaCalculada != null) desResaltarRuta();
        /**/
        this.accionActual = accion;
    }

    public void agregarParada(ActionEvent e) {
        /**/
        labelCoordenadas.setStyle("-fx-text-fill: black;");
        mouseTracking = true;
        /**/
        setAccionActual(Accion.AGREGAR_NODO);
        panel.setOnMouseClicked(this::handlePanelClick);
    }

    public void agregarP(ActionEvent e) {
        Localizacion neoLoca = new Localizacion(longitud, latitud, 0, txtLocalizacion.getText());
        int id = GestorRutas.getInstance().agregarParada(txtNombre.getText(), neoLoca);
        System.out.println(GestorRutas.getInstance().getParadas().get(id).getNombre());
        String nodeId = ""+id;
        Node newNode = graph.addNode(nodeId);
        newNode.setAttribute("ui.label", newNode.getId());
        nodosDelGrafo.put(id, newNode);
        newNode.setAttribute("x", gPosX);
        newNode.setAttribute("y", gPosY);
        panelAgregar.setVisible(false);
        panelAgregar.toBack();
        txtNombre.setText("");
        txtLocalizacion.setText("");
    }

    public void modificarParada(ActionEvent e) {
        setAccionActual(Accion.MODIFICAR_NODO);
        panel.setOnMouseClicked(this::handlePanelClick);
    }

    public void modificarP(ActionEvent e) {
        paradaSeleccionada1.setNombre(txtNombreM.getText());
        paradaSeleccionada1.getLocalizacion().setDescripcionDireccion(txtLocalizacionM.getText());
        panelModificar.setVisible(false);
        panelModificar.toBack();

    }

    public void eliminarParada(ActionEvent e) {
        parada_Ruta = false;
        setAccionActual(Accion.ELIMINAR_NODO);
        panel.setOnMouseClicked(this::handlePanelClick);
    }


    public void agregarRuta(ActionEvent e) {
        setAccionActual(Accion.AGREGAR_ARISTA);
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        spnTiempo.setValueFactory(valueFactory);
        spnDistancia.setValueFactory(valueFactory2);
        spnCosto.setValueFactory(valueFactory3);
        panel.setOnMouseClicked(this::handlePanelClick);
    }

    public void agregarR(ActionEvent e) {
        int id = GestorRutas.getInstance().agregarRuta(paradaSeleccionada1.getId(), paradaSeleccionada2.getId(), spnTiempo.getValue().floatValue(), spnDistancia.getValue().floatValue(), spnCosto.getValue().floatValue());
        agregarArista(id, nodoSeleccionado1, nodoSeleccionado2,spnTiempo.getValue().floatValue(), spnDistancia.getValue().floatValue(), spnCosto.getValue().floatValue());
        deselectNodo(nodoSeleccionado1);
        deselectNodo(nodoSeleccionado2);
        nodoSeleccionado1 = null;
        nodoSeleccionado2 = null;
        panelAgregarRuta.setVisible(false);
        panelAgregarRuta.toBack();
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        spnTiempo.setValueFactory(valueFactory);
        spnDistancia.setValueFactory(valueFactory2);
        spnCosto.setValueFactory(valueFactory3);
    }

    public void modificarRuta(ActionEvent e) {
        accionActual = Accion.NINGUNA;
        panelLateralModificarRuta.setVisible(true);
        panelLateralModificarRuta.toFront();
        menuLateral.setVisible(false);
        menuLateral.toBack();
        ObservableList<Ruta> rutasModificar = FXCollections.observableArrayList(GestorRutas.getInstance().getRutas().values());
        tablaModificarRuta.setItems(rutasModificar);
        tablaModificarRuta.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Item seleccionado: " + newValue);
                rutaSeleccionada = newValue;
                btnLateralModificar.setDisable(false);

                //Resaltar la ruta
                Edge neoArista = aristasDelGrafo.get(newValue.getId());
                neoArista.setAttribute("ui.class", "highlight");
                Edge oldArista = aristasDelGrafo.get(oldValue.getId());
                oldArista.removeAttribute("ui.class");
            } else {
                System.out.println("No hay item seleccionado.");
            }
        });
    }

    public void modificarR(ActionEvent e) {
        panelModificarRuta.setVisible(true);
        panelModificarRuta.toFront();
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, rutaSeleccionada.getTiempo(), 0.5f);
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, rutaSeleccionada.getDistancia(), 0.5f);
        SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, rutaSeleccionada.getCostoBruto(), 0.5f);
        SpinnerValueFactory<Double> valueFactory4 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 1000.0f, rutaSeleccionada.getDescuento()*100, 0.5f);
        valueFactory4.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (GestorRutas.getInstance().modificarDescuentoRuta(newValue.floatValue()/100, rutaSeleccionada.getId())) btnModificarRuta.setDisable(false);
            else btnModificarRuta.setDisable(true);
        });
        spnModificarTiempo.setValueFactory(valueFactory);
        spnModificarDistancia.setValueFactory(valueFactory2);
        spnModificarCosto.setValueFactory(valueFactory3);
        spnModificarDescuento.setValueFactory(valueFactory4);

        modificarArista(rutaSeleccionada.getId(), spnModificarTiempo.getValue().floatValue(), spnModificarDistancia.getValue().floatValue(), spnModificarCosto.getValue().floatValue(), spnModificarDescuento.getValue().floatValue());


    }

    public void ocultarM(ActionEvent e){
        modificarArista(rutaSeleccionada.getId(), spnModificarTiempo.getValue().floatValue(), spnModificarDistancia.getValue().floatValue(), spnModificarCosto.getValue().floatValue(),spnModificarDescuento.getValue().floatValue());
        tablaModificarRuta.getSelectionModel().clearSelection();
        modificarArista(rutaSeleccionada.getId(), spnModificarTiempo.getValue().floatValue(), spnModificarDistancia.getValue().floatValue(), spnModificarCosto.getValue().floatValue(), spnModificarDescuento.getValue().floatValue());
        btnLateralModificar.setDisable(true);
        panelLateralModificarRuta.setVisible(false);
        panelLateralModificarRuta.toBack();
        menuLateral.setVisible(true);
        menuLateral.toFront();
        panelModificarRuta.setVisible(false);
        panelModificarRuta.toBack();
        rutaSeleccionada = null;
    }

    public void eliminarRuta(ActionEvent e) {
        accionActual = Accion.NINGUNA;
        panelLateralEliminarRuta.setVisible(true);
        panelLateralEliminarRuta.toFront();
        menuLateral.setVisible(false);
        menuLateral.toBack();
        ObservableList<Ruta> rutasEliminar = FXCollections.observableArrayList(GestorRutas.getInstance().getRutas().values());
        tablaEliminarRuta.setItems(rutasEliminar);
        tablaEliminarRuta.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Item seleccionado: " + newValue);
                rutaSeleccionada = newValue;
                btnLateralEliminar.setDisable(false);

                Edge neoArista = aristasDelGrafo.get(newValue.getId());
                neoArista.setAttribute("ui.class", "highlight");
                Edge oldArista = aristasDelGrafo.get(oldValue.getId());
                oldArista.removeAttribute("ui.class");
            } else {
                System.out.println("No hay item seleccionado.");
            }
        });
        parada_Ruta = true;
    }

    public void eliminarR(ActionEvent e) {panelConfirmacion.setVisible(true);}


    private void handlePanelClick(MouseEvent event) {
        /**/
        mouseTracking = false;
        labelCoordenadas.setStyle("-fx-text-fill: white;");
        /**/
        /**/
        // Convertir las coordenadas del panel a las coordenadas del grafo
        double clickX = event.getX();
        double clickY = event.getY();
        Point3 graphCoordinates = panel.getCamera().transformPxToGu(clickX, clickY);
        double x = graphCoordinates.x;
        double y = graphCoordinates.y;
        setCoordinates(x, y);

        System.out.println("Clic en: (" + gPosX + ", " + gPosY + ")");
        /**/

        // Buscar el nodo más cercano al clic
        Node nodoCercano = null;
        for (Node nodo : graph) {
            double nodeX = (double) nodo.getAttribute("x");
            double nodeY = (double) nodo.getAttribute("y");

            // Calcular la distancia entre el clic y el nodo
            double distancia = Math.sqrt(Math.pow(nodeX - x, 2) + Math.pow(nodeY - y, 2));
            if (distancia < 0.5) { // Rango de selección
                nodoCercano = nodo;
                break;
            }
        }

        // Actuar según la acción seleccionada
        if (nodoCercano != null || accionActual == Accion.AGREGAR_NODO) {
            switch (accionActual) {
                case AGREGAR_NODO:
                    panelAgregar.setVisible(true);
                    panelAgregar.toFront();
                    break;

                case MODIFICAR_NODO:
                    nodoSeleccionado1 = nodoCercano;
                    seleccionarNodo(nodoSeleccionado1);
                    paradaSeleccionada1 = GestorRutas.getInstance().getParadas().get(Integer.parseInt(nodoSeleccionado1.getId()));
                    System.out.println(paradaSeleccionada1.getNombre());
                    txtNombreM.setText(paradaSeleccionada1.getNombre());
                    txtLocalizacionM.setText(paradaSeleccionada1.getLocalizacion().getDescripcionDireccion());
                    panelModificar.setVisible(true);
                    panelModificar.toFront();
                    deselectNodo(nodoSeleccionado1);
                    nodoSeleccionado1 = null;
                    break;

                case ELIMINAR_NODO:
                    nodoSeleccionado1 = nodoCercano;
                    seleccionarNodo(nodoSeleccionado1);
                    paradaSeleccionada1 = GestorRutas.getInstance().getParadas().get(Integer.parseInt(nodoSeleccionado1.getId()));
                    panelConfirmacion.setVisible(true);
                    panelConfirmacion.toFront();
                    break;

                case AGREGAR_ARISTA:
                    if (nodoSeleccionado1 == null) {
                        nodoSeleccionado1 = nodoCercano;
                        seleccionarNodo(nodoSeleccionado1);
                        paradaSeleccionada1 = GestorRutas.getInstance().getParadas().get(Integer.parseInt(nodoSeleccionado1.getId()));
                    } else if (nodoSeleccionado2 == null) {
                        if (nodoSeleccionado1 == nodoCercano) {
                            System.out.println("No se puede seleccionar el mismo nodo para la arista.");
                        } else {
                            nodoSeleccionado2 = nodoCercano;
                            seleccionarNodo(nodoSeleccionado2);
                            paradaSeleccionada2 = GestorRutas.getInstance().getParadas().get(Integer.parseInt(nodoSeleccionado2.getId()));
                            panelAgregarRuta.setVisible(true);
                            panelAgregarRuta.toFront();
                        }
                    }
                    break;

                case BUSCAR_RUTA:
                    if (nodoSeleccionado1 == null) {
                        nodoSeleccionado1 = nodoCercano;
                        seleccionarNodo(nodoSeleccionado1);
                        paradaSeleccionada1 = GestorRutas.getInstance().getParadas().get(Integer.parseInt(nodoSeleccionado1.getId()));
                    } else if (nodoSeleccionado2 == null) {
                        if (nodoSeleccionado1 == nodoCercano) {
                            System.out.println("No se puede buscar una ruta hacia la misma parada");
                        } else {
                            nodoSeleccionado2 = nodoCercano;
                            seleccionarNodo(nodoSeleccionado2);
                            paradaSeleccionada2 = GestorRutas.getInstance().getParadas().get(Integer.parseInt(nodoSeleccionado2.getId()));
                            resaltarRuta(rutaOptima());
                        }
                    }
                    break;
            }
        } else {
            System.out.println("No se seleccionó ningún nodo. Haz clic cerca de un nodo.");
        }
    }



    private void seleccionarNodo(Node nodo) {
        // Resaltar el nodo seleccionado
        nodo.setAttribute("ui.class", "selected");
    }

    private void deselectNodo(Node nodo) {
        // Restablecer el color o quitar el estilo de selección
        nodo.removeAttribute("ui.class");
    }

    private void agregarArista(int id, Node nodo1, Node nodo2, float tiempo, float distancia, float costo) {
        if (nodo1 != null && nodo2 != null) {
            String aristaId = String.valueOf(id);
            graph.addEdge(aristaId, nodo1.getId(), nodo2.getId(), true); // true para crear una arista dirigida
            Edge arista = graph.getEdge(aristaId);
            arista.setAttribute("ui.label", "T: " + tiempo + " D: " + distancia + " C: " + costo);
            aristasDelGrafo.put(id, arista);
            arista.setAttribute("Tiempo", tiempo);
            arista.setAttribute("Distancia", distancia);
            arista.setAttribute("Costo", costo);
        }
    }

    private void eliminarArista(int id) {
        String edgeId = String.valueOf(id);
        Edge arista = graph.getEdge(edgeId);
        if (arista != null) {
            graph.removeEdge(arista);
            GestorRutas.getInstance().eliminarRuta(rutaSeleccionada.getId());
        } else {
            System.out.println("No existe la arista");
        }
    }

    private void modificarArista(int id, float tiempo, float distancia, float costo, float descuento) {
        String edgeId = String.valueOf(id);
        Edge arista = graph.getEdge(edgeId);
        if (arista != null) {
            arista.setAttribute("Tiempo", tiempo);
            arista.setAttribute("Distancia", distancia);
            arista.setAttribute("Costo", costo);
            arista.setAttribute("Descuento", descuento);
            arista.setAttribute("ui.label", "T: " + tiempo + " D: " + distancia + " C: " + costo);

            rutaSeleccionada.setTiempo(tiempo);
            rutaSeleccionada.setDistancia(distancia);
            rutaSeleccionada.setCostoBruto(costo);
            rutaSeleccionada.setDescuento(descuento/100);
            //deseleccionar arista
            aristasDelGrafo.get(rutaSeleccionada.getId()).removeAttribute("ui.class");
        } else {
            System.out.println("No existe una arista entre los nodos seleccionados.");
        }
    }

    public void setCoordinates(double x, double y) {
        this.gPosX = x;
        this.gPosY = y;
    }


    public void confirmar(ActionEvent e) {
        if(parada_Ruta){
            eliminarArista(rutaSeleccionada.getId());
            panelLateralEliminarRuta.setVisible(false);
            panelLateralEliminarRuta.toBack();
            menuLateral.setVisible(true);
            menuLateral.toFront();
            btnLateralEliminar.setDisable(true);
            tablaEliminarRuta.getSelectionModel().clearSelection();
            rutaSeleccionada = null;
            parada_Ruta = false;
        }else{
            GestorRutas.getInstance().eliminarParada(paradaSeleccionada1.getId());
            graph.removeNode(nodoSeleccionado1.getId());
            nodoSeleccionado1 = null;
        }
        panelConfirmacion.setVisible(false);
    }

    public void cancelar(ActionEvent e) {
        if(parada_Ruta) {
            panelLateralEliminarRuta.setVisible(false);
            panelLateralEliminarRuta.toBack();
            menuLateral.setVisible(true);
            menuLateral.toFront();
            rutaSeleccionada = null;
            parada_Ruta = false;
        }else{
            deselectNodo(nodoSeleccionado1);
            nodoSeleccionado1 = null;
        }
        panelConfirmacion.setVisible(false);
    }

    public void alternarExpMin()
    {
        GestorRutas.getInstance().setExpMin(!GestorRutas.getInstance().getExpMinActivado());
        if (GestorRutas.getInstance().getExpMinActivado()) modoExpMin();
        else desModoExpMin();
    }

    private void modoExpMin()
    {
        mst = GestorRutas.getInstance().getMst(getPrefs());
        for (Map.Entry<Integer, Edge> entry : aristasDelGrafo.entrySet())
        {
            if (!mst.containsKey(entry.getKey())) entry.getValue().setAttribute("ui.class", "invisible");
            else entry.getValue().setAttribute("ui.class", "mstEdge");
        }
        if (ultimaRutaCalculada!=null) desResaltarRuta();
    }

    private void desModoExpMin()
    {
        for (Edge e: aristasDelGrafo.values())
            e.removeAttribute("ui.class");
    }

    public void encontrarRuta()
    {
        setAccionActual(Accion.BUSCAR_RUTA);
        panel.setOnMouseClicked(this::handlePanelClick);
    }

    private Preferencias[] getPrefs()
    {
        Preferencias prefs[] = new Preferencias[5];
        prefs[0] = Preferencias.getPorValor(cmbPref1.getSelectionModel().getSelectedIndex());
        prefs[0] = Preferencias.getPorValor(cmbPref2.getSelectionModel().getSelectedIndex());
        prefs[0] = Preferencias.getPorValor(cmbPref3.getSelectionModel().getSelectedIndex());
        prefs[0] = Preferencias.getPorValor(cmbPref4.getSelectionModel().getSelectedIndex());
        prefs[0] = Preferencias.NINGUNA;

        return prefs;
    }

    public List<ParParadaRuta> rutaOptima()
    {
        Preferencias prefs[] = getPrefs();
        //Los ids de las paradas hay que cambiarlos según los nodos a los que se les haga click
        ultimaRutaCalculada = GestorRutas.getInstance().encontrarRuta(paradaSeleccionada1.getId(), paradaSeleccionada2.getId(), prefs);
        return ultimaRutaCalculada;
    }

    private void resaltarRuta(List<ParParadaRuta> ruta)
    {
        for (ParParadaRuta pr : ruta)
        {
            Node nodo = nodosDelGrafo.get(pr.parada().getId());
            Edge arista = null;
            if (pr.ruta() != null) arista = aristasDelGrafo.get(pr.ruta().getId());
            nodo.removeAttribute("ui.class");
            nodo.setAttribute("ui.class", "highlight");
            if (arista != null) arista.setAttribute("ui.class", "highlight");
        }
    }

    private void desResaltarRuta()
    {
        for (ParParadaRuta pr : ultimaRutaCalculada)
        {
            Node nodo = nodosDelGrafo.get(pr.parada().getId());
            Edge arista = null;
            if (pr.ruta() != null) arista = aristasDelGrafo.get(pr.ruta().getId());
            nodo.removeAttribute("ui.class");
            if (arista != null) arista.removeAttribute("ui.class");
        }
    }

}

