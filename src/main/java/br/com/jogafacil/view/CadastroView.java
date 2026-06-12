package br.com.jogafacil.view;

import br.com.jogafacil.model.Locador;
import br.com.jogafacil.model.Locatario;
import br.com.jogafacil.util.Arquivo;
import br.com.jogafacil.util.Validador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CadastroView {

    public Scene getScene(Stage stage) {
        Label titulo    = new Label("Criar Conta");
        Label subtitulo = new Label("Escolha o tipo de conta");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        subtitulo.setStyle("-fx-font-size: 13px;");


        ToggleGroup grupo = new ToggleGroup();
        RadioButton rbLocador   = new RadioButton("Locador   (dono de quadras)");
        RadioButton rbLocatario = new RadioButton("Locatário (aluga quadras)");
        RadioButton rbArbitro   = new RadioButton("Árbitro   (apita partidas)");
        rbLocador.setToggleGroup(grupo);
        rbLocatario.setToggleGroup(grupo);
        rbArbitro.setToggleGroup(grupo);
        rbLocador.setSelected(true); // padrão


        TextField     txtNome  = new TextField();
        TextField     txtEmail = new TextField();
        PasswordField txtSenha = new PasswordField();
        txtNome.setPromptText("Nome completo");
        txtEmail.setPromptText("seu@email.com");
        txtSenha.setPromptText("Mínimo 4 caracteres");
        txtNome.setMaxWidth(280);
        txtEmail.setMaxWidth(280);
        txtSenha.setMaxWidth(280);


        // Label lblModalidade = new Label("Modalidade:");
        // ComboBox<Modalidade> cmbModalidade = new ComboBox<>();
        // cmbModalidade.getItems().addAll(Arquivo.carregar("modalidades.dat"));
        // cmbModalidade.setMaxWidth(280);
        //
        // Label lblValor = new Label("Valor por hora (R$):");
        // TextField txtValor = new TextField();
        // txtValor.setPromptText("Ex: 80.00");
        // txtValor.setMaxWidth(280);
        //
        // VBox camposArbitro = new VBox(8, lblModalidade, cmbModalidade, lblValor, txtValor);
        // camposArbitro.setVisible(false);
        //
        // grupo.selectedToggleProperty().addListener((obs, ant, novo) -> {
        //     camposArbitro.setVisible(novo == rbArbitro);
        // });

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button btnCadastrar = new Button("Cadastrar");
        Button btnVoltar    = new Button("Voltar ao Login");
        btnCadastrar.setMaxWidth(280);
        btnVoltar.setMaxWidth(280);


        btnCadastrar.setOnAction(e -> {

            String erros = Validador.validarPessoa(
                    txtNome.getText(), txtEmail.getText(), txtSenha.getText()
            );
            if (!erros.isEmpty()) { lblErro.setText(erros); return; }

            String email = txtEmail.getText().trim();
            String nome  = txtNome.getText().trim();
            String senha = txtSenha.getText();


            if (emailJaCadastrado(email)) {
                lblErro.setText("Este email já está em uso.");
                return;
            }

            if (rbLocador.isSelected()) {
                ArrayList<Locador> lista = Arquivo.carregar("locadores.dat");
                lista.add(new Locador(nome, email, senha));
                Arquivo.salvar(lista, "locadores.dat");

            } else if (rbLocatario.isSelected()) {
                ArrayList<Locatario> lista = Arquivo.carregar("locatarios.dat");
                lista.add(new Locatario(nome, email, senha));
                Arquivo.salvar(lista, "locatarios.dat");

            } else if (rbArbitro.isSelected()) {
                // List<Arbitro> lista = Arquivo.carregar("arbitros.dat");
                // lista.add(new Arbitro(nome, email, senha,
                //     cmbModalidade.getValue(), Validador.parseDouble(txtValor.getText())));
                // Arquivo.salvar(lista, "arbitros.dat");
                lblErro.setText("Cadastro de Árbitro disponível em breve.");
                return;
            }

            mostrarInfo("Conta criada com sucesso! Faça login para continuar.");
            stage.setScene(new LoginView().getScene(stage));
        });

        btnVoltar.setOnAction(e ->
                stage.setScene(new LoginView().getScene(stage))
        );

        VBox tipoBox = new VBox(8, rbLocador, rbLocatario, rbArbitro);
        tipoBox.setPadding(new Insets(0, 0, 10, 0));

        VBox form = new VBox(8,
                new Label("Tipo de conta:"),
                tipoBox,
                new Separator(),
                new Label("Nome:"),   txtNome,
                new Label("Email:"),  txtEmail,
                new Label("Senha:"),  txtSenha,
                // camposArbitro,
                lblErro,
                btnCadastrar,
                btnVoltar
        );
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(280);

        VBox layout = new VBox(15, titulo, subtitulo, form);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        return new Scene(layout, 420, 560);
    }


    private boolean emailJaCadastrado(String email) {
        List<Locador> locadores = Arquivo.carregar("locadores.dat");
        for (Locador u : locadores)
            if (u.getEmail().equalsIgnoreCase(email)) return true;

        List<Locatario> locatarios = Arquivo.carregar("locatarios.dat");
        for (Locatario u : locatarios)
            if (u.getEmail().equalsIgnoreCase(email)) return true;

        // Árbitro
        // List<Arbitro> arbitros = Arquivo.carregar("arbitros.dat");
        // for (Arbitro u : arbitros)
        //     if (u.getEmail().equalsIgnoreCase(email)) return true;

        return false;
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}