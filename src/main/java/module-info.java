module br.com.jogafacil {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    exports br.com.jogafacil;
    exports br.com.jogafacil.util;
    exports br.com.jogafacil.model;
    opens br.com.jogafacil.model to javafx.base;
    exports br.com.jogafacil.view;
}