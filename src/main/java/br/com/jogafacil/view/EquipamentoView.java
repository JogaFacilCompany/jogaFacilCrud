package br.com.jogafacil.view;

import br.com.jogafacil.model.Equipamento;
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

public class EquipamentoView {

    public Scene getSceneLista(Stage stage) {
        Label titulo = new Label("Equipamentos");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Equipamento> tabela = new TableView<>();

        TableColumn<Equipamento, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Equipamento, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        TableColumn<Equipamento, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        tabela.getColumns().addAll(colNome, colValor, colDescricao);

        ArrayList<Equipamento> lista = Arquivo.carregar("equipamentos.dat");
        tabela.getItems().addAll(lista);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnNovo    = new Button("Novo Equipamento");
        Button btnEditar  = new Button("Editar");
        Button btnExcluir = new Button("Excluir");
        Button btnVoltar  = new Button("Voltar");

        btnNovo.setOnAction(e -> stage.setScene(getSceneFormulario(stage, null, lista)));

        btnEditar.setOnAction(e -> {
            Equipamento selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um equipamento para editar.");
                return;
            }
            stage.setScene(getSceneFormulario(stage, selecionado, lista));
        });

        btnExcluir.setOnAction(e -> {
            Equipamento selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um equipamento para excluir.");
                return;
            }

            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION, "Excluir este equipamento? Esta ação não pode ser desfeita.", ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    lista.remove(selecionado);
                    Arquivo.salvar(lista, "equipamentos.dat");
                    stage.setScene(getSceneLista(stage));
                }
            });
        });

        // TODO: trocar por new GerenteHomeView().getScene(stage) quando o Aluno 4 implementar
        btnVoltar.setOnAction(e -> stage.setScene(new LoginView().getScene(stage)));

        HBox botoes = new HBox(10, btnNovo, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), tabela, lblErro, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 600, 420);
    }

    public Scene getSceneFormulario(Stage stage, Equipamento equipamento, ArrayList<Equipamento> lista) {
        boolean modoEdicao = (equipamento != null);

        Label titulo = new Label(modoEdicao ? "Editar Equipamento" : "Novo Equipamento");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField txtNome = new TextField(modoEdicao ? equipamento.getNome() : "");
        TextField txtValor = new TextField(modoEdicao ? String.format("%.2f", equipamento.getValor()) : "");
        TextField txtDescricao = new TextField(modoEdicao ? equipamento.getDescricao() : "");

        txtNome.setPromptText("Ex: Bola de futsal");
        txtValor.setPromptText("Ex: 150,00");
        txtDescricao.setPromptText("Ex: Bola oficial tamanho 4");

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        btnSalvar.setOnAction(e -> {
            String nome = txtNome.getText().trim();
            String valorTexto = txtValor.getText().trim();
            String descricao = txtDescricao.getText().trim();

            if (nome.isEmpty()) {
                lblErro.setText("Nome é obrigatório.");
                return;
            }

            if (descricao.isEmpty()) {
                lblErro.setText("Descrição é obrigatória.");
                return;
            }

            if (!Validador.isNumeroValido(valorTexto)) {
                lblErro.setText("Valor inválido.");
                return;
            }

            double valor = Validador.parseDouble(valorTexto);

            if (!modoEdicao) {
                lista.add(new Equipamento(nome, valor, descricao));
            } else {
                equipamento.setNome(nome);
                equipamento.setValor(valor);
                equipamento.setDescricao(descricao);
            }

            Arquivo.salvar(lista, "equipamentos.dat");
            mostrarInfo("Equipamento salvo com sucesso!");
            stage.setScene(getSceneLista(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getSceneLista(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Nome:"),      0, 0); grid.add(txtNome,      1, 0);
        grid.add(new Label("Valor:"),     0, 1); grid.add(txtValor,     1, 1);
        grid.add(new Label("Descrição:"), 0, 2); grid.add(txtDescricao, 1, 2);
        grid.add(lblErro,                 1, 3);

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
