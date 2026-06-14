package br.com.jogafacil.view;

import br.com.jogafacil.model.Locatario;
import br.com.jogafacil.util.Arquivo;
import br.com.jogafacil.util.SessaoUsuario;
import br.com.jogafacil.util.Validador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class LocatarioPerfilView {

    public Scene getScene(Stage stage) {
        Locatario locatario = (Locatario) SessaoUsuario.getInstancia().getUsuarioLogado();

        Label titulo = new Label("Meu Perfil");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblNome = new Label("Nome:" + locatario.getNome());
        Label lblEmail = new Label("Email:" + locatario.getEmail());
        Label lblTipo = new Label("Tipo:" + locatario.getTipo());
        lblNome.setStyle("-fx-font-size: 14px;");
        lblEmail.setStyle("-fx-font-size: 14px;");
        lblTipo.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");


        Button btnEditar  = new Button("Editar Perfil");
        Button btnExcluir = new Button("Excluir Conta");
        Button btnVoltar  = new Button("Voltar");

        btnEditar.setOnAction(e -> {
            ArrayList<Locatario> todos = Arquivo.carregar("Locatarios.dat");
            stage.setScene(getSceneFormulario(stage, locatario, todos));
        });

        btnExcluir.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Excluir sua conta? Esta ação não pode ser desfeita.",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmar exclusão");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    ArrayList<Locatario> todos = Arquivo.carregar("locatarios.dat");
                    todos.remove(locatario);
                    Arquivo.salvar(todos, "locatarios.dat");
                    SessaoUsuario.getInstancia().logout();
                    stage.setScene(new LoginView().getScene(stage));
                }
            });

        });

        btnVoltar.setOnAction(e -> {
            stage.setScene(new LocatarioHomeView().getScene(stage));
        });

        VBox dados = new VBox(10, lblNome, lblEmail, lblTipo);
        dados.setPadding(new Insets(15, 0, 15, 0));

        HBox botoes = new HBox(10, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), dados, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 420, 280);
    }


    public Scene getSceneFormulario(Stage stage, Locatario locatario, ArrayList<Locatario> todos) {
        Label titulo = new Label("Editar Perfil");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField     txtNome  = new TextField(locatario.getNome());
        TextField     txtEmail = new TextField(locatario.getEmail());
        TextField txtSenha = new TextField();
        txtSenha.setText(locatario.getSenha());

        txtNome.setPromptText("Nome completo");
        txtEmail.setPromptText("seu@email.com");
        txtSenha.setPromptText("Mínimo 4 caracteres");

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        btnSalvar.setOnAction(e -> {
            String erros = Validador.validarPessoa(
                    txtNome.getText(), txtEmail.getText(), txtSenha.getText()
            );
            if (!erros.isEmpty()) { lblErro.setText(erros); return; }

            locatario.setNome(txtNome.getText().trim());
            locatario.setEmail(txtEmail.getText().trim());
            locatario.setSenha(txtSenha.getText());

            Arquivo.salvar(todos, "locatarios.dat");
            stage.setScene(getScene(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getScene(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(15); grid.setPadding(new Insets(15));
        grid.add(new Label("Nome:"),  0, 0); grid.add(txtNome,  1, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(txtEmail, 1, 1);
        grid.add(new Label("Senha:"), 0, 2); grid.add(txtSenha, 1, 2);
        grid.add(lblErro,             1, 3);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 420, 280);
    }
}