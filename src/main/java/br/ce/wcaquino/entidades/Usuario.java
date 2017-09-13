package br.ce.wcaquino.entidades;

public class Usuario {

    private String nome;

    public Usuario() {
    }

    public Usuario(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;

        Usuario usuario = (Usuario) o;

        return getNome().equals(usuario.getNome());
    }

    @Override
    public String toString() {
        return "Usuario {" + "nome='" + nome + '\'' + '}';
    }
}