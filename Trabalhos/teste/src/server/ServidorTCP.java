package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import model.Candidato;
import model.Enquete;

public class ServidorTCP {

    private static final int PORTA = 9871;
    private static Enquete enquete;

    public static void main(String[] args) throws Exception {
        // Cria enquete global
        enquete = new Enquete(
                "Votação para o melhor candidato",
                Arrays.asList(
                        new Candidato("Alice"),
                        new Candidato("Bruno"),
                        new Candidato("Carla"),
                        new Candidato("Diego"),
                        new Candidato("Eduarda"),
                        new Candidato("Fernanda"),
                        new Candidato("Gustavo"),
                        new Candidato("Helena"),
                        new Candidato("Igor"),
                        new Candidato("Joana"),
                        new Candidato("Kaique"),
                        new Candidato("Larissa"),
                        new Candidato("Marcos")),
                "01/06/2024 14:00",
                "00:14:00",
                false); // Inicialmente fechada

        // Inicia o servidor UDP para broadcast de informações
        ServidorUDP.iniciar(enquete);

        ServerSocket servidor = new ServerSocket(PORTA);
        System.out.println("\nServidor TCP ouvindo na porta " + PORTA);

        while (true) {
            Socket conexao = servidor.accept();
            System.out.println("\nNova conexão recebida de: " + conexao.getInetAddress().getHostAddress());

            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());

            String comando = entrada.readLine();
            System.out.println("Comando recebido: " + comando);

            if (comando.startsWith("VOTO:")) {
                String nomeCandidato = comando.substring(5);
                boolean sucesso = false;

                for (Candidato c : enquete.getCandidatos()) {
                    if (c.getNome().equalsIgnoreCase(nomeCandidato)) {
                        c.incrementarVoto();
                        sucesso = true;
                        System.out.println("Voto registrado para: " + nomeCandidato);
                        System.out.println("Total de votos atual: " + c.getVotos());
                        break;
                    }
                }

                if (sucesso) {
                    saida.writeBytes("Voto recebido para " + nomeCandidato + "\n");
                } else {
                    System.out.println("Candidato não encontrado: " + nomeCandidato);
                    saida.writeBytes("Candidato não encontrado.\n");
                }
            } else if (comando.equals("INFO")) {
                System.out.println("\nEnviando informações da enquete:");
                System.out.println("Título: " + enquete.getTitulo());
                System.out.println("Status: " + (enquete.isStatus() ? "Aberta" : "Fechada"));
                System.out.println("Tempo de Abertura: " + enquete.getTempoAbertura());
                System.out.println("Tempo de Duração: " + enquete.getTempoDuracao());
                System.out.println("\nVotos por candidato:");

                // Envia o título
                saida.writeBytes(enquete.getTitulo() + "\n");

                // Envia o status
                saida.writeBytes(enquete.isStatus() + "\n");

                // Envia os tempos
                saida.writeBytes(enquete.getTempoAbertura() + "\n");
                saida.writeBytes(enquete.getTempoDuracao() + "\n");

                // Envia a lista de candidatos e seus votos
                for (Candidato c : enquete.getCandidatos()) {
                    String info = c.getNome() + ":" + c.getVotos();
                    System.out.println(info);
                    saida.writeBytes(info + "\n");
                }
                saida.writeBytes("\n"); // Marca o fim da lista
            }

            conexao.close();
            System.out.println("Conexão encerrada\n");
        }
    }
}
