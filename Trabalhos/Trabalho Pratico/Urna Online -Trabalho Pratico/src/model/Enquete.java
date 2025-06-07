package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class Enquete {
    private String id; // UUID como ID único da enquete
    private String titulo;
    private List<Candidato> candidatos;
    private LocalDateTime tempoAbertura;
    private Duration tempoDuracao;
    private boolean status; // true = aberta, false = fechada

    public Enquete(String titulo, List<Candidato> candidatos, LocalDateTime tempoAbertura, String tempoDuracao,
            boolean status) {
        this.id = UUID.randomUUID().toString(); // Gera um UUID único
        this.titulo = titulo;
        this.candidatos = candidatos;
        this.tempoAbertura = tempoAbertura;

        // Parse da duração (formato HH:mm:ss)
        LocalTime duracaoTime = LocalTime.parse(tempoDuracao);
        this.tempoDuracao = Duration.ofHours(duracaoTime.getHour())
                .plusMinutes(duracaoTime.getMinute())
                .plusSeconds(duracaoTime.getSecond());

        this.status = status;
    }

    public Enquete(String titulo, LocalDateTime tempoAbertura, String tempoDuracao,
            boolean status) {
        this.id = UUID.randomUUID().toString(); // Gera um UUID único
        this.titulo = titulo;
        this.candidatos = null;
        this.tempoAbertura = tempoAbertura;

        // Parse da duração (formato HH:mm:ss)
        LocalTime duracaoTime = LocalTime.parse(tempoDuracao);
        this.tempoDuracao = Duration.ofHours(duracaoTime.getHour())
                .plusMinutes(duracaoTime.getMinute())
                .plusSeconds(duracaoTime.getSecond());

        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<Candidato> getCandidatos() {
        return candidatos;
    }

    public LocalDateTime getTempoAbertura() {
        return tempoAbertura;
    }

    public String getTempoDuracao() {
        // Formatando de volta para "HH:mm:ss"
        long horas = tempoDuracao.toHours();
        long minutos = tempoDuracao.toMinutesPart();
        long segundos = tempoDuracao.toSecondsPart();
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }

    public boolean isStatus() {
        atualizarStatus(); // Garante que sempre está correto ao consultar
        return status;
    }

    public void abrirEnquete() {
        this.status = true;
    }

    public void fecharEnquete() {
        this.status = false;
    }

    public void adicionarCandidato(Candidato candidato) {
        this.candidatos.add(candidato);
    }

    public void removerCandidato(Candidato candidato) {
        this.candidatos.remove(candidato);
    }

    private void atualizarStatus() {
        if (status) {
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime fechamento = tempoAbertura.plus(tempoDuracao);

            // Se o tempo atual passou do tempo de fechamento, fecha a enquete
            if (agora.isAfter(fechamento)) {
                System.out.println("Enquete fechada automaticamente. Tempo de duração expirado.");
                System.out.println("Tempo de abertura: " + tempoAbertura);
                System.out.println("Tempo de duração: " + getTempoDuracao());
                System.out.println("Tempo de fechamento: " + fechamento);
                System.out.println("Tempo atual: " + agora);
                status = false;
            }
        }
    }
}
