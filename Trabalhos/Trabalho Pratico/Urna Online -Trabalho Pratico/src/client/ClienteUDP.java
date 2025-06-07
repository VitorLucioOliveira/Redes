package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Candidato;
import model.Enquete;

public class ClienteUDP {
    private static final int PORTA_SERVIDOR = 9872; // Porta do servidor
    private static final int PORTA_CLIENTE = 9873; // Porta do cliente
    private static boolean rodando = true;
    private static List<Enquete> ultimasEnquetes = new ArrayList<>();
    private static DatagramSocket socket;
    private static Thread threadRecebimento;

    public static void iniciarRecebimento(AtualizacaoListener listener) {
        try {
            // Para o recebimento anterior se estiver rodando
            parar();

            // Cria um novo socket
            socket = new DatagramSocket(PORTA_CLIENTE);
            socket.setBroadcast(true); // Habilita broadcast
            System.out.println("Cliente UDP iniciado na porta " + PORTA_CLIENTE);

            rodando = true;
            threadRecebimento = new Thread(() -> {
                try {
                    byte[] buffer = new byte[4096];
                    DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);

                    while (rodando && !socket.isClosed()) {
                        socket.receive(pacote);
                        String dados = new String(pacote.getData(), 0, pacote.getLength());
                        System.out.println("Pacote UDP recebido de: " + pacote.getAddress().getHostAddress());

                        // Processa os dados recebidos
                        String[] linhas = dados.split("\n");
                        if (linhas.length >= 1) {
                            try {
                                int numEnquetes = Integer.parseInt(linhas[0]);
                                List<Enquete> enquetesAtualizadas = new ArrayList<>();
                                int linhaAtual = 1;

                                // Processa cada enquete
                                for (int e = 0; e < numEnquetes; e++) {
                                    String id = linhas[linhaAtual++];
                                    String titulo = linhas[linhaAtual++];
                                    boolean status = Boolean.parseBoolean(linhas[linhaAtual++]);
                                    String tempoAberturaStr = linhas[linhaAtual++];
                                    LocalDateTime tempoAbertura = LocalDateTime.parse(tempoAberturaStr);
                                    String tempoDuracao = linhas[linhaAtual++];

                                    List<Candidato> candidatos = new ArrayList<>();
                                    while (!linhas[linhaAtual].equals("FIM_ENQUETE")) {
                                        String[] partes = linhas[linhaAtual].split(":");
                                        if (partes.length == 2) {
                                            String nome = partes[0];
                                            int votos = Integer.parseInt(partes[1]);
                                            Candidato candidato = new Candidato(nome);
                                            for (int j = 0; j < votos; j++) {
                                                candidato.incrementarVoto();
                                            }
                                            candidatos.add(candidato);
                                        }
                                        linhaAtual++;
                                    }
                                    linhaAtual++; // Pula a linha "FIM_ENQUETE"

                                    Enquete enquete = new Enquete(titulo, candidatos, tempoAbertura, tempoDuracao,
                                            status);
                                    enquete.setId(id);
                                    enquetesAtualizadas.add(enquete);
                                    System.out.println("Processada enquete ID: " + id);
                                }

                                ultimasEnquetes = enquetesAtualizadas;
                                System.out
                                        .println("Enquetes atualizadas via UDP - Total: " + enquetesAtualizadas.size());

                                // Notifica o listener sobre a atualização
                                if (listener != null) {
                                    listener.onAtualizacao(enquetesAtualizadas);
                                }
                            } catch (Exception e) {
                                System.err.println("Erro ao processar dados UDP: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        System.err.println("Erro no cliente UDP: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            threadRecebimento.start();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar cliente UDP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void parar() {
        rodando = false;
        if (threadRecebimento != null && threadRecebimento.isAlive()) {
            threadRecebimento.interrupt();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Cliente UDP parado");
    }

    public static List<Enquete> getUltimasEnquetes() {
        return ultimasEnquetes;
    }

    // Interface para notificar atualizações
    public interface AtualizacaoListener {
        void onAtualizacao(List<Enquete> enquetes);
    }

}