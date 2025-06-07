package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import model.Candidato;
import model.Enquete;

public class ServidorUDP {
    private static final int PORTA = 9872;
    private static boolean rodando = true;
    private static DatagramSocket socket;

    public static void iniciar(List<Enquete> enquetes) {
        try {
            // Fecha o socket anterior se existir
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            // Cria um novo socket
            socket = new DatagramSocket(PORTA);
            socket.setBroadcast(true); // Habilita broadcast
            System.out.println("Servidor UDP iniciado na porta " + PORTA);

            new Thread(() -> {
                try {
                    while (rodando && !socket.isClosed()) {
                        // Prepara os dados para envio
                        StringBuilder dados = new StringBuilder();
                        dados.append(enquetes.size()).append("\n");

                        for (Enquete enquete : enquetes) {
                            dados.append(enquete.getId()).append("\n");
                            dados.append(enquete.getTitulo()).append("\n");
                            dados.append(enquete.isStatus()).append("\n");
                            dados.append(enquete.getTempoAbertura()).append("\n");
                            dados.append(enquete.getTempoDuracao()).append("\n");

                            for (Candidato candidato : enquete.getCandidatos()) {
                                dados.append(candidato.getNome()).append(":")
                                        .append(candidato.getVotos()).append("\n");
                            }
                            dados.append("FIM_ENQUETE\n");
                        }

                        // Envia os dados para todos os clientes na rede local
                        byte[] buffer = dados.toString().getBytes();
                        DatagramPacket pacote = new DatagramPacket(
                                buffer,
                                buffer.length,
                                InetAddress.getByName("255.255.255.255"),
                                PORTA + 1);

                        socket.send(pacote);
                        System.out.println("Dados enviados via UDP - Total de enquetes: " + enquetes.size());

                        // Aguarda um pouco antes de enviar a próxima atualização
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        System.err.println("Erro no servidor UDP: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar servidor UDP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void parar() {
        rodando = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public static void enviarAtualizacaoEnquetes() {
        try {
            DatagramSocket socket = new DatagramSocket();
            String mensagem = "atualizar-enquetes";
            byte[] buffer = mensagem.getBytes();

            // IP de broadcast
            InetAddress endereco = InetAddress.getByName("255.255.255.255");
            DatagramPacket pacote = new DatagramPacket(buffer, buffer.length, endereco, 9876);

            socket.setBroadcast(true);
            socket.send(pacote);
            socket.close();

            System.out.println("[UDP] Atualização enviada via broadcast.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}