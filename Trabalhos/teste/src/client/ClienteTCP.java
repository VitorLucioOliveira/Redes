package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Candidato;
import model.Enquete;

public class ClienteTCP {

    private static final String IP_SERVIDOR = "localhost"; // ou o IP da máquina com o servidor
    private static final int PORTA_SERVIDOR = 9871;

    public static String enviarVoto(String nomeCandidato) {
        try (Socket socket = new Socket(IP_SERVIDOR, PORTA_SERVIDOR)) {
            // Envia o comando VOTO
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            saida.writeBytes("VOTO:" + nomeCandidato + "\n");

            // Recebe a resposta do servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String resposta = entrada.readLine();

            return "Voto computado com sucesso! Servidor respondeu: " + resposta;

        } catch (Exception e) {
            return "Erro ao votar: " + e.getMessage();
        }
    }

    public static Enquete obterInformacoesEnquete() {
        try (Socket socket = new Socket(IP_SERVIDOR, PORTA_SERVIDOR)) {
            // Envia o comando INFO
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            saida.writeBytes("INFO\n");

            // Recebe as informações do servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Lê o título
            String titulo = entrada.readLine();

            // Lê o status
            boolean status = Boolean.parseBoolean(entrada.readLine());

            // Lê os tempos
            LocalDateTime tempoAbertura = LocalDateTime.parse(entrada.readLine());
            String tempoDuracao = entrada.readLine();

            // Lê a lista de candidatos e seus votos
            List<Candidato> candidatos = new ArrayList<>();
            String linha;
            while ((linha = entrada.readLine()) != null && !linha.isEmpty()) {
                String[] partes = linha.split(":");
                if (partes.length == 2) {
                    String nome = partes[0];
                    int votos = Integer.parseInt(partes[1]);
                    Candidato candidato = new Candidato(nome);
                    for (int i = 0; i < votos; i++) {
                        candidato.incrementarVoto();
                    }
                    candidatos.add(candidato);
                }
            }

            return new Enquete(titulo, candidatos, tempoAbertura, tempoDuracao, status);

        } catch (Exception e) {
            System.err.println("Erro ao obter informações da enquete: " + e.getMessage());
            return null;
        }
    }
}
