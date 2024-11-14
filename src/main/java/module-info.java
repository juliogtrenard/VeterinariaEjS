module es.juliogtrenard.veterinariaejs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens es.juliogtrenard.veterinariaejs to javafx.fxml;
    exports es.juliogtrenard.veterinariaejs;
    opens es.juliogtrenard.veterinariaejs.controladores to javafx.fxml;
    exports es.juliogtrenard.veterinariaejs.controladores;
    exports es.juliogtrenard.veterinariaejs.bd;
    exports es.juliogtrenard.veterinariaejs.dao;
    exports es.juliogtrenard.veterinariaejs.modelo;
}