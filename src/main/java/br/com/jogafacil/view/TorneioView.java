package br.com.jogafacil.view;

import br.com.jogafacil.model.Torneio;
import br.com.jogafacil.util.Arquivo;
import br.com.jogafacil.util.SessaoUsuario;
import br.com.jogafacil.util.Validador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TorneioView {

    //tela de lista
    public Scene getSceneLista(Stage stage) {
        Label titulo = new Label("Torneios");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //Tabela
        TableView<Torneio> tabela = new TableView<>();

        TableColumn<Torneio, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Torneio, Double> colPremiacao = new TableColumn<>("Premiação");
        colPremiacao.setCellValueFactory(new PropertyValueFactory<>("premiacao"));

        TableColumn<Torneio, LocalDate> colData = new TableColumn<>("Início");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colData.setCellFactory(col -> new TableCell<Torneio, LocalDate>() {
            @Override protected void updateItem(LocalDate data, boolean empty) {
                super.updateItem(data, empty);
                setText((empty || data == null) ? null : data.format(fmtData));
            }
        });

        TableColumn<Torneio, Double> colTaxa = new TableColumn<>("Taxa Inscrição");
        colTaxa.setCellValueFactory(new PropertyValueFactory<>("taxaInscricao"));

        TableColumn<Torneio, Integer> colTimes = new TableColumn<>("Nº de Times");
        colTimes.setCellValueFactory(new PropertyValueFactory<>("numeroTimes"));

        tabela.getColumns().addAll(colNome, colPremiacao, colData, colTaxa, colTimes);

        // Carrega os dados do disco e enche a tabela
        ArrayList<Torneio> lista = Arquivo.carregar("torneios.dat");
        tabela.getItems().addAll(lista);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        //Botões
        Button btnNovo    = new Button("Novo Torneio");
        Button btnEditar  = new Button("Editar");
        Button btnExcluir = new Button("Excluir");
        Button btnVoltar  = new Button("Voltar");


        btnNovo.setOnAction(e -> stage.setScene(getSceneFormulario(stage, null, lista)));

        btnEditar.setOnAction(e -> {
            Torneio selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um torneio para editar.");
                return;
            }
            stage.setScene(getSceneFormulario(stage, selecionado, lista));
        });

        btnExcluir.setOnAction(e -> {
            Torneio selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um torneio para excluir.");
                return;
            }

            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION,
                    "Excluir este torneio? Esta ação não pode ser desfeita.",
                    ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    lista.remove(selecionado);
                    Arquivo.salvar(lista, "torneios.dat");
                    stage.setScene(getSceneLista(stage));
                }
            });
        });

        btnVoltar.setOnAction(e -> {
            if (SessaoUsuario.getInstancia().isGerente()) {
                stage.setScene(new GerenteHomeView().getScene(stage));
            } else {
                stage.setScene(new LocadorHomeView().getScene(stage));
            }
        });

        HBox botoes = new HBox(10, btnNovo, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), tabela, lblErro, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 700, 440);
    }

    //TELA DE FORMULÁRIO (criar/editar)
    public Scene getSceneFormulario(Stage stage, Torneio torneio, ArrayList<Torneio> lista) {
        boolean modoEdicao = (torneio != null);

        Label titulo = new Label(modoEdicao ? "Editar Torneio" : "Novo Torneio");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField txtNome      = new TextField(modoEdicao ? torneio.getNome() : "");
        TextField txtPremiacao = new TextField(modoEdicao ? String.valueOf(torneio.getPremiacao()) : "");
        TextField txtData      = new TextField(modoEdicao ? torneio.getDataInicio().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        TextField txtTaxa      = new TextField(modoEdicao ? String.valueOf(torneio.getTaxaInscricao()) : "");
        TextField txtTimes     = new TextField(modoEdicao ? String.valueOf(torneio.getNumeroTimes()) : "");

        txtNome.setPromptText("Ex: Copa Joga Fácil");
        txtPremiacao.setPromptText("Ex: 1000.00");
        txtData.setPromptText("Ex: 25/12/2026");
        txtTaxa.setPromptText("Ex: 50.00");
        txtTimes.setPromptText("Ex: 8");

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        btnSalvar.setOnAction(e -> {
            String nome      = txtNome.getText().trim();
            String premioTxt = txtPremiacao.getText().trim();
            String dataTxt   = txtData.getText().trim();
            String taxaTxt   = txtTaxa.getText().trim();
            String timesTxt  = txtTimes.getText().trim();

            if (nome.isEmpty()) {
                lblErro.setText("Nome é obrigatório.");
                return;
            }
            if (!Validador.isNumeroValido(premioTxt)) {
                lblErro.setText("Premiação inválida.");
                return;
            }
            if (!Validador.isDataValida(dataTxt)) {
                lblErro.setText("Data inválida. Use o formato dd/MM/yyyy.");
                return;
            }
            if (!Validador.isNumeroValido(taxaTxt)) {
                lblErro.setText("Taxa de inscrição inválida.");
                return;
            }
            if (!Validador.isInteiroPositivo(timesTxt)) {
                lblErro.setText("Número de times deve ser um inteiro positivo.");
                return;
            }

            double premiacao    = Validador.parseDouble(premioTxt);
            LocalDate dataInicio = Validador.parseData(dataTxt);
            double taxa         = Validador.parseDouble(taxaTxt);
            int numeroTimes     = Integer.parseInt(timesTxt);

            if (premiacao < 0 || taxa < 0) {
                lblErro.setText("Premiação e taxa não podem ser negativas.");
                return;
            }

            if (!modoEdicao) {
                lista.add(new Torneio(nome, premiacao, dataInicio, taxa, numeroTimes));
            } else {
                torneio.setNome(nome);
                torneio.setPremiacao(premiacao);
                torneio.setDataInicio(dataInicio);
                torneio.setTaxaInscricao(taxa);
                torneio.setNumeroTimes(numeroTimes);
            }

            Arquivo.salvar(lista, "torneios.dat");
            mostrarInfo("Torneio salvo com sucesso!");
            stage.setScene(getSceneLista(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getSceneLista(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Nome:"),            0, 0); grid.add(txtNome,      1, 0);
        grid.add(new Label("Premiação:"),       0, 1); grid.add(txtPremiacao, 1, 1);
        grid.add(new Label("Data início:"),     0, 2); grid.add(txtData,      1, 2);
        grid.add(new Label("Taxa inscrição:"),  0, 3); grid.add(txtTaxa,      1, 3);
        grid.add(new Label("Nº de times:"),     0, 4); grid.add(txtTimes,     1, 4);
        grid.add(lblErro,                       1, 5);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, new Separator(), grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 480, 420);
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
