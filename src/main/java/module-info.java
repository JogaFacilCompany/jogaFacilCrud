module br.com.jogafacil {
    requires javafx.controls;
    requires javafx.fxml;

    opens br.com.jogafacil to javafx.fxml;
    exports br.com.jogafacil;
}
