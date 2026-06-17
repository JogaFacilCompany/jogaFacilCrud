package br.com.jogafacil.view;

import br.com.jogafacil.model.Gerente;
import br.com.jogafacil.model.Quadra;
import br.com.jogafacil.util.Arquivo;
import br.com.jogafacil.util.Validador;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GerenteView {

    //tela de lista
    public Scene getSceneLista(Stage stage) {
        Label titulo = new Label("Gerentes");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //Tabela
        TableView<Gerente> tabela = new TableView<>();

        TableColumn<Gerente, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Gerente, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Gerente, String> colQuadra = new TableColumn<>("Quadra");
        colQuadra.setCellValueFactory(c -> new SimpleStringProperty(descreverQuadra(c.getValue().getQuadra())));

        tabela.getColumns().addAll(colNome, colEmail, colQuadra);

        // Carrega os dados do disco e enche a tabela
        ArrayList<Gerente> lista = Arquivo.carregar("gerentes.dat");
        tabela.getItems().addAll(lista);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        //Botões
        Button btnNovo    = new Button("Novo Gerente");
        Button btnEditar  = new Button("Editar");
        Button btnExcluir = new Button("Excluir");
        Button btnVoltar  = new Button("Voltar");

        btnNovo.setOnAction(e -> stage.setScene(getSceneFormulario(stage, null, lista)));

        btnEditar.setOnAction(e -> {
            Gerente selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um gerente para editar.");
                return;
            }
            stage.setScene(getSceneFormulario(stage, selecionado, lista));
        });

        btnExcluir.setOnAction(e -> {
            Gerente selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um gerente para excluir.");
                return;
            }

            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION,
                    "Excluir este gerente? Esta ação não pode ser desfeita.",
                    ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    lista.remove(selecionado);
                    Arquivo.salvar(lista, "gerentes.dat");
                    stage.setScene(getSceneLista(stage));
                }
            });
        });

        btnVoltar.setOnAction(e -> stage.setScene(new LocadorHomeView().getScene(stage)));

        HBox botoes = new HBox(10, btnNovo, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), tabela, lblErro, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 640, 440);
    }

    //TELA DE FORMULÁRIO (criar/editar)
    public Scene getSceneFormulario(Stage stage, Gerente gerente, ArrayList<Gerente> lista) {
        boolean modoEdicao = (gerente != null);

        Label titulo = new Label(modoEdicao ? "Editar Gerente" : "Novo Gerente");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField txtNome  = new TextField(modoEdicao ? gerente.getNome()  : "");
        TextField txtEmail = new TextField(modoEdicao ? gerente.getEmail() : "");
        PasswordField txtSenha = new PasswordField();
        if (modoEdicao) {
            txtSenha.setText(gerente.getSenha());
        }

        txtNome.setPromptText("Nome completo");
        txtEmail.setPromptText("seu@email.com");
        txtSenha.setPromptText("Mínimo 4 caracteres");

        // ComboBox de Quadra: carrega as quadras cadastradas (Aluno 4)
        ComboBox<Quadra> cmbQuadra = new ComboBox<>();
        ArrayList<Quadra> quadras = Arquivo.carregar("quadras.dat");
        cmbQuadra.getItems().addAll(quadras);
        cmbQuadra.setPromptText("Selecione a quadra");
        cmbQuadra.setConverter(new StringConverter<Quadra>() {
            @Override public String toString(Quadra q) { return q == null ? null : descreverQuadra(q); }
            @Override public Quadra fromString(String s) { return null; }
        });
        if (modoEdicao) {
            cmbQuadra.setValue(gerente.getQuadra());
        }

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        if (!modoEdicao && quadras.isEmpty()) {
            lblErro.setText("Cadastre uma quadra antes de criar um gerente.");
            btnSalvar.setDisable(true);
        }

        btnSalvar.setOnAction(e -> {
            String nome  = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String senha = txtSenha.getText();
            Quadra quadra = cmbQuadra.getValue();

            String erros = Validador.validarPessoa(nome, email, senha);
            if (!erros.isEmpty()) {
                lblErro.setText(erros);
                return;
            }
            if (quadra == null) {
                lblErro.setText("Selecione uma quadra.");
                return;
            }

            if (!modoEdicao) {
                lista.add(new Gerente(nome, email, senha, quadra));
            } else {
                gerente.setNome(nome);
                gerente.setEmail(email);
                gerente.setSenha(senha);
                gerente.setQuadra(quadra);
            }

            Arquivo.salvar(lista, "gerentes.dat");
            mostrarInfo("Gerente salvo com sucesso!");
            stage.setScene(getSceneLista(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getSceneLista(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Nome:"),   0, 0); grid.add(txtNome,   1, 0);
        grid.add(new Label("Email:"),  0, 1); grid.add(txtEmail,  1, 1);
        grid.add(new Label("Senha:"),  0, 2); grid.add(txtSenha,  1, 2);
        grid.add(new Label("Quadra:"), 0, 3); grid.add(cmbQuadra, 1, 3);
        grid.add(lblErro,              1, 4);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, new Separator(), grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 480, 380);
    }

    // Texto amigável para exibir a quadra em tabelas e combos (Quadra não tem toString()).
    private String descreverQuadra(Quadra q) {
        if (q == null) return "(sem quadra)";
        String mod = (q.getModalidade() != null) ? q.getModalidade().getNome() : "sem modalidade";
        return q.getEndereco() + " — " + mod;
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
