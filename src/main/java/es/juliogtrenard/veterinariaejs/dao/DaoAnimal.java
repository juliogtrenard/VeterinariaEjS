package es.juliogtrenard.veterinariaejs.dao;

import es.juliogtrenard.veterinariaejs.bd.DBConnect;
import es.juliogtrenard.veterinariaejs.modelo.Animal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

/**
 * Clase donde se ejecutan las consultas para la tabla Animales
 */
public class DaoAnimal {
    /**
     * Busca un animal por medio de su id
     *
     * @param id id del animal a buscar
     * @return animal o null
     */
    public static Animal getAnimal(int id) {
        DBConnect connection;
        Animal animal = null;
        try {
            connection = new DBConnect();
            String consulta = "SELECT id,nombre,especie,raza,sexo,edad,peso,observaciones,fecha_primera_consulta,foto FROM Animales WHERE id = ?";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id_animal = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String especie = rs.getString("especie");
                String raza = rs.getString("raza");
                String sexo = rs.getString("sexo");
                int edad = rs.getInt("edad");
                int peso = rs.getInt("peso");
                String observaciones = rs.getString("observaciones");
                LocalDate fecha_primera_consulta = rs.getDate("fecha_primera_consulta").toLocalDate();
                Blob foto = rs.getBlob("foto");
                animal = new Animal(id_animal,nombre,especie,raza,sexo,edad,peso,observaciones,fecha_primera_consulta,foto);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return animal;
    }

    /**
     * Carga los datos de la tabla Animal y los devuelve para usarlos en un listado de animales
     *
     * @return listado de animales para cargar en un tableview
     */
    public static ObservableList<Animal> cargarListado() {
        DBConnect connection;
        ObservableList<Animal> animales = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT id,nombre,especie,raza,sexo,edad,peso,observaciones,fecha_primera_consulta,foto FROM Animales";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id_animal = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String especie = rs.getString("especie");
                String raza = rs.getString("raza");
                String sexo = rs.getString("sexo");
                int edad = rs.getInt("edad");
                int peso = rs.getInt("peso");
                String observaciones = rs.getString("observaciones");
                LocalDate fecha_primera_consulta = rs.getDate("fecha_primera_consulta").toLocalDate();
                Blob foto = rs.getBlob("foto");
                Animal animal = new Animal(id_animal,nombre,especie,raza,sexo,edad,peso,observaciones,fecha_primera_consulta,foto);
                animales.add(animal);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return animales;
    }

    /**
     * Convierte un objeto File a Blob
     *
     * @param file fichero imagen
     * @return blob el blob de la imagen
     * @throws SQLException errores de SQL
     * @throws IOException errores de E/S de datos
     */
    public static Blob convertFileToBlob(File file) throws SQLException, IOException {
        DBConnect connection = new DBConnect();
        // Open a connection to the database
        try (Connection conn = connection.getConnection();
             FileInputStream inputStream = new FileInputStream(file)) {

            // Create Blob
            Blob blob = conn.createBlob();
            // Write the file's bytes to the Blob
            byte[] buffer = new byte[1024];
            int bytesRead;

            try (var outputStream = blob.setBinaryStream(1)) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return blob;
        }
    }

    /**
     * Modifica los datos de un animal en la BD
     *
     * @param animal Animal con datos nuevos
     * @param animalNuevo Nuevos datos del animal a modificar
     * @return true/false
     */
    public static boolean modificar(Animal animal, Animal animalNuevo) {
        DBConnect connection;
        PreparedStatement pstmt;
        try {
            connection = new DBConnect();
            String consulta = "UPDATE Animales SET nombre = ?,especie = ?,raza = ?,sexo = ?,edad = ?,peso = ?,observaciones = ?,fecha_primera_consulta = ?,foto = ? WHERE id = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt = connection.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, animalNuevo.getNombre());
            pstmt.setString(2, animalNuevo.getEspecie());
            pstmt.setString(3, animalNuevo.getRaza());
            pstmt.setString(4, animalNuevo.getSexo());
            pstmt.setInt(5, animalNuevo.getEdad());
            pstmt.setInt(6, animalNuevo.getPeso());
            pstmt.setString(7, animalNuevo.getObservaciones());
            pstmt.setDate(8, Date.valueOf(animalNuevo.getFecha_primera_consulta()));
            pstmt.setBlob(9, animalNuevo.getFoto());
            pstmt.setInt(10,animal.getId());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Crea un nuevo animal en la BD
     *
     * @param animal Animal con datos nuevos
     * @return id/-1
     */
    public  static int insertar(Animal animal) {
        DBConnect connection;
        PreparedStatement pstmt;
        try {
            connection = new DBConnect();
            String consulta = "INSERT INTO Animales (nombre,especie,raza,sexo,edad,peso,observaciones,fecha_primera_consulta,foto) VALUES (?,?,?,?,?,?,?,?,?) ";
            pstmt = connection.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, animal.getNombre());
            pstmt.setString(2, animal.getEspecie());
            pstmt.setString(3, animal.getRaza());
            pstmt.setString(4, animal.getSexo());
            pstmt.setInt(5, animal.getEdad());
            pstmt.setInt(6, animal.getPeso());
            pstmt.setString(7, animal.getObservaciones());
            pstmt.setDate(8, Date.valueOf(animal.getFecha_primera_consulta()));
            pstmt.setBlob(9, animal.getFoto());
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    pstmt.close();
                    connection.closeConnection();
                    return id;
                }
            }
            pstmt.close();
            connection.closeConnection();
            return -1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Elimina un animal
     *
     * @param animal Animal a eliminar
     * @return true/false
     */
    public  static boolean eliminar(Animal animal){
        DBConnect connection;
        PreparedStatement pstmt;
        try {
            connection = new DBConnect();
            String consulta = "DELETE FROM Animales WHERE id = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, animal.getId());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

}
