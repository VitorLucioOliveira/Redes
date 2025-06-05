package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Candidato;
import model.Enquete;

public class ClienteUDP {
    private static final int PORTA_SERVIDOR = 9872; // Porta do servidor
    private static final int PORTA_CLIENTE = 9873; // Porta do cliente
    private static boolean rodando = true;
    private static Enquete ultimaEnquete;

    public static void iniciarRecebimento(AtualizacaoListener listener) {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(PORTA_CLIENTE)) {
                System.out.println("Cliente UDP iniciado na porta " + PORTA_CLIENTE);

                byte[] buffer = new byte[1024];
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);

                while (rodando) {
                    socket.receive(pacote);
                    String dados = new String(pacote.getData(), 0, pacote.getLength());
                    System.out.println(
                            "Pacote UDP recebido: " + dados.substring(0, Math.min(50, dados.length())) + "...");

                    // Processa os dados recebidos
                    String[] linhas = dados.split("\n");
                    if (linhas.length >= 4) {
                        String titulo = linhas[0];
                        boolean status = Boolean.parseBoolean(linhas[1]);
                        LocalDateTime tempoAbertura = LocalDateTime.parse(linhas[2]);
                        String tempoDuracao = linhas[3];

                        List<Candidato> candidatos = new ArrayList<>();
                        for (int i = 4; i < linhas.length - 1; i++) {
                            String[] partes = linhas[i].split(":");
                            if (partes.length == 2) {
                                String nome = partes[0];
                                int votos = Integer.parseInt(partes[1]);
                                Candidato candidato = new Candidato(nome);
                                for (int j = 0; j < votos; j++) {
                                    candidato.incrementarVoto();
                                }
                                candidatos.add(candidato);
                            }
                        }

                        ultimaEnquete = new Enquete(titulo, candidatos, tempoAbertura, tempoDuracao, status);
                        System.out.println("Enquete atualizada via UDP - Total de candidatos: " + candidatos.size());

                        // Notifica o listener sobre a atualização
                        if (listener != null) {
                            listener.onAtualizacao(ultimaEnquete);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro no cliente UDP: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static void parar() {
        rodando = false;
    }

    public static Enquete getUltimaEnquete() {
        return ultimaEnquete;
    }

    // Interface para notificar atualizações
    public interface AtualizacaoListener {
        void onAtualizacao(Enquete enquete);
    }
}