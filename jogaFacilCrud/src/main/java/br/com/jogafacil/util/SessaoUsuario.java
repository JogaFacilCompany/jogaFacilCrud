package br.com.jogafacil.util;

import br.com.jogafacil.model.Usuario;

/**
 * Singleton que guarda o estado do usuário logado.
 *
 * Como usar em qualquer tela:
 *   SessaoUsuario sessao = SessaoUsuario.getInstancia();
 *   if (sessao.isLocador()) { ... }
 *   String email = sessao.getEmailUsuario();
 */
public class SessaoUsuario {

    private static SessaoUsuario instancia;

    private Usuario usuarioLogado;

    private SessaoUsuario() {}

    public static SessaoUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SessaoUsuario();
        }
        return instancia;
    }


    public void login(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    public boolean estaLogado() { return usuarioLogado != null; }
    public boolean isLocador()   { return "LOCADOR".equals(usuarioLogado.getTipo()); }
    public boolean isLocatario() { return "LOCATARIO".equals(usuarioLogado.getTipo()); }
    public boolean isGerente()   { return "GERENTE".equals(usuarioLogado.getTipo()); }



    public Usuario getUsuarioLogado() { return usuarioLogado; }

    public String getEmailUsuario() { return estaLogado() ? usuarioLogado.getEmail() : null; }
    public String getNomeUsuario() { return estaLogado() ? usuarioLogado.getNome() : null; }
    public String getTipoUsuario() { return estaLogado() ? usuarioLogado.getTipo() : null; }
}