package es.juliogtrenard.veterinariaejs.controladores;

import es.juliogtrenard.veterinariaejs.dao.DaoAnimal;
import es.juliogtrenard.veterinariaejs.modelo.Animal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de datos de animal
 */
public class DatosController implements Initializable {
    /**
     * Un animal
     */
    private Animal animal;

    /**
     * Una imagen
     */
    private Blob imagen;

    /**
     * Boton para borrar la foto
     */
    @FXML
    private Button btnFotoBorrar;

    /**
     * Fecha de la primera consulta
     */
    @FXML
    private DatePicker fecha;

    /**
     * Foto del animal
     */
    @FXML
    private ImageView foto;

    /**
     * RadioButton para femenino
     */
    @FXML
    private RadioButton rbFemenino;

    /**
     * RadioButton para masculino
     */
    @FXML
    private RadioButton rbMasculino;

    /**
     * Grupo para los radio buttons
     */
    public ToggleGroup tgSexo;

    /**
     * Cuadro de texto para la edad
     */
    @FXML
    private TextField txtEdad;

    /**
     * Cuadro de texto para la especie
     */
    @FXML
    private TextField txtEspecie;

    /**
     * Cuadro de texto para el nombre
     */
    @FXML
    private TextField txtNombre;

    /**
     * Cuadro de texto para las observaciones
     */
    @FXML
    private TextField txtObservaciones;

    /**
     * Cuadro de texto para el peso
     */
    @FXML
    private TextField txtPeso;

    /**
     * Cuadro de texto para la raza
     */
    @FXML
    private TextField txtRaza;

    /**
     * Constructor que define el animal a editar
     *
     * @param animal a editar
     */
    public DatosController(Animal animal) {
        this.animal = animal;
    }

    /**
     * Constructor vacío
     */
    public DatosController() {
        this.animal = null;
    }

    /**
     * Se ejecuta cuando se carga la ventana
     *
     * @param url la url
     * @param resourceBundle los recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.imagen = null;
        btnFotoBorrar.setDisable(true);
        foto.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/juliogtrenard/veterinariaejs/img/imagenpordefecto.jpg"))));
        if (this.animal!=null) {
            txtNombre.setText(animal.getNombre());
            txtEspecie.setText(animal.getEspecie());
            txtRaza.setText(animal.getRaza());
            if (animal.getSexo().equals("Femenino")) {
                rbFemenino.setSelected(true);
            }
            txtEdad.setText(animal.getEdad() + "");
            txtPeso.setText(animal.getPeso() + "");
            txtObservaciones.setText(animal.getObservaciones());
            fecha.setValue(animal.getFecha_primera_consulta());
            if (animal.getFoto() != null) {
                this.imagen = animal.getFoto();
                try {
                    InputStream imagen = animal.getFoto().getBinaryStream();
                    foto.setImage(new Image(imagen));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                btnFotoBorrar.setDisable(false);
            }
        }
    }

    /**
     * Abre un FileChooser para seleccionar una imagen
     *
     * @param event el evento
     */
    @FXML
    void seleccionImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione una foto del animal");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.jpg","*.png"));
        File file = fileChooser.showOpenDialog(null);
        try {
            double kbs = (double) file.length() / 1024;
            if (kbs > 64) {
                alerta("La imagen no puede ser mayor a 64KB");
            } else {
                InputStream imagen = new FileInputStream(file);
                Blob blob = DaoAnimal.convertFileToBlob(file);
                this.imagen = blob;
                foto.setImage(new Image(imagen));
                btnFotoBorrar.setDisable(false);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Borra la foto del animal
     *
     * @param event el evento
     */
    @FXML
    void borrarFoto(ActionEvent event) {
        imagen = null;
        foto.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/juliogtrenard/veterinariaejs/img/imagenpordefecto.jpg"))));
        btnFotoBorrar.setDisable(true);
    }

    /**
     * Cierra la ventana
     *
     * @param event el evento
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage)txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Valida y procesa los datos
     *
     * @param event el evento
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            alerta(error);
        } else {
            Animal nuevo = new Animal();
            nuevo.setNombre(txtNombre.getText());
            nuevo.setEspecie(txtEspecie.getText());
            nuevo.setRaza(txtRaza.getText());
            if (rbMasculino.isSelected()) {
                nuevo.setSexo("Masculino");
            } else {
                nuevo.setSexo("Femenino");
            }
            nuevo.setEdad(Integer.parseInt(txtEdad.getText()));
            nuevo.setPeso(Integer.parseInt(txtPeso.getText()));
            nuevo.setObservaciones(txtObservaciones.getText());
            nuevo.setFecha_primera_consulta(fecha.getValue());
            nuevo.setFoto(this.imagen);
            if (this.animal == null) {
                int id = DaoAnimal.insertar(nuevo);
                if (id == -1) {
                    alerta("Error almacenando los datos. Vuelva a intentarlo.");
                } else {
                    confirmacion("Animal añadido correctamente.");
                    this.cancelar(null);
                }
            } else {
                if (DaoAnimal.modificar(this.animal, nuevo)) {
                    confirmacion("Animal actualizado correctamente.");
                    this.cancelar(null);
                } else {
                    alerta("Ha habido un error almacenando los datos. Por favor vuelva a intentarlo.");
                }
            }
        }
    }

    /**
     * Valida los datos del formulario
     *
     * @return string con posibles errores
     */
    public String validar() {
        String error = "";
        if (txtNombre.getText().isEmpty()) {
            error += "El campo nombre no puede estar vacío\n";
        }
        if (txtEspecie.getText().isEmpty()) {
            error += "El campo especie no puede estar vacío\n";
        }
        if (txtRaza.getText().isEmpty()) {
            error += "El campo raza no puede estar vacío\n";
        }
        if (txtEdad.getText().isEmpty()) {
            error += "El campo edad no puede estar vacío\n";
        } else {
            try {
                Integer.parseInt(txtEdad.getText());
            } catch (NumberFormatException e) {
                error += "El campo edad tiene que ser numérico\n";
            }
        }
        if (txtPeso.getText().isEmpty()) {
            error += "El campo peso no puede estar vacío\n";
        } else {
            try {
                Integer.parseInt(txtPeso.getText());
            } catch (NumberFormatException e) {
                error += "El campo peso tiene que ser numérico\n";
            }
        }
        if (txtObservaciones.getText().isEmpty()) {
            error += "El campo observaciones no puede estar vacío\n";
        }
        if (fecha.getValue() == null) {
            error += "El campo fecha de la primera consulta no puede estar vacío\n";
        }
        return error;
    }

    /**
     * Muestra un mensaje de alerta al usuario
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
     * Muestra un mensaje de confirmación al usuario
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

}
