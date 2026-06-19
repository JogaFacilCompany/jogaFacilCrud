package br.com.jogafacil.view;

import br.com.jogafacil.model.Locador;
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

public class LocadorPerfilView {

    public Scene getScene(Stage stage) {
        Locador locador = (Locador) SessaoUsuario.getInstancia().getUsuarioLogado();

        Label titulo = new Label("Meu Perfil");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        Label lblNome = new Label("Nome: " + locador.getNome());
        Label lblEmail = new Label("Email: " + locador.getEmail());
        Label lblTipo = new Label("Tipo: " + locador.getTipo());
        lblNome.setStyle("-fx-font-size: 14px;");
        lblEmail.setStyle("-fx-font-size: 14px;");
        lblTipo.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");



        // Botões
        Button btnEditar  = new Button("Editar Perfil");
        Button btnExcluir = new Button("Excluir Conta");
        Button btnVoltar  = new Button("Voltar");


        btnEditar.setOnAction(e -> {
            ArrayList<Locador> todos = Arquivo.carregar("locadores.dat");
            stage.setScene(getSceneFormulario(stage, locador, todos));
        });


        btnExcluir.setOnAction(e -> {
            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION, "Excluir sua conta? Esta ação não pode ser desfeita.", ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    ArrayList<Locador> todos = Arquivo.carregar("locadores.dat");
                    todos.remove(locador);
                    Arquivo.salvar(todos,"locadores.dat");
                    SessaoUsuario.getInstancia().logout();
                    stage.setScene(new LoginView().getScene(stage));
                }
            });
        });


        btnVoltar.setOnAction(e -> {
            stage.setScene(new LocadorHomeView().getScene(stage));
        });

        VBox dados = new VBox(10, lblNome, lblEmail, lblTipo);
        dados.setPadding(new Insets(15, 0, 15, 0));

        HBox botoes = new HBox(10, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), dados, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 420, 280);
    }
// tela edição
    public Scene getSceneFormulario(Stage stage, Locador locador, ArrayList<Locador> todos) {
        Label titulo = new Label("Editar Perfil");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField     txtNome  = new TextField(locador.getNome());
        TextField     txtEmail = new TextField(locador.getEmail());
        TextField txtSenha = new TextField();
        txtSenha.setText(locador.getSenha());

        txtNome.setPromptText("Nome completo");
        txtEmail.setPromptText("seu@email.com");
        txtSenha.setPromptText("Mínimo 4 caracteres");

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        btnSalvar.setOnAction(e -> {
            try {
                String erros = Validador.validarPessoa(txtNome.getText(), txtEmail.getText(), txtSenha.getText());
                if (!erros.isEmpty()) { lblErro.setText(erros); return; }
                locador.setNome(txtNome.getText().trim());
                locador.setEmail(txtEmail.getText().trim());
                locador.setSenha(txtSenha.getText());
                Arquivo.salvar(todos, "locadores.dat");
                stage.setScene(getScene(stage));
            } catch (Exception ex) {
                lblErro.setText("Erro ao salvar perfil: " + ex.getMessage());
            }
        });

        btnCancelar.setOnAction(e -> stage.setScene(getScene(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Nome:"),  0, 0); grid.add(txtNome,  1, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(txtEmail, 1, 1);
        grid.add(new Label("Senha:"), 0, 2); grid.add(txtSenha, 1, 2);
        grid.add(lblErro,             1, 3);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 460, 280);
    }
}
