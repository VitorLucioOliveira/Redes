package model;
import java.util.List;

public class Enquete {
    private String titulo;
    private List<Candidato> candidatos;
    private String tempoAbertura;
    private String tempoDuracao;
    private boolean status; // false for open, true for closed


    public Enquete(String titulo, List<Candidato> candidatos, String tempoAbertura, String tempoDuracao, boolean status) {
        this.titulo = titulo;
        this.candidatos = candidatos;
        this.tempoAbertura = tempoAbertura;
        this.tempoDuracao = tempoDuracao;
        this.status =  status; // Initially closed
    }
    public String getTitulo() {
        return titulo;
    }
    public List<Candidato> getCandidatos() {
        return candidatos;
    }
    public String getTempoAbertura() {
        return tempoAbertura;
    }
    public String getTempoDuracao() {
        return tempoDuracao;
    }
    public boolean isStatus() {
        return status;
    }
    public void abrirEnquete() {
        this.status = false; // Set to open
    }
    public void fecharEnquete() {
        this.status = true; // Set to closed
    }
    public void adicionarCandidato(Candidato candidato) {
        this.candidatos.add(candidato);
    }
    public void removerCandidato(Candidato candidato) {
        this.candidatos.remove(candidato);
    }
  
}
