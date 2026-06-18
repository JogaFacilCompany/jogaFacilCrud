package br.com.jogafacil.view;

import br.com.jogafacil.model.Gerente;
import br.com.jogafacil.model.Quadra;
import br.com.jogafacil.util.SessaoUsuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GerenteHomeView {

    public Scene getScene(Stage stage) {
        SessaoUsuario sessao = SessaoUsuario.getInstancia();
        Gerente gerente = (Gerente) sessao.getUsuarioLogado();

        Label lblSaudacao = new Label("Olá, " + sessao.getNomeUsuario() + "!");
        Label lblTipo     = new Label("Perfil: Gerente");
        lblSaudacao.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        lblTipo.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // Card (somente leitura) com a quadra delegada pelo locador
        Label lblQuadraTitulo = new Label("Minha Quadra");
        lblQuadraTitulo.setStyle("-fx-font-weight: bold;");
        Label lblQuadraInfo = new Label(descreverQuadra(gerente.getQuadra()));
        lblQuadraInfo.setWrapText(true);
        VBox cardQuadra = new VBox(4, lblQuadraTitulo, lblQuadraInfo);
        cardQuadra.setPadding(new Insets(10));
        cardQuadra.setStyle("-fx-border-color: #ccc; -fx-border-radius: 4; -fx-background-color: #f7f7f7;");

        Button btnReservas = new Button("Reservas");
        Button btnEquipamentos = new Button("Equipamentos");
        Button btnTorneios     = new Button("Torneios");
        Button btnSair         = new Button("Sair");

        for (Button b : new Button[]{btnReservas, btnEquipamentos, btnTorneios, btnSair})
            b.setMaxWidth(Double.MAX_VALUE);
        btnSair.setStyle("-fx-text-fill: red;");

        btnReservas.setOnAction(e ->
                stage.setScene(new ReservaView().getSceneLista(stage)));
        btnEquipamentos.setOnAction(e -> stage.setScene(new EquipamentoView().getSceneLista(stage)));
        btnTorneios.setOnAction(e -> stage.setScene(new TorneioView().getSceneLista(stage)));
        btnSair.setOnAction(e -> {
            SessaoUsuario.getInstancia().logout();
            stage.setScene(new LoginView().getScene(stage));
        });

        VBox cabecalho = new VBox(4, lblSaudacao, lblTipo);
        VBox navegacao = new VBox(8,
                cardQuadra, new Separator(),
                btnReservas, btnEquipamentos, btnTorneios,
                new Separator(), btnSair
        );
        navegacao.setFillWidth(true);

        VBox layout = new VBox(20, cabecalho, new Separator(), navegacao);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setMaxWidth(360);

        StackPane root = new StackPane(layout);
        root.setPadding(new Insets(20));

        return new Scene(root, 440, 440);
    }

    private String descreverQuadra(Quadra q) {
        if (q == null) return "Nenhuma quadra delegada.";
        String mod = (q.getModalidade() != null) ? q.getModalidade().getNome() : "sem modalidade";
        return q.getEndereco()
                + "\nModalidade: " + mod
                + "\nCNPJ: " + q.getCnpj()
                + String.format("\nValor: R$ %.2f", q.getValor());
    }
}
