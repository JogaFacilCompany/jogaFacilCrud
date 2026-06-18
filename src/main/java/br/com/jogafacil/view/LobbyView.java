package br.com.jogafacil.view;

import br.com.jogafacil.model.Locatario;
import br.com.jogafacil.model.Lobby;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class LobbyView {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //tela de lista
    public Scene getSceneLista(Stage stage) {
        Label titulo = new Label("Lobbies");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //Tabela — todo locatário vê todos os lobbies (próprios e de outros, para poder entrar)
        TableView<Lobby> tabela = new TableView<>();

        TableColumn<Lobby, String> colReserva = new TableColumn<>("Reserva");
        colReserva.setCellValueFactory(c -> new SimpleStringProperty(descreverReserva(c.getValue().getReserva())));

        TableColumn<Lobby, String> colArbitro = new TableColumn<>("Precisa de árbitro?");
        colArbitro.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isPrecisaArbitro() ? "Sim" : "Não"));

        TableColumn<Lobby, Integer> colFalta = new TableColumn<>("Jogadores em falta");
        colFalta.setCellValueFactory(new PropertyValueFactory<>("jogadoresEmFalta"));

        tabela.getColumns().addAll(colReserva, colArbitro, colFalta);

        // Carrega os dados do disco e enche a tabela
        ArrayList<Lobby> lista = Arquivo.carregar("lobbies.dat");
        tabela.getItems().addAll(lista);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        //Botões
        Button btnNovo    = new Button("Novo Lobby");
        Button btnEditar  = new Button("Editar");
        Button btnExcluir = new Button("Excluir");
        Button btnEntrar  = new Button("Entrar");
        Button btnVoltar  = new Button("Voltar");

        btnNovo.setOnAction(e -> stage.setScene(getSceneFormulario(stage, null, lista)));

        btnEditar.setOnAction(e -> {
            Lobby selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um lobby para editar.");
                return;
            }
            if (!ehDono(selecionado)) {
                lblErro.setText("Você só pode editar os lobbies que você criou.");
                return;
            }
            stage.setScene(getSceneFormulario(stage, selecionado, lista));
        });

        btnExcluir.setOnAction(e -> {
            Lobby selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um lobby para excluir.");
                return;
            }
            if (!ehDono(selecionado)) {
                lblErro.setText("Você só pode excluir os lobbies que você criou.");
                return;
            }

            Alert confirma = new Alert(Alert.AlertType.CONFIRMATION,
                    "Excluir este lobby? Esta ação não pode ser desfeita.",
                    ButtonType.YES, ButtonType.NO);
            confirma.setTitle("Confirmar exclusão");
            confirma.setHeaderText(null);
            confirma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    lista.remove(selecionado);
                    Arquivo.salvar(lista, "lobbies.dat");
                    stage.setScene(getSceneLista(stage));
                }
            });
        });

        // "Entrar" simboliza o jogador (ou árbitro) preenchendo uma vaga em falta no lobby
        btnEntrar.setOnAction(e -> {
            Lobby selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                lblErro.setText("Selecione um lobby para entrar.");
                return;
            }
            if (selecionado.getJogadoresEmFalta() <= 0) {
                lblErro.setText("Este lobby já está completo.");
                return;
            }
            selecionado.setJogadoresEmFalta(selecionado.getJogadoresEmFalta() - 1);
            Arquivo.salvar(lista, "lobbies.dat");
            mostrarInfo("Você entrou no lobby!");
            stage.setScene(getSceneLista(stage));
        });

        btnVoltar.setOnAction(e -> stage.setScene(new LocatarioHomeView().getScene(stage)));

        HBox botoes = new HBox(10, btnNovo, btnEditar, btnExcluir, btnEntrar, btnVoltar);

        VBox layout = new VBox(15, titulo, new Separator(), tabela, lblErro, botoes);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 720, 460);
    }

    //TELA DE FORMULÁRIO (criar/editar)
    public Scene getSceneFormulario(Stage stage, Lobby lobby, ArrayList<Lobby> lista) {
        boolean modoEdicao = (lobby != null);
        SessaoUsuario sessao = SessaoUsuario.getInstancia();

        Label titulo = new Label(modoEdicao ? "Editar Lobby" : "Novo Lobby");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // ComboBox de Reserva: só as reservas do próprio locatário podem virar um lobby
        ComboBox<Reserva> cmbReserva = new ComboBox<>();
        ArrayList<Reserva> todasReservas = Arquivo.carregar("reservas.dat");
        Locatario logado = (Locatario) sessao.getUsuarioLogado();
        ArrayList<Reserva> reservasDoLocatario = new ArrayList<>();
        for (Reserva r : todasReservas) {
            if (r.getLocatario() != null && Objects.equals(r.getLocatario().getEmail(), logado.getEmail())) {
                reservasDoLocatario.add(r);
            }
        }
        cmbReserva.getItems().addAll(reservasDoLocatario);
        cmbReserva.setPromptText("Selecione a reserva");
        cmbReserva.setConverter(new StringConverter<Reserva>() {
            @Override public String toString(Reserva r) { return r == null ? null : descreverReserva(r); }
            @Override public Reserva fromString(String s) { return null; }
        });
        if (modoEdicao) {
            cmbReserva.setValue(lobby.getReserva());
        }

        CheckBox chkArbitro = new CheckBox("Precisa de árbitro");
        TextField txtJogadoresFalta = new TextField(modoEdicao ? String.valueOf(lobby.getJogadoresEmFalta()) : "");
        txtJogadoresFalta.setPromptText("Ex: 2");

        if (modoEdicao) {
            chkArbitro.setSelected(lobby.isPrecisaArbitro());
        }

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button btnSalvar   = new Button("Salvar");
        Button btnCancelar = new Button("Cancelar");

        if (!modoEdicao && reservasDoLocatario.isEmpty()) {
            lblErro.setText("Cadastre uma reserva antes de criar um lobby.");
            btnSalvar.setDisable(true);
        }

        btnSalvar.setOnAction(e -> {
            Reserva reserva = cmbReserva.getValue();
            boolean precisaArbitro = chkArbitro.isSelected();
            String faltaTxt = txtJogadoresFalta.getText().trim();

            if (reserva == null) {
                lblErro.setText("Selecione uma reserva.");
                return;
            }
            if (faltaTxt.isEmpty() || !faltaTxt.matches("\\d+")) {
                lblErro.setText("Jogadores em falta deve ser um número inteiro (0 ou mais).");
                return;
            }

            int jogadoresEmFalta = Integer.parseInt(faltaTxt);

            if (!modoEdicao) {
                lista.add(new Lobby(reserva, precisaArbitro, jogadoresEmFalta));
            } else {
                lobby.setReserva(reserva);
                lobby.setPrecisaArbitro(precisaArbitro);
                lobby.setJogadoresEmFalta(jogadoresEmFalta);
            }

            Arquivo.salvar(lista, "lobbies.dat");
            mostrarInfo("Lobby salvo com sucesso!");
            stage.setScene(getSceneLista(stage));
        });

        btnCancelar.setOnAction(e -> stage.setScene(getSceneLista(stage)));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Reserva:"),            0, 0); grid.add(cmbReserva,          1, 0);
        grid.add(chkArbitro,                        0, 1);
        grid.add(new Label("Jogadores em falta:"), 0, 2); grid.add(txtJogadoresFalta,   1, 2);
        grid.add(lblErro,                           1, 3);

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox layout = new VBox(15, titulo, new Separator(), grid, botoes);
        layout.setPadding(new Insets(25));

        return new Scene(layout, 500, 380);
    }

    // Só o locatário que criou o lobby (dono da reserva associada) pode editar/excluir.
    private boolean ehDono(Lobby lobby) {
        SessaoUsuario sessao = SessaoUsuario.getInstancia();
        if (!sessao.isLocatario()) return false;
        if (lobby.getReserva() == null || lobby.getReserva().getLocatario() == null) return false;
        return Objects.equals(lobby.getReserva().getLocatario().getEmail(), sessao.getEmailUsuario());
    }

    // Texto amigável para exibir a reserva em tabelas e combos (Reserva já tem toString(), mas
    // aqui detalhamos um pouco mais para facilitar a escolha no formulário).
    private String descreverReserva(Reserva r) {
        if (r == null) return "(sem reserva)";
        String endereco = (r.getQuadra() != null) ? r.getQuadra().getEndereco() : "sem quadra";
        return endereco + " - " + r.getData().format(FMT_DATA) + " " + r.getHorario();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}