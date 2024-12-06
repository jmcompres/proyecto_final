package com.frontend.visual;

import java.util.*;

import com.backend.GestorArchivos;
import com.backend.GestorRutas;
import com.backend.Parada;

import com.backend.Preferencias;
import com.backend.Ruta;
import com.backend.Localizacion;
import com.backend.ParParadaRuta;

import javafx.application.Platform;
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


    // Componentes de la interfaz gráfica para agregar paradas
    @FXML private Pane panelAgregar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtLocalizacion;

    // Componentes de la interfaz gráfica para modificar paradas
    @FXML private Pane panelModificar;
    @FXML private Button btnModificar;
    @FXML private TextField txtNombreM;
    @FXML private TextField txtLocalizacionM;

    // Componentes de la interfaz gráfica para agregar rutas
    @FXML private Pane panelAgregarRuta;
    @FXML private Spinner<Double> spnTiempo;
    @FXML private Spinner<Double> spnDistancia;
    @FXML private Spinner<Double> spnCosto;
    @FXML private Button btnAgregarRuta;

    // Componentes de la interfaz gráfica para modificar rutas
    @FXML private Button btnModificarRuta;
    @FXML private Pane panelModificarRuta;
    @FXML private Spinner<Double> spnModificarTiempo;
    @FXML private Spinner<Double> spnModificarDistancia;
    @FXML private Spinner<Double> spnModificarCosto;

    // Botones para gestionar paradas y rutas
    @FXML private Button btnAgregarP;
    @FXML private Button btnModificarP;
    @FXML private Button btnEliminarP;
    @FXML private Button btnAgregarR;
    @FXML private Button btnModificarR;
    @FXML private Button btnEliminarR;

    // Componentes de gestión de rutas y menú lateral
    @FXML private VBox menuLateral;
    @FXML private Pane panelLateralModificarRuta;
    @FXML private Pane panelLateralEliminarRuta;
    @FXML private Button btnLateralModificar;
    @FXML private Button btnLateralEliminar;
    @FXML private Button btnBuscar;

    // Tablas para modificar y eliminar rutas
    @FXML private TableView<Ruta> tablaModificarRuta;
    @FXML private TableColumn<Ruta,String> columnaIdModificar;
    @FXML private TableColumn<Ruta,String> columnaOrigenModificar;
    @FXML private TableColumn<Ruta,String> columnaDestinoModificar;

    @FXML private TableView<Ruta> tablaEliminarRuta;
    @FXML private TableColumn<Ruta,String> columnaIdEliminar;
    @FXML private TableColumn<Ruta,String> columnaOrigenEliminar;
    @FXML private TableColumn<Ruta,String> columnaDestinoEliminar;

    // Componentes para selección de preferencias
    @FXML private ComboBox<String> cmbPref1;
    @FXML private ComboBox<String> cmbPref2;
    @FXML private ComboBox<String> cmbPref3;
    @FXML private ComboBox<String> cmbPref4;
    private ObservableList<String> opcionesPrefs;
    private final Set<String> prefsSeleccionadas = new HashSet<>();

    @FXML private CheckBox cbxExpMin;

    // Panel principal
    @FXML private Pane panelPrincipal;
    //Panel para confirmar eliminacion
    @FXML private Pane panelConfirmacion;

    // Componentes para manejo de grafos y nodos
    private MultiGraph graph = new MultiGraph("Grafo");
    FxViewer viewer;
    FxViewPanel panel;
    private Node nodoSeleccionado1;
    private Node nodoSeleccionado2;
    private Parada paradaSeleccionada1 = null;
    private Parada paradaSeleccionada2 = null;
    private Accion accionActual = Accion.NINGUNA;
    private Accion accionPrevia = null;
    private Ruta rutaSeleccionada = null;

    // Variables para posicionamiento y coordenadas
    private Double posX;
    private Double posY;
    private Ruta rutaEncontrada = null;
    private boolean parada_Ruta = false;

    // Coordenadas geográficas
    private Double longitud;
    private Double latitud;
    private Double gPosX;
    private Double gPosY;

    // Componente para mostrar información de acción
    @FXML
    private Label labelInfoAccion;
    private static boolean mouseTracking = false;

    // Estructuras de datos para manejo de grafos
    private Map<Integer, Edge> aristasDelGrafo;
    private Map<Integer, Node> nodosDelGrafo;
    private List<ParParadaRuta> ultimaRutaCalculada;
    private Map<Integer, Ruta> mst;

    // Spinner adicional para modificar descuentos
    @FXML private Spinner<Double> spnModificarDescuento;
    /**/


    public void initialize() {
        // Inicialización de mapas y listas
        aristasDelGrafo = new HashMap<Integer,Edge>();
        nodosDelGrafo = new HashMap<Integer, Node>();
        ultimaRutaCalculada = null;

        // Configuración de estilos para tablas
        tablaModificarRuta.getStylesheets().add(getClass().getResource("/Personalizacion.css").toExternalForm());
        tablaEliminarRuta.getStylesheets().add(getClass().getResource("/Personalizacion.css").toExternalForm());

        // Configuración de columnas para las tablas de rutas
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

        // Configuración del grafo
        graph.setAttribute("ui.antialias", true);
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.stylesheet", "url('file:src/main/resources/Grafos.css')");
        graph.setAttribute("layout.force", false);

        // Configuración del visor de grafos
        viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();

        // Configuración adicional del panel y la cámara
        panel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());
        panel.getCamera().setAutoFitView(false);
        panel.getCamera().setGraphViewport(-5, 0, panel.widthProperty().doubleValue(), panel.heightProperty().doubleValue());
        panel.getCamera().setViewCenter(0,0,0);
        panel.getCamera().setViewPercent(1);

        panelPrincipal.getChildren().add(panel);
        panel.prefHeightProperty().bind(panelPrincipal.heightProperty());
        panel.prefWidthProperty().bind(panelPrincipal.widthProperty());

        // Configuración de imágenes para botones
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

        // Configuración del seguimiento del mouse
        /**/
        panelPrincipal.setOnMouseMoved((MouseEvent event) -> {
            if (mouseTracking)
            {
                longitud = (event.getX()-(panelPrincipal.widthProperty().doubleValue()/2))/((panelPrincipal.widthProperty().doubleValue()/2))*180;
                latitud = (event.getY()-(panelPrincipal.heightProperty().doubleValue()/2))/((panelPrincipal.heightProperty().doubleValue()/2))*90;
                labelInfoAccion.setText(String.format("ACCIÓN: AGREGAR PARADA    Latitud: %.2f°, Longitud: %.2f°", -latitud, longitud));
            }
        });
        /**/

        Platform.runLater(() -> {
            cargarDataDeFichero();
        });
    }


    /*MÉTODOS DE INICIALIZACIÓN*/
    //Configura la fábrica de celdas para un ComboBox, personalizando su apariencia y comportamiento.
    private void configurarCellFactory(ComboBox<String> comboBox) {
        comboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    // Restablecer la celda cuando esté vacía
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {
                    setText(item);
                    // Deshabilitar opciones ya seleccionadas en otros ComboBox
                    boolean deshabilitado = prefsSeleccionadas.contains(item) && !item.equals(comboBox.getValue());
                    setDisable(deshabilitado);
                    setStyle(deshabilitado ? "-fx-text-fill: gray; -fx-opacity: 0.5;" : "");
                }
            }
        });
    }

    //Agrega un listener a un ComboBox para gestionar la selección de preferencias.
    private void agregarListener(ComboBox<String> fuente, List<ComboBox<String>> todos, String opcionDefault) {
        fuente.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Desactivar la opción de expansión mínima cuando cambia la selección
            if (!oldValue.equals(newValue))
            {
                noExpMin();
            }

            // Actualizar el conjunto de preferencias seleccionadas
            if (oldValue != null && !oldValue.equals(opcionDefault)) {
                prefsSeleccionadas.remove(oldValue);
            }
            if (newValue != null && !newValue.equals(opcionDefault)) {
                prefsSeleccionadas.add(newValue);
            }

            // Gestionar la habilitación del botón de búsqueda y la casilla de expansión mínima
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
    
    //Agrega habilitadores entre ComboBox consecutivos para gestionar su estado.
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


    //Establece la acción actual del controlador, gestionando la selección de nodos y rutas.
    public void setAccionActual(Accion accion) {
        // Deseleccionar nodos y rutas si es necesario
        if (nodoSeleccionado1 != null && accion!=Accion.MOSTRAR_RUTA) deselectNodo(nodoSeleccionado1);
        if (nodoSeleccionado2 != null && accion!=Accion.MOSTRAR_RUTA) deselectNodo(nodoSeleccionado2);
        if (ultimaRutaCalculada != null && accion!=Accion.MOSTRAR_RUTA)
        {
            desResaltarRuta();
        }

        // Guardar datos si la acción es NINGUNA
        if (accion == Accion.NINGUNA) GestorArchivos.guardarData();
        // Actualizar la acción actual y previa
        accionPrevia = accionActual;
        this.accionActual = accion;

        // Reiniciar nodos seleccionados si la acción cambió
        if (accionActual != accionPrevia)
        {
            nodoSeleccionado1 = null;
            nodoSeleccionado2 = null;
        }
    }

    //Inicia el proceso de agregar una parada, preparando el seguimiento del mouse.
    public void agregarParada(ActionEvent e) {
        // Preparar la etiqueta de información de acción
        labelInfoAccion.setText("ACCIÓN: AGREGAR PARADA    Latitud: 0°, Longitud: 0°");
        mouseTracking = true;

        // Establecer la acción actual y configurar el manejador de clics
        setAccionActual(Accion.AGREGAR_NODO);
        panel.setOnMouseClicked(this::handlePanelClick);
    }

    //Agrega una nueva parada al sistema de rutas.
    public void agregarP(ActionEvent e) {
        Localizacion neoLoca = new Localizacion(longitud, latitud, 0, txtLocalizacion.getText());
        int id = GestorRutas.getInstance().agregarParada(txtNombre.getText(), neoLoca, gPosX, gPosY);
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

        noExpMin();
        quitarAccion();
    }

    //Prepara la interfaz para modificar una parada existente.
    public void modificarParada(ActionEvent e) {
        labelInfoAccion.setText("ACCIÓN: MODIFICAR PARADA");
        setAccionActual(Accion.MODIFICAR_NODO);
        panel.setOnMouseClicked(this::handlePanelClick);
    }

    //Confirma la modificación de una parada seleccionada.
    public void modificarP(ActionEvent e) {
        paradaSeleccionada1.setNombre(txtNombreM.getText());
        paradaSeleccionada1.getLocalizacion().setDescripcionDireccion(txtLocalizacionM.getText());
        panelModificar.setVisible(false);
        panelModificar.toBack();
        noExpMin();
        quitarAccion();
    }

    //Prepara la interfaz para eliminar una parada.
    public void eliminarParada(ActionEvent e) {
        parada_Ruta = false;
        setAccionActual(Accion.ELIMINAR_NODO);
        panel.setOnMouseClicked(this::handlePanelClick);
        labelInfoAccion.setText("ACCIÓN: ELIMINAR PARADA");
    }

    //Prepara la interfaz para agregar una nueva ruta entre paradas.
    public void agregarRuta(ActionEvent e) {
        setAccionActual(Accion.AGREGAR_ARISTA);
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0f, 100.0f, 1.0f, 0.5f);
        spnTiempo.setValueFactory(valueFactory);
        spnDistancia.setValueFactory(valueFactory2);
        spnCosto.setValueFactory(valueFactory3);
        panel.setOnMouseClicked(this::handlePanelClick);
        labelInfoAccion.setText("ACCIÓN: AGREGAR RUTA");
    }

    //Confirma la adición de una nueva ruta entre dos paradas.
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

    //Prepara la interfaz para modificar una ruta existente.
    public void modificarRuta(ActionEvent e) {
        accionActual = Accion.NINGUNA;
        labelInfoAccion.setText("ACCIÓN: MODIFICAR RUTA");
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

    //Abre el panel de modificación de detalles de una ruta seleccionada.
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

    //Oculta los paneles de modificación de ruta y restaura la interfaz.
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

    //Prepara la interfaz para eliminar una ruta.
    public void eliminarRuta(ActionEvent e) {
        accionActual = Accion.NINGUNA;
        labelInfoAccion.setText("ACCIÓN: ELIMINAR RUTA");
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

    //Muestra el panel de confirmación para eliminar una ruta.
    public void eliminarR(ActionEvent e) {panelConfirmacion.setVisible(true);}

    //Maneja los clics en el panel del grafo, gestionando diferentes acciones según el estado actual.
    private void handlePanelClick(MouseEvent event) {
        /**/
        mouseTracking = false;
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
            if (distancia < 0.08) { // Rango de selección
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
                            infoRutaOptima();
                        }
                    }
                    break;
            }
        } else {
            System.out.println("No se seleccionó ningún nodo. Haz clic cerca de un nodo.");
        }
    }


    //Selecciona un nodo resaltándolo visualmente.
    private void seleccionarNodo(Node nodo) {
        // Resaltar el nodo seleccionado
        nodo.setAttribute("ui.class", "selected");
    }

    //Deselecciona un nodo, eliminando su estilo de selección.
    private void deselectNodo(Node nodo) {
        // Restablecer el color o quitar el estilo de selección
        if (nodo!=null) nodo.removeAttribute("ui.class");
        else return;
    }

    //Agrega una nueva arista (ruta) entre dos nodos en el grafo.
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

            noExpMin();
            quitarAccion();
        }
    }

    //Elimina una arista (ruta) del grafo.
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

    //Modifica los atributos de una arista existente.
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
            noExpMin();
            quitarAccion();
        } else {
            System.out.println("No existe una arista entre los nodos seleccionados.");
        }
    }

    //Establece las coordenadas de la posición actual en el grafo.
    public void setCoordinates(double x, double y) {
        this.gPosX = x;
        this.gPosY = y;
    }

    //Confirma la eliminación de una ruta o parada.
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
        noExpMin();
        quitarAccion();
        panelConfirmacion.setVisible(false);
    }

    //Cancela la operación de eliminación en curso.
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

    //Alterna el modo de Árbol de Expansión Mínima (MST).
    public void alternarExpMin()
    {
        GestorRutas.getInstance().setExpMin(!GestorRutas.getInstance().getExpMinActivado());
        if (GestorRutas.getInstance().getExpMinActivado()) modoExpMin();
        else desModoExpMin();
    }

    //Activa el modo de Árbol de Expansión Mínima.
    private void modoExpMin()
    {
        mst = GestorRutas.getInstance().getMst(getPrefs());
        for (Map.Entry<Integer, Edge> entry : aristasDelGrafo.entrySet())
        {
            entry.getValue().removeAttribute("ui.class");
            if (!mst.containsKey(entry.getKey())) entry.getValue().setAttribute("ui.class", "invisible");
            else entry.getValue().setAttribute("ui.class", "mstEdge");
        }
        if (ultimaRutaCalculada!=null) desResaltarRuta();
    }

    // Desactiva el modo de Árbol de Expansión Mínima.
    private void desModoExpMin()
    {
        for (Edge e: aristasDelGrafo.values())
            e.removeAttribute("ui.class");
    }

    //Inicia la búsqueda de ruta óptima.
    public void encontrarRuta()
    {
        labelInfoAccion.setText("ACCIÓN: BUSCAR RUTA ÓPTIMA");
        setAccionActual(Accion.BUSCAR_RUTA);
        panel.setOnMouseClicked(this::handlePanelClick);
    }

    //Obtiene las preferencias seleccionadas para el cálculo de rutas.
    private Preferencias[] getPrefs()
    {
        Preferencias prefs[] = new Preferencias[5];
        prefs[0] = Preferencias.getPorValor(cmbPref1.getSelectionModel().getSelectedIndex());
        prefs[1] = Preferencias.getPorValor(cmbPref2.getSelectionModel().getSelectedIndex());
        prefs[2] = Preferencias.getPorValor(cmbPref3.getSelectionModel().getSelectedIndex());
        prefs[3] = Preferencias.getPorValor(cmbPref4.getSelectionModel().getSelectedIndex());
        prefs[4] = Preferencias.NINGUNA;

        return prefs;
    }

    //Calcula la ruta óptima entre dos paradas.
    public List<ParParadaRuta> rutaOptima()
    {
        Preferencias prefs[] = getPrefs();
        //Los ids de las paradas hay que cambiarlos según los nodos a los que se les haga click
        ultimaRutaCalculada = GestorRutas.getInstance().encontrarRuta(paradaSeleccionada1.getId(), paradaSeleccionada2.getId(), prefs);
        return ultimaRutaCalculada;
    }

    //Resalta visualmente la ruta óptima.
    private void resaltarRuta(List<ParParadaRuta> ruta)
    {
        if (rutaOptima() == null)
        {
            setAccionActual(Accion.NINGUNA);
            nodoSeleccionado1 = null;
            nodoSeleccionado2 = null;
        }
        for (ParParadaRuta pr : ruta)
        {
            Node nodo = nodosDelGrafo.get(pr.parada().getId());
            Edge arista = null;
            if (pr.ruta() != null) arista = aristasDelGrafo.get(pr.ruta().getId());
            nodo.removeAttribute("ui.class");
            nodo.setAttribute("ui.class", "highlight");
            if (arista != null)
            {
                if (GestorRutas.getInstance().getExpMinActivado()) arista.setAttribute("ui.class", "highlightExpMin");
                else arista.setAttribute("ui.class", "highlight");
            }
        }
    }

    //Elimina el resaltado de la última ruta calculada.
    private void desResaltarRuta()
    {
        for (ParParadaRuta pr : ultimaRutaCalculada)
        {
            Node nodo = nodosDelGrafo.get(pr.parada().getId());
            Edge arista = null;
            if (pr.ruta() != null) arista = aristasDelGrafo.get(pr.ruta().getId());
            nodo.removeAttribute("ui.class");
            if (arista != null)
            {
                arista.removeAttribute("ui.class");
                if (GestorRutas.getInstance().getExpMinActivado()) arista.setAttribute("ui.class", "mstEdge");
            }
        }
        ultimaRutaCalculada = null;
    }

    //Desactiva el modo de Árbol de Expansión Mínima.
    private void noExpMin()
    {
        GestorRutas.getInstance().desactivarExpMin();
        cbxExpMin.setSelected(false);
        desModoExpMin();
    }

    // Resetea la acción actual.
    private void quitarAccion()
    {
        setAccionActual(Accion.NINGUNA);
        labelInfoAccion.setText("ACCIÓN: NINGUNA");
    }

    //Muestra información detallada de la ruta óptima.
    private void infoRutaOptima()
    {
        float costo = 0, tiempo = 0, distancia = 0, transbordos = 0;
        for (ParParadaRuta pr : ultimaRutaCalculada)
        {
            if (pr.ruta() != null)
            {
                transbordos++;
                costo += pr.ruta().getCostoNeto();
                tiempo += pr.ruta().getTiempo();
                distancia += pr.ruta().getDistancia();
            }
        }
        setAccionActual(Accion.MOSTRAR_RUTA);
        labelInfoAccion.setText(String.format("COSTO: %.2f  TIEMPO: %.2f  DISTANCIA: %.2f  TRANSBORDOS: %.2f", costo, tiempo, distancia, transbordos));
    }

    //Carga los datos de paradas y rutas desde un archivo.
    public void cargarDataDeFichero()
    {
        float w = panelPrincipal.widthProperty().floatValue();
        float h = panelPrincipal.heightProperty().floatValue();

        Map<Integer, Parada> paradas = GestorRutas.getInstance().getParadas();
        for (Parada p : paradas.values())
        {
            int id = p.getId();
            String nodeId = ""+id;
            Node newNode = graph.addNode(nodeId);
            newNode.setAttribute("ui.label", newNode.getId());
            nodosDelGrafo.put(id, newNode);
            Double coordx = p.getCoordx();
            Double coordy = p.getCoordy();
            newNode.setAttribute("x", coordx);
            newNode.setAttribute("y", coordy);
        }

        Map<Integer, Ruta> rutas = GestorRutas.getInstance().getRutas();
        for (Ruta r : rutas.values())
        {
            Node nodo1 = nodosDelGrafo.get(r.getOrigen().getId());
            Node nodo2 = nodosDelGrafo.get(r.getDestino().getId());
            String aristaId = String.valueOf(r.getId());
            graph.addEdge(aristaId, nodo1.getId(), nodo2.getId(), true);
            Edge arista = graph.getEdge(aristaId);

            arista.setAttribute("ui.label", "T: " + r.getTiempo() + " D: " + r.getDistancia() + " C: " + r.getCostoBruto());
            aristasDelGrafo.put(r.getId(), arista);
            arista.setAttribute("Tiempo", r.getTiempo());
            arista.setAttribute("Distancia", r.getDistancia());
            arista.setAttribute("Costo", r.getCostoBruto());
        }

    }

}

