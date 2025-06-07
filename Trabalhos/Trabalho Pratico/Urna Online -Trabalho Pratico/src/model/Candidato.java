package model;

public class Candidato {
    private final String nome;
    private int votos;

    public Candidato(String nome) {
        this.nome = nome;
        this.votos = 0;
    }

     public Candidato(String nome, int votos) {
        this.nome = nome;
        this.votos = votos;
    }


    public String getNome() {
        return nome;
    }

    public int getVotos() {
        return votos;
    }

    public void incrementarVoto() {
        votos++;
    }
}
