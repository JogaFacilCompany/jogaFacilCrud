package br.com.jogafacil.view;

import br.com.jogafacil.util.SessaoUsuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LocadorHomeView {

    public Scene getScene(Stage stage) {
        SessaoUsuario sessao = SessaoUsuario.getInstancia();

        Label lblSaudacao = new Label("Olá, " + sessao.getNomeUsuario() + "!");
        Label lblTipo     = new Label("Perfil: Locador");
        lblSaudacao.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        lblTipo.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Button btnPerfil      = new Button("👤  Meu Perfil");
        Button btnQuadras     = new Button("🏟️  Minhas Quadras");
        Button btnGerentes    = new Button("👷  Meus Gerentes");
        Button btnModalidades = new Button("⚽  Modalidades");
        Button btnSair        = new Button("Sair");

        for (Button b : new Button[]{btnPerfil, btnQuadras, btnGerentes, btnModalidades, btnSair})
            b.setMaxWidth(Double.MAX_VALUE);

        btnSair.setStyle("-fx-text-fill: red;");

        btnPerfil.setOnAction(e ->
                stage.setScene(new LocadorPerfilView().getScene(stage))
        );

        // Conecte quando os alunos 1 e 4 terminarem:
        // btnQuadras.setOnAction(e -> stage.setScene(new QuadraView().getSceneLista(stage)));
        // btnGerentes.setOnAction(e -> stage.setScene(new GerenteView().getSceneLista(stage)));
        // btnModalidades.setOnAction(e -> stage.setScene(new ModalidadeView().getSceneLista(stage)));

        btnSair.setOnAction(e -> {
            SessaoUsuario.getInstancia().logout();
            stage.setScene(new LoginView().getScene(stage));
        });

        VBox cabecalho = new VBox(4, lblSaudacao, lblTipo);
        VBox navegacao = new VBox(8,
                btnPerfil, new Separator(),
                btnQuadras, btnGerentes, btnModalidades,
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
