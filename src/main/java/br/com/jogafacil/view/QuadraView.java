package br.com.jogafacil.view;

import br.com.jogafacil.model.Modalidade;
import br.com.jogafacil.model.Quadra;
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

public class QuadraView {

    //tela de lista
    public Scene getSceneLista(Stage stage) {
        Label titulo = new Label("Quadras");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //Tabela
        TableView<Quadra> tabela = new TableView<>();

        TableColumn<Quadra, Modalidade> colModalidade = new TableColumn<>("Modalidade");
        colModalidade.setCellValueFactory(new PropertyValueFactory<>("modalidade"));

        TableColumn<Quadra, String> colCnpj = new TableColumn<>("CNPJ");
        colCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));

        TableColumn<Quadra, String> colEndereco = new TableColumn<>("Endereço");
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        TableColumn<Quadra, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        tabela.getColumns().addAll(colModalidade, colCnpj, colEndereco, colValor);

        // Carrega os dados do disco e enche a tabela
        ArrayList<Quadra> lista = Arquivo.carregar("quadras.dat");
        tabela.getItems().addAll(lista);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        //Botões
        Button btnNova    = new Button("Nova Quadra");
        Button btnEditar  = new Button("Editar");
        Button btnExcluir = new Button("Excluir");
        Button btnVoltar  = new Button("Voltar");

        btnNova.setOnAction(e -> stage.setScene(getSceneFormulario(stage, null, lista)));

        btnEditar.setOnAction(e -> {
            Quadra selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                lblErro.setText("Selecione uma quadra para editar.");
                return;
            }
            stage.setScene(getSceneFormulario(stage, selecionada, lista));
        });

        btnExcluir.setOnAction(e -> {
            Quadra selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                lblErro.setText("Selecione uma quadra para excluir.");
                return;
            }

            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION,
                    "Excluir esta quadra? Esta ação não pode ser desfeita.",
                    ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    lista.remove(selecionada);
                    Arquivo.salvar(lista, "quadras.dat");
                    stage.setScene(getSceneLista(stage));
                }
            });
        });

        btnVoltar.setOnAction(e -> stage.setScene(new LocadorHomeView().getScene(stage)));

        HBox botoes = new HBox(10, btnNova, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), tabela, lblErro, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 640, 440);
    }

    //TELA DE FORMULÁRIO (criar/editar)
    public Scene getSceneFormulario(Stage stage, Quadra quadra, ArrayList<Quadra> lista) {
        boolean modoEdicao = (quadra != null);

        Label titulo = new Label(modoEdicao ? "Editar Quadra" : "Nova Quadra");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // ComboBox de Modalidade: carrega as modalidades cadastradas (Aluno 1)
        ComboBox<Modalidade> cmbModalidade = new ComboBox<>();
        ArrayList<Modalidade> modalidades = Arquivo.carregar("modalidades.dat");
        cmbModalidade.getItems().addAll(modalidades);
        cmbModalidade.setPromptText("Selecione a modalidade");
        if (modoEdicao) {
            cmbModalidade.setValue(quadra.getModalidade());
        }

        TextField txtCnpj     = new TextField(modoEdicao ? quadra.getCnpj() : "");
        TextField txtEndereco = new TextField(modoEdicao ? quadra.getEndereco() : "");
        TextField txtValor    = new TextField(modoEdicao ? String.valueOf(quadra.getValor()) : "");

        txtCnpj.setPromptText("Ex: 11.222.333/0001-81");
        txtEndereco.setPromptText("Ex: Rua das Quadras, 123 - Centro");
        txtValor.setPromptText("Ex: 80.00");

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        btnSalvar.setOnAction(e -> {
            Modalidade modalidade = cmbModalidade.getValue();
            String cnpj     = txtCnpj.getText().trim();
            String endereco = txtEndereco.getText().trim();
            String valorTxt = txtValor.getText().trim();

            if (modalidade == null) {
                lblErro.setText("Selecione uma modalidade.");
                return;
            }
            if (cnpj.isEmpty()) {
                lblErro.setText("CNPJ é obrigatório.");
                return;
            }
            if (endereco.isEmpty()) {
                lblErro.setText("Endereço é obrigatório.");
                return;
            }
            if (!Validador.isNumeroValido(valorTxt)) {
                lblErro.setText("Valor inválido.");
                return;
            }

            double valor = Validador.parseDouble(valorTxt);

            if (!modoEdicao) {
                lista.add(new Quadra(modalidade, cnpj, endereco, valor));
            } else {
                quadra.setModalidade(modalidade);
                quadra.setCnpj(cnpj);
                quadra.setEndereco(endereco);
                quadra.setValor(valor);
            }

            Arquivo.salvar(lista, "quadras.dat");
            mostrarInfo("Quadra salva com sucesso!");
            stage.setScene(getSceneLista(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getSceneLista(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Modalidade:"), 0, 0); grid.add(cmbModalidade, 1, 0);
        grid.add(new Label("CNPJ:"),       0, 1); grid.add(txtCnpj,       1, 1);
        grid.add(new Label("Endereço:"),   0, 2); grid.add(txtEndereco,   1, 2);
        grid.add(new Label("Valor:"),      0, 3); grid.add(txtValor,      1, 3);
        grid.add(lblErro,                  1, 4);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, new Separator(), grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 480, 380);
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}