package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import model.Candidato;
import model.Enquete;

public class ServidorUDP {
    private static final int PORTA_SERVIDOR = 9872;
    private static final int PORTA_CLIENTE = 9873;
    private static Enquete enquete;
    private static boolean rodando = true;

    public static void iniciar(Enquete enqueteVotacao) {
        enquete = enqueteVotacao;

        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(PORTA_SERVIDOR)) {
                // Habilita broadcast
                socket.setBroadcast(true);
                System.out.println("\nServidor UDP iniciado na porta " + PORTA_SERVIDOR);

                while (rodando) {
                    // Prepara os dados para envio
                    StringBuilder dados = new StringBuilder();
                    dados.append(enquete.getTitulo()).append("\n");
                    dados.append(enquete.isStatus()).append("\n");
                    dados.append(enquete.getTempoAbertura()).append("\n");
                    dados.append(enquete.getTempoDuracao()).append("\n");

                    // Adiciona informações dos candidatos
                    for (Candidato c : enquete.getCandidatos()) {
                        dados.append(c.getNome()).append(":").append(c.getVotos()).append("\n");
                    }
                    dados.append("\n"); // Marca o fim da lista

                    byte[] buffer = dados.toString().getBytes();

                    // Envia para broadcast local na porta do cliente
                    DatagramPacket pacote = new DatagramPacket(
                            buffer,
                            buffer.length,
                            InetAddress.getByName("255.255.255.255"),// Broadcast para todos os hosts na rede local
                    PORTA_CLIENTE);

                    socket.send(pacote);
                    System.out.println("Informações enviadas via UDP para porta " + PORTA_CLIENTE);

                    // Aguarda 1 segundo antes da próxima atualização
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("Erro no servidor UDP: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static void parar() {
        rodando = false;
    }
}