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

    public static String enviarVoto(String nomeCandidato, String idEnquete) {
        try (Socket socket = new Socket(IP_SERVIDOR, PORTA_SERVIDOR)) {
            System.out.println("Conectando ao servidor TCP em " + IP_SERVIDOR + ":" + PORTA_SERVIDOR);

            // Envia o comando VOTO
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            String comando = "VOTO:" + idEnquete + ":" + nomeCandidato + "\n";
            System.out.println("Enviando comando: " + comando.trim());
            saida.writeBytes(comando);

            // Recebe a resposta do servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String resposta = entrada.readLine();
            System.out.println("Resposta recebida: " + resposta);

            return "Voto computado com sucesso! Servidor respondeu: " + resposta;

        } catch (Exception e) {
            System.err.println("Erro ao votar: " + e.getMessage());
            e.printStackTrace();
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
            String tempoAberturaStr = entrada.readLine();
            System.out.println("Tempo de abertura recebido do servidor: " + tempoAberturaStr);
            LocalDateTime tempoAbertura = LocalDateTime.parse(tempoAberturaStr);
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

            Enquete enquete = new Enquete(titulo, candidatos, tempoAbertura, tempoDuracao, status);
            System.out.println("Enquete criada com tempo de abertura: " + enquete.getTempoAbertura());
            return enquete;

        } catch (Exception e) {
            System.err.println("Erro ao obter informações da enquete: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String enviarNovaEnquete(String titulo, List<Candidato> candidatos, String tempoAbertura,
            String tempoDuracao, boolean status) {
        try (Socket socket = new Socket(IP_SERVIDOR, PORTA_SERVIDOR)) {
            System.out.println("Conectando ao servidor TCP em " + IP_SERVIDOR + ":" + PORTA_SERVIDOR);

            // Envia o comando CRIAR_ENQUETE
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            String comando = "CRIAR_ENQUETE\n";
            System.out.println("Enviando comando: " + comando.trim());
            saida.writeBytes(comando);

            // Envia os dados da enquete
            System.out.println("Enviando título: " + titulo);
            saida.writeBytes(titulo + "\n");

            // Envia os candidatos
            System.out.println("Enviando " + candidatos.size() + " candidatos:");
            for (Candidato candidato : candidatos) {
                String linha = candidato.getNome() + ":0";
                System.out.println("Enviando candidato: " + linha);
                saida.writeBytes(linha + "\n");
            }

            // Envia os tempos e status
            System.out.println("Enviando tempo de abertura: " + tempoAbertura);
            saida.writeBytes(tempoAbertura + "\n");
            System.out.println("Enviando tempo de duração: " + tempoDuracao);
            saida.writeBytes(tempoDuracao + "\n");
            System.out.println("Enviando status: " + status);
            saida.writeBytes(status + "\n");

            // Recebe a resposta do servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String resposta = entrada.readLine();
            System.out.println("Resposta recebida: " + resposta);

            return "Enquete criada com sucesso! Servidor respondeu: " + resposta;

        } catch (Exception e) {
            System.err.println("Erro ao criar Enquete: " + e.getMessage());
            e.printStackTrace();
            return "Erro ao criar Enquete: " + e.getMessage();
        }
    }

    public static List<Enquete> listarEnquetes() {
        try (Socket socket = new Socket(IP_SERVIDOR, PORTA_SERVIDOR)) {
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            saida.writeBytes("LISTAR_ENQUETES\n");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            List<Enquete> enquetes = new ArrayList<>();
            String linha;

            while ((linha = entrada.readLine()) != null && !linha.equals("FIM_LISTA")) {
                String id = linha; // Primeira linha é o ID
                String titulo = entrada.readLine();
                boolean status = Boolean.parseBoolean(entrada.readLine());
                String tempoAberturaStr = entrada.readLine();
                LocalDateTime tempoAbertura = LocalDateTime.parse(tempoAberturaStr);
                String tempoDuracao = entrada.readLine();

                // Ler candidatos
                List<Candidato> candidatos = new ArrayList<>();
                String linhaCandidato;
                while ((linhaCandidato = entrada.readLine()) != null && !linhaCandidato.equals("FIM_CANDIDATOS")) {
                    String[] partes = linhaCandidato.split(":");
                    String nome = partes[0];
                    int votos = Integer.parseInt(partes[1]);
                    Candidato candidato = new Candidato(nome);
                    for (int i = 0; i < votos; i++) {
                        candidato.incrementarVoto();
                    }
                    candidatos.add(candidato);
                }

                // Criar enquete com todos os dados
                Enquete enquete = new Enquete(titulo, candidatos, tempoAbertura, tempoDuracao, status);
                enquete.setId(id); // Define o ID recebido do servidor
                enquetes.add(enquete);
            }

            return enquetes;

        } catch (Exception e) {
            System.err.println("Erro ao obter informações das enquetes: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
