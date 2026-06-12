module br.com.jogafacil {
    requires javafx.controls;
    exports br.com.jogafacil;
    exports br.com.jogafacil.util;
    exports br.com.jogafacil.model;
    opens br.com.jogafacil.model to javafx.base;
    exports br.com.jogafacil.view;
}