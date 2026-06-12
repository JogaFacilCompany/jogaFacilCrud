package br.com.jogafacil.view;

//import br.com.jogafacil.model.Arbitro;
//import br.com.jogafacil.model.Gerente;
import br.com.jogafacil.model.Locador;
import br.com.jogafacil.model.Locatario;
import br.com.jogafacil.util.Arquivo;
import br.com.jogafacil.util.SessaoUsuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class LoginView {

    public LoginView() {}

    public Scene getScene(Stage stage) {
        Label titulo    = new Label("Joga Fácil");
        Label subtitulo = new Label("Faça login para continuar");
        titulo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        subtitulo.setStyle("-fx-font-size: 13px;");


        TextField     txtEmail = new TextField();
        PasswordField txtSenha = new PasswordField();
        txtEmail.setPromptText("seu@email.com");
        txtSenha.setPromptText("Senha");
        txtEmail.setMaxWidth(280);
        txtSenha.setMaxWidth(280);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button btnEntrar   = new Button("Entrar");
        Button btnCadastro = new Button("Criar conta");
        btnEntrar.setMaxWidth(280);
        btnCadastro.setMaxWidth(280);


        btnEntrar.setOnAction(e -> {
            String email = txtEmail.getText().trim();
            String senha = txtSenha.getText();

            if (email.isEmpty() || senha.isEmpty()) {
                lblErro.setText("Preencha email e senha.");
                return;
            }


            ArrayList<Locador> locadores = Arquivo.carregar("locadores.dat");
            for (Locador u : locadores) {
                if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                    SessaoUsuario.getInstancia().login(u);
                    abrirMenuPrincipal(stage);
                    return;
                }
            }


            ArrayList<Locatario> locatarios = Arquivo.carregar("locatarios.dat");
            for (Locatario u : locatarios) {
                if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                    SessaoUsuario.getInstancia().login(u);
                    abrirMenuPrincipal(stage);
                    return;
                }
            }

            // List<Gerente> gerentes = Arquivo.carregar("gerentes.dat");
            // for (Gerente u : gerentes) {
            //     if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
            //         SessaoUsuario.getInstancia().login("GERENTE", u.getEmail(), u);
            //         abrirMenuPrincipal(stage);
            //         return;
            //     }
            // }

            // List<Arbitro> arbitros = Arquivo.carregar("arbitros.dat");
            // for (Arbitro u : arbitros) {
            //     if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
            //         SessaoUsuario.getInstancia().login("ARBITRO", u.getEmail(), u);
            //         abrirMenuPrincipal(stage);
            //         return;
            //     }
            // }

            lblErro.setText("Email ou senha incorretos.");
        });

        btnCadastro.setOnAction(e ->
                stage.setScene(new CadastroView().getScene(stage))
        );

        VBox form = new VBox(10,
                new Label("Email:"), txtEmail,
                new Label("Senha:"), txtSenha,
                lblErro,
                btnEntrar,
                new Separator(),
                new Label("Não tem conta?"),
                btnCadastro
        );
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(280);
        form.setPadding(new Insets(20));

        VBox layout = new VBox(15, titulo, subtitulo, form);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        return new Scene(layout, 420, 480);
    }


    private void abrirMenuPrincipal(Stage stage) {
        SessaoUsuario sessao = SessaoUsuario.getInstancia();


        if (sessao.isLocador()) {
            stage.setScene(new LocadorHomeView().getScene(stage));
        } else if (sessao.isLocatario()) {
            stage.setScene(new LocatarioHomeView().getScene(stage));
        }

    }
}
