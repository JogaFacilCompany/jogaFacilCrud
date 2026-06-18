package br.com.jogafacil.view;

import br.com.jogafacil.model.Gerente;
import br.com.jogafacil.model.Usuario;
import br.com.jogafacil.model.Locatario;
import br.com.jogafacil.model.Quadra;
import br.com.jogafacil.model.Reserva;
import br.com.jogafacil.util.Arquivo;
import br.com.jogafacil.util.SessaoUsuario;
import br.com.jogafacil.util.Validador;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class ReservaView {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //tela de lista
    public Scene getSceneLista(Stage stage) {
        SessaoUsuario sessao = SessaoUsuario.getInstancia();
        boolean isGerente = sessao.isGerente();

        Label titulo = new Label(isGerente ? "Reservas da minha quadra" : "Minhas Reservas");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //Tabela
        TableView<Reserva> tabela = new TableView<>();

        TableColumn<Reserva, String> colLocatario = new TableColumn<>("Locatário");
        colLocatario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLocatario() != null ? c.getValue().getLocatario().getNome() : ""));

        TableColumn<Reserva, LocalDate> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colData.setCellFactory(col -> new TableCell<Reserva, LocalDate>() {
            @Override protected void updateItem(LocalDate data, boolean empty) {
                super.updateItem(data, empty);
                setText((empty || data == null) ? null : data.format(FMT_DATA));
            }
        });

        TableColumn<Reserva, String> colHorario = new TableColumn<>("Horário");
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));

        TableColumn<Reserva, String> colQuadra = new TableColumn<>("Quadra");
        colQuadra.setCellValueFactory(c -> new SimpleStringProperty(descreverQuadra(c.getValue().getQuadra())));

        TableColumn<Reserva, String> colExtra = new TableColumn<>("Extra");
        colExtra.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isExtra() ? c.getValue().getExtraDescricao() : "Não"));

        tabela.getColumns().addAll(colLocatario, colData, colHorario, colQuadra, colExtra);

        // Carrega os dados do disco e filtra de acordo com o perfil logado
        ArrayList<Reserva> todasReservas = Arquivo.carregar("reservas.dat");
        ArrayList<Reserva> lista = new ArrayList<>();
        if (isGerente) {
            Gerente gerente = (Gerente) sessao.getUsuarioLogado();
            lista = filtrarPorQuadraDoGerente(todasReservas, gerente);
        } else if (sessao.isLocatario()) {
            Usuario usuario = sessao.getUsuarioLogado();

            for(Reserva reserva : todasReservas) {
                if (reserva.getLocatario().getNome().equals(usuario.getNome())) {
                    lista.add(reserva);
                }
            }

        } else {
            lista = todasReservas;
        }
        tabela.getItems().addAll(lista);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        //Botões
        Button btnNova    = new Button("Nova Reserva");
        Button btnEditar  = new Button("Editar");
        Button btnExcluir = new Button("Excluir");
        Button btnVoltar  = new Button("Voltar");

        // Gerente só vê/edita/exclui as reservas das quadras delegadas a ele; não cria reservas.
        btnNova.setVisible(!isGerente);
        btnNova.setManaged(!isGerente);

        btnNova.setOnAction(e -> stage.setScene(getSceneFormulario(stage, null, todasReservas)));

        btnEditar.setOnAction(e -> {
            Reserva selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                lblErro.setText("Selecione uma reserva para editar.");
                return;
            }
            stage.setScene(getSceneFormulario(stage, selecionada, todasReservas));
        });

        btnExcluir.setOnAction(e -> {
            Reserva selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                lblErro.setText("Selecione uma reserva para excluir.");
                return;
            }

            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION,
                    "Excluir esta reserva? Esta ação não pode ser desfeita.",
                    ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    todasReservas.remove(selecionada);
                    Arquivo.salvar(todasReservas, "reservas.dat");
                    stage.setScene(getSceneLista(stage));
                }
            });
        });

        btnVoltar.setOnAction(e -> {
            if (isGerente) {
                stage.setScene(new GerenteHomeView().getScene(stage));
            } else {
                stage.setScene(new LocatarioHomeView().getScene(stage));
            }
        });

        HBox botoes = new HBox(10, btnNova, btnEditar, btnExcluir, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), tabela, lblErro, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 720, 460);
    }

    //TELA DE FORMULÁRIO (criar/editar)
    public Scene getSceneFormulario(Stage stage, Reserva reserva, ArrayList<Reserva> lista) {
        boolean modoEdicao = (reserva != null);
        SessaoUsuario sessao = SessaoUsuario.getInstancia();

        Label titulo = new Label(modoEdicao ? "Editar Reserva" : "Nova Reserva");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // O locatário da reserva é sempre o usuário logado (não cadastra reserva para outra pessoa)
        Locatario locatarioLogado = modoEdicao ? reserva.getLocatario() : (Locatario) sessao.getUsuarioLogado();

        TextField txtData    = new TextField(modoEdicao ? reserva.getData().format(FMT_DATA) : "");
        TextField txtHorario = new TextField(modoEdicao ? reserva.getHorario() : "");

        txtData.setPromptText("Ex: 25/12/2026");
        txtHorario.setPromptText("Ex: 19:00");

        // ComboBox de Quadra: carrega as quadras cadastradas (Aluno 3)
        ComboBox<Quadra> cmbQuadra = new ComboBox<>();
        ArrayList<Quadra> quadras = Arquivo.carregar("quadras.dat");
        cmbQuadra.getItems().addAll(quadras);
        cmbQuadra.setPromptText("Selecione a quadra");
        cmbQuadra.setConverter(new StringConverter<Quadra>() {
            @Override public String toString(Quadra q) { return q == null ? null : descreverQuadra(q); }
            @Override public Quadra fromString(String s) { return null; }
        });
        if (modoEdicao) {
            cmbQuadra.setValue(reserva.getQuadra());
        }

        CheckBox chkExtra = new CheckBox("Possui item/serviço extra");
        TextField txtExtraDescricao = new TextField(modoEdicao ? reserva.getExtraDescricao() : "");
        txtExtraDescricao.setPromptText("Ex: Aluguel de bolas");
        txtExtraDescricao.setDisable(true);

        if (modoEdicao) {
            chkExtra.setSelected(reserva.isExtra());
            txtExtraDescricao.setDisable(!reserva.isExtra());
        }
        chkExtra.selectedProperty().addListener((obs, antigo, novo) -> txtExtraDescricao.setDisable(!novo));

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        if (!modoEdicao && quadras.isEmpty()) {
            lblErro.setText("Cadastre uma quadra antes de criar uma reserva.");
            btnSalvar.setDisable(true);
        }

        btnSalvar.setOnAction(e -> {
            String dataTxt    = txtData.getText().trim();
            String horario    = txtHorario.getText().trim();
            Quadra quadra      = cmbQuadra.getValue();
            boolean extra      = chkExtra.isSelected();
            String extraDescricao = txtExtraDescricao.getText().trim();

            if (!Validador.isDataValida(dataTxt)) {
                lblErro.setText("Data inválida. Use o formato dd/MM/yyyy.");
                return;
            }
            if (horario.isEmpty()) {
                lblErro.setText("Horário é obrigatório.");
                return;
            }
            if (quadra == null) {
                lblErro.setText("Selecione uma quadra.");
                return;
            }
            if (extra && extraDescricao.isEmpty()) {
                lblErro.setText("Descreva o item/serviço extra.");
                return;
            }

            LocalDate data = Validador.parseData(dataTxt);

            if (!modoEdicao) {
                lista.add(new Reserva(locatarioLogado, data, horario, quadra, extra,
                        extra ? extraDescricao : ""));
            } else {
                reserva.setLocatario(locatarioLogado);
                reserva.setData(data);
                reserva.setHorario(horario);
                reserva.setQuadra(quadra);
                reserva.setExtra(extra);
                reserva.setExtraDescricao(extra ? extraDescricao : "");
            }

            Arquivo.salvar(lista, "reservas.dat");
            mostrarInfo("Reserva salva com sucesso!");
            stage.setScene(getSceneLista(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getSceneLista(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Data:"),     0, 0); grid.add(txtData,            1, 0);
        grid.add(new Label("Horário:"),  0, 1); grid.add(txtHorario,         1, 1);
        grid.add(new Label("Quadra:"),   0, 2); grid.add(cmbQuadra,          1, 2);
        grid.add(chkExtra,                0, 3); grid.add(txtExtraDescricao, 1, 3);
        grid.add(lblErro,                 1, 4);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, new Separator(), grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 500, 420);
    }

    // O gerente só vê reservas feitas nas quadras delegadas a ele. Como a Quadra é serializada
    // por valor (sem id), comparamos pelo CNPJ — identificador natural e único da quadra.
    private ArrayList<Reserva> filtrarPorQuadraDoGerente(ArrayList<Reserva> todas, Gerente gerente) {
        ArrayList<Reserva> filtradas = new ArrayList<>();
        if (gerente.getQuadra() == null) return filtradas;
        String cnpjDelegado = gerente.getQuadra().getCnpj();
        for (Reserva r : todas) {
            if (r.getQuadra() != null && Objects.equals(r.getQuadra().getCnpj(), cnpjDelegado)) {
                filtradas.add(r);
            }
        }
        return filtradas;
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