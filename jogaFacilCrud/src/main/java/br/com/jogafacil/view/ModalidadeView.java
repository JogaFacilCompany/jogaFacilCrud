package br.com.jogafacil.view;

import br.com.jogafacil.model.Modalidade;
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

public class ModalidadeView {

    public Scene getSceneLista(Stage stage) {
        Label titulo = new Label("Modalidades");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Modalidade> tabela = new TableView<>();

        TableColumn<Modalidade, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Modalidade, Integer> colQtd = new TableColumn<>("Qtd. Jogadores");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("qtdJogadores"));

        TableColumn<Modalidade, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        tabela.getColumns().addAll(colNome, colQtd, colDescricao);

        ArrayList<Modalidade> lista = Arquivo.carregar("modalidades.dat");
        tabela.getItems().addAll(lista);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnNova    = new Button("Nova Modalidade");
        Button btnEditar  = new Button("Editar");
        Button btnExcluir = new Button("Excluir");
        Button btnVoltar  = new Button("Voltar");

        btnNova.setOnAction(e -> stage.setScene(getSceneFormulario(stage, null, lista)));

        btnEditar.setOnAction(e -> {
            Modalidade selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                lblErro.setText("Selecione uma modalidade para editar.");
                return;
            }
            stage.setScene(getSceneFormulario(stage, selecionada, lista));
        });

        btnExcluir.setOnAction(e -> {
            Modalidade selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                lblErro.setText("Selecione uma modalidade para excluir.");
                return;
            }

            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION, "Excluir esta modalidade? Esta ação não pode ser desfeita.", ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    lista.remove(selecionada);
                    Arquivo.salvar(lista, "modalidades.dat");
                    stage.setScene(getSceneLista(stage));
                }
            });
        });

        btnVoltar.setOnAction(e -> stage.setScene(new LocadorHomeView().getScene(stage)));

        HBox botoes = new HBox(10, btnNova, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), tabela, lblErro, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 600, 420);
    }

    public Scene getSceneFormulario(Stage stage, Modalidade modalidade, ArrayList<Modalidade> lista) {
        boolean modoEdicao = (modalidade != null);

        Label titulo = new Label(modoEdicao ? "Editar Modalidade" : "Nova Modalidade");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField txtNome = new TextField(modoEdicao ? modalidade.getNome() : "");
        TextField txtQtd = new TextField(modoEdicao ? String.valueOf(modalidade.getQtdJogadores()) : "");
        TextField txtDescricao = new TextField(modoEdicao ? modalidade.getDescricao() : "");

        txtNome.setPromptText("Ex: Futsal");
        txtQtd.setPromptText("Ex: 5");
        txtDescricao.setPromptText("Ex: Modalidade jogada em quadra coberta");

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        btnSalvar.setOnAction(e -> {
            String nome = txtNome.getText().trim();
            String qtdTexto = txtQtd.getText().trim();
            String descricao = txtDescricao.getText().trim();

            if (nome.isEmpty()) {
                lblErro.setText("Nome é obrigatório.");
                return;
            }

            if (descricao.isEmpty()) {
                lblErro.setText("Descrição é obrigatória.");
                return;
            }

            if (!Validador.isInteiroPositivo(qtdTexto)) {
                lblErro.setText("Quantidade de jogadores inválida.");
                return;
            }

            int qtd = Integer.parseInt(qtdTexto);

            if (!modoEdicao) {
                lista.add(new Modalidade(nome, qtd, descricao));
            } else {
                modalidade.setNome(nome);
                modalidade.setQtdJogadores(qtd);
                modalidade.setDescricao(descricao);
            }

            Arquivo.salvar(lista, "modalidades.dat");
            mostrarInfo("Modalidade salva com sucesso!");
            stage.setScene(getSceneLista(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getSceneLista(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Nome:"),       0, 0); grid.add(txtNome,       1, 0);
        grid.add(new Label("Qtd. Jogadores:"), 0, 1); grid.add(txtQtd,    1, 1);
        grid.add(new Label("Descrição:"),  0, 2); grid.add(txtDescricao,  1, 2);
        grid.add(lblErro,                  1, 3);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, new Separator(), grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 460, 320);
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
