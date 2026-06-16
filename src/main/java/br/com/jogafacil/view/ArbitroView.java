package br.com.jogafacil.view;

import br.com.jogafacil.model.Arbitro;
import br.com.jogafacil.util.Arquivo;
import br.com.jogafacil.util.Validador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ArbitroView {

    //tela da lista
    public Scene getSceneLista(Stage stage) {
        Label titulo = new Label("Árbitros");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //Tabela
        TableView<Arbitro> tabela = new TableView<>();

        TableColumn<Arbitro, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Arbitro, String> colCpf = new TableColumn<>("CPF");
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        TableColumn<Arbitro, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        TableColumn<Arbitro, Double> colValor = new TableColumn<>("Valor/Partida");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorPartida"));

        tabela.getColumns().addAll(colNome, colCpf, colTelefone, colValor);

        // Carrega os dados do disco e enche a tabela
        ArrayList<Arbitro> lista = Arquivo.carregar("arbitros.dat");
        tabela.getItems().addAll(lista);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        //Botões
        Button btnNovo    = new Button("Novo Árbitro");
        Button btnEditar  = new Button("Editar");
        Button btnExcluir = new Button("Excluir");
        Button btnVoltar  = new Button("Voltar");

        btnNovo.setOnAction(e -> stage.setScene(getSceneFormulario(stage, null, lista)));

        btnEditar.setOnAction(e -> {
            Arbitro selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um árbitro para editar.");
                return;
            }
            stage.setScene(getSceneFormulario(stage, selecionado, lista));
        });

        btnExcluir.setOnAction(e -> {
            Arbitro selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um árbitro para excluir.");
                return;
            }

            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION,
                    "Excluir este árbitro? Esta ação não pode ser desfeita.",
                    ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    lista.remove(selecionado);
                    Arquivo.salvar(lista, "arbitros.dat");
                    stage.setScene(getSceneLista(stage));
                }
            });
        });

        btnVoltar.setOnAction(e -> stage.setScene(new LocadorHomeView().getScene(stage)));

        HBox botoes = new HBox(10, btnNovo, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), tabela, lblErro, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 600, 420);
    }


    //tela do formulario
    public Scene getSceneFormulario(Stage stage, Arbitro arbitro, ArrayList<Arbitro> lista) {
        boolean modoEdicao = (arbitro != null);

        Label titulo = new Label(modoEdicao ? "Editar Árbitro" : "Novo Árbitro");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField txtNome     = new TextField(modoEdicao ? arbitro.getNome() : "");
        TextField txtCpf      = new TextField(modoEdicao ? arbitro.getCpf() : "");
        TextField txtTelefone = new TextField(modoEdicao ? arbitro.getTelefone() : "");
        TextField txtValor    = new TextField(modoEdicao ? String.valueOf(arbitro.getValorPartida()) : "");

        txtNome.setPromptText("Ex: João Silva");
        txtCpf.setPromptText("Ex: 123.456.789-00");
        txtTelefone.setPromptText("Ex: (41) 99999-9999");
        txtValor.setPromptText("Ex: 150.00");

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        btnSalvar.setOnAction(e -> {
            String nome     = txtNome.getText().trim();
            String cpf      = txtCpf.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String valorTxt = txtValor.getText().trim();

            if (nome.isEmpty()) {
                lblErro.setText("Nome é obrigatório.");
                return;
            }
            if (cpf.isEmpty()) {
                lblErro.setText("CPF é obrigatório.");
                return;
            }
            if (!Validador.isNumeroValido(valorTxt)) {
                lblErro.setText("Valor por partida inválido.");
                return;
            }

            double valor = Validador.parseDouble(valorTxt);

            if (!modoEdicao) {
                lista.add(new Arbitro(nome, cpf, telefone, valor));
            } else {
                arbitro.setNome(nome);
                arbitro.setCpf(cpf);
                arbitro.setTelefone(telefone);
                arbitro.setValorPartida(valor);
            }

            Arquivo.salvar(lista, "arbitros.dat");
            mostrarInfo("Árbitro salvo com sucesso!");
            stage.setScene(getSceneLista(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getSceneLista(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Nome:"),     0, 0); grid.add(txtNome,     1, 0);
        grid.add(new Label("CPF:"),      0, 1); grid.add(txtCpf,      1, 1);
        grid.add(new Label("Telefone:"), 0, 2); grid.add(txtTelefone, 1, 2);
        grid.add(new Label("Valor/Partida:"), 0, 3); grid.add(txtValor, 1, 3);
        grid.add(lblErro,                1, 4);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, new Separator(), grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 460, 360);
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}