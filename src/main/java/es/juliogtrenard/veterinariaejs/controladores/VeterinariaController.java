package es.juliogtrenard.veterinariaejs.controladores;

import es.juliogtrenard.veterinariaejs.dao.DaoAnimal;
import es.juliogtrenard.veterinariaejs.modelo.Animal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana principal
 */
public class VeterinariaController implements Initializable {
    /**
     * Boton para editar
     */
    @FXML
    private MenuItem btnEditar;

    /**
     * Boton para eliminar
     */
    @FXML
    private MenuItem btnEliminar;

    /**
     * Boton para mostrar datos
     */
    @FXML
    private MenuItem btnDatos;

    /**
     * Campo de texto para filtrar por nombre
     */
    @FXML
    private TextField filtroNombre;

    /**
     * Tabla de animales
     */
    @FXML
    private TableView<Animal> tabla;

    private ObservableList<Animal> masterData = FXCollections.observableArrayList();
    private ObservableList<Animal> filteredData = FXCollections.observableArrayList();

    /**
     * Se ejecuta cuando se carga la ventana
     *
     * @param url la url
     * @param resourceBundle recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deshabilitarMenus(true);

        crearTabla();

        cargarAnimales();

        menuContextual();

        tabla.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> deshabilitarMenus(newValue == null));

        filtroNombre.setOnKeyTyped(_ -> filtrar());

        tabla.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editar(null);
            }
        });
    }

    /**
     * Configura el menú contextual
     */
    private void menuContextual() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem = new MenuItem("Editar");
        MenuItem borrarItem = new MenuItem("Eliminar");
        contextMenu.getItems().addAll(editarItem,borrarItem);
        editarItem.setOnAction(this::editar);
        borrarItem.setOnAction(this::eliminar);
        tabla.setRowFactory(_ -> {
            TableRow<Animal> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    tabla.getSelectionModel().select(row.getItem());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    /**
     * Abre una ventana para editar el animal seleccionado
     *
     * @param event el evento
     */
    @FXML
    void editar(ActionEvent event) {
        Animal animal = tabla.getSelectionModel().getSelectedItem();
        if (animal == null) {
            alerta("Tienes que seleccionar un animal de la lista");
        } else {
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/veterinariaejs/fxml/datos.fxml"));
                DatosController controlador = new DatosController(animal);
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setTitle("Editar animal");
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarAnimales();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                alerta("Error al abrir la ventana. Inténtelo de nuevo.");
            }
        }
    }

    /**
     * Elimina un animal
     *
     * @param event el evento
     */
    @FXML
    void eliminar(ActionEvent event) {
        Animal animal = tabla.getSelectionModel().getSelectedItem();
        if (animal == null) {
            alerta("Tienes que seleccionar un animal de la lista");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(tabla.getScene().getWindow());
            alert.setHeaderText(null);
            alert.setTitle("Confirmación");
            alert.setContentText("¿Estás seguro que quieres eliminar ese animal?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                if (DaoAnimal.eliminar(animal)) {
                    confirmacion("Animal eliminado correctamente");
                    cargarAnimales();
                } else {
                    alerta("Error al intentar eliminar. Inténtelo de nuevo.");
                }
            }
        }
    }

    /**
     * Abre una ventana para añadir un animal
     *
     * @param event el evento
     */
    @FXML
    void nuevo(ActionEvent event) {
        try {
            Window ventana = tabla.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/veterinariaejs/fxml/datos.fxml"));
            DatosController controlador = new DatosController();
            fxmlLoader.setController(controlador);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Añadir un nuevo animal");
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarAnimales();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            alerta("Error al abrir la ventana. Inténtelo de nuevo.");
        }
    }

    /**
     * Función que filtra la tabla por nombre
     */
    public void filtrar() {
        String valor = filtroNombre.getText();
        if (valor != null) {
            valor = valor.toLowerCase();
            if (valor.isEmpty()) {
                tabla.setItems(masterData);
            } else {
                filteredData.clear();
                for (Animal animal : masterData) {
                    String nombre = animal.getNombre();
                    nombre = nombre.toLowerCase();
                    if (nombre.contains(valor)) {
                        filteredData.add(animal);
                    }
                }
                tabla.setItems(filteredData);
            }
        }
    }

    /**
     * Función para habilitar o deshabilitar los botones de edición del menu
     *
     * @param deshabilitado true/false
     */
    public void deshabilitarMenus(boolean deshabilitado) {
        btnEditar.setDisable(deshabilitado);
        btnEliminar.setDisable(deshabilitado);
        btnDatos.setDisable(deshabilitado);
    }

    /**
     * Carga las columnas de la tabla
     */
    public void crearTabla() {
        TableColumn<Animal, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory("id"));
        TableColumn<Animal, String> colNombre = new TableColumn<>("NOMBRE");
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        TableColumn<Animal, String> colEspecie = new TableColumn<>("ESPECIE");
        colEspecie.setCellValueFactory(new PropertyValueFactory("especie"));
        TableColumn<Animal, String> colRaza = new TableColumn<>("RAZA");
        colRaza.setCellValueFactory(new PropertyValueFactory("raza"));
        TableColumn<Animal, String> colSexo = new TableColumn<>("SEXO");
        colSexo.setCellValueFactory(new PropertyValueFactory("sexo"));
        TableColumn<Animal, Integer> colEdad = new TableColumn<>("EDAD");
        colEdad.setCellValueFactory(new PropertyValueFactory("edad"));
        TableColumn<Animal, Integer> colPeso = new TableColumn<>("PESO");
        colPeso.setCellValueFactory(new PropertyValueFactory("peso"));
        TableColumn<Animal, LocalDate> colFecha = new TableColumn<>("PRIMERA CONSULTA");
        colFecha.setCellValueFactory(new PropertyValueFactory("fecha_primera_consulta"));

        colId.prefWidthProperty().bind(tabla.widthProperty().divide(8));
        colNombre.prefWidthProperty().bind(tabla.widthProperty().divide(8));
        colEspecie.prefWidthProperty().bind(tabla.widthProperty().divide(8));
        colRaza.prefWidthProperty().bind(tabla.widthProperty().divide(8));
        colSexo.prefWidthProperty().bind(tabla.widthProperty().divide(8));
        colEdad.prefWidthProperty().bind(tabla.widthProperty().divide(8));
        colPeso.prefWidthProperty().bind(tabla.widthProperty().divide(8));
        colFecha.prefWidthProperty().bind(tabla.widthProperty().divide(8));

        tabla.getColumns().addAll(colId,colNombre,colEspecie,colRaza,colSexo,colEdad,colPeso,colFecha);
    }

    /**
     * Carga los datos de los animales en la tabla
     */
    private void cargarAnimales() {
        filtroNombre.setText(null);
        tabla.getItems().clear();
        masterData.clear();
        filteredData.clear();
        ObservableList<Animal> animales = DaoAnimal.cargarListado();
        masterData.addAll(animales);
        tabla.setItems(animales);
    }

    public void mostrarDatos() {
        Animal animal = tabla.getSelectionModel().getSelectedItem();
        if (animal == null) {
            alerta("Tienes que seleccionar un animal de la tabla");
        } else {
            String info = animal.toString();
            confirmacion(info);
        }
    }

    /**
     * Función que muestra un mensaje de alerta al usuario
     *
     * @param texto contenido de la alerta
     */
    public void alerta(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("Error");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    /**
     * Función que muestra un mensaje de confirmación al usuario
     *
     * @param texto contenido del mensaje
     */
    public void confirmacion(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    /**
     * Información de la aplicación
     * @param actionEvent el evento
     */
    public void acercaDe(ActionEvent actionEvent) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Acerca de:");
        alerta.setContentText("VETERINARIA GASTEIZ\nDesarrollado por Julio Gonzalez");
        alerta.showAndWait();
    }
}