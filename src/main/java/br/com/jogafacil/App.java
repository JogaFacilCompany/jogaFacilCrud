package br.com.jogafacil;

import br.com.jogafacil.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Joga Fácil");
        stage.setScene(new LoginView().getScene(stage));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}