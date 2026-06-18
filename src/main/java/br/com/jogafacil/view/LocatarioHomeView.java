package br.com.jogafacil.view;

import br.com.jogafacil.util.SessaoUsuario;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LocatarioHomeView {

    public Scene getScene(Stage stage) {
        SessaoUsuario sessao = SessaoUsuario.getInstancia();

        Label lblSaudacao = new Label("Olá, " + sessao.getNomeUsuario() + "!");
        Label lblTipo     = new Label("Perfil: Locatário");
        lblSaudacao.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        lblTipo.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Button btnPerfil   = new Button("Meu Perfil");
        Button btnReservas = new Button("Minhas Reservas");
        Button btnLobbies  = new Button("Lobbies");
        Button btnTorneios = new Button("Torneios");
        Button btnSair     = new Button("Sair");

        for (Button b : new Button[]{btnPerfil, btnReservas, btnLobbies, btnTorneios, btnSair})
            b.setMaxWidth(Double.MAX_VALUE);

        btnLobbies.setOnAction(e ->
                stage.setScene(new LobbyView().getSceneLista(stage)));
        btnSair.setStyle("-fx-text-fill: red;");
        btnReservas.setOnAction(e ->
                stage.setScene(new ReservaView().getSceneLista(stage)));
        btnPerfil.setOnAction(e ->
                stage.setScene(new LocatarioPerfilView().getScene(stage))
        );


        btnSair.setOnAction(e -> {
            SessaoUsuario.getInstancia().logout();
            stage.setScene(new LoginView().getScene(stage));
        });

        VBox cabecalho = new VBox(4, lblSaudacao, lblTipo);
        VBox navegacao = new VBox(8,
                btnPerfil, new Separator(),
                btnReservas, btnLobbies, btnTorneios,
                new Separator(), btnSair
        );
        navegacao.setFillWidth(true);

        VBox layout = new VBox(20, cabecalho, new Separator(), navegacao);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setMaxWidth(320);

        StackPane root = new StackPane(layout);
        root.setPadding(new Insets(20));

        return new Scene(root, 400, 400);
    }
}
