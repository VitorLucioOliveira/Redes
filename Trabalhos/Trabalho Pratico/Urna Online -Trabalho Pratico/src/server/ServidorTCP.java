package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Candidato;
import model.Enquete;

public class ServidorTCP {

    private static final int PORTA = 9871;
    private static List<Enquete> enquetes;

    public static void main(String[] args) throws Exception {
        // Cria enquete global com tempo de abertura atual
        LocalDateTime tempoAbertura = LocalDateTime.now();
        System.out.println("Tempo de abertura da enquete: " + tempoAbertura);

        enquetes = new ArrayList<>();

        Enquete enquete = new Enquete(
                "Votacao para o melhor candidato",
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
                        new Candidato("Marcos")
                       ),
                tempoAbertura,
                "00:00:10",
                true);

        Enquete enquete2 = new Enquete(
                "Dia da prova de Redes",
                Arrays.asList(
                        new Candidato("Segunda"),
                        new Candidato("Terca"),
                        new Candidato("Quarta"),
                        new Candidato("Quinta"),
                        new Candidato("Sexta"),
                        new Candidato("Sabado"),
                        new Candidato("Domingo")),
                tempoAbertura,
                "00:01:00",
                true);

        enquetes.add(enquete);
        enquetes.add(enquete2);

        ServidorUDP.iniciar(enquetes);

        ServerSocket servidor = new ServerSocket(PORTA);
        System.out.println("\nServidor TCP ouvindo na porta " + PORTA);

        while (true) {
            Socket conexao = servidor.accept();
            System.out.println("\nNova conexao recebida de: " + conexao.getInetAddress().getHostAddress());

            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());

            String comando = entrada.readLine();
            System.out.println("Comando recebido: " + comando);

            if (comando.startsWith("VOTO:")) {
                System.out.println("Processando comando VOTO: " + comando);
                String[] partes = comando.split(":");
                if (partes.length < 3) {
                    System.out.println("Comando VOTO inválido - formato incorreto");
                    saida.writeBytes("Comando VOTO inválido. Use VOTO:<idEnquete>:<nomeCandidato>\n");
                } else {
                    try {
                        String idEnquete = partes[1].trim();
                        String nomeCandidato = partes[2].trim();
                        System.out.println("Buscando enquete ID: " + idEnquete + " para candidato: " + nomeCandidato);

                        // Buscar a enquete pelo ID
                        Enquete enqueteAlvo = null;
                        for (Enquete e : enquetes) {
                            System.out.println("Comparando com enquete ID: " + e.getId());
                            if (e.getId().equals(idEnquete)) {
                                enqueteAlvo = e;
                                System.out.println("Enquete encontrada: " + e.getTitulo());
                                break;
                            }
                        }

                        if (enqueteAlvo == null) {
                            System.out.println("Enquete nao encontrada: " + idEnquete);
                            saida.writeBytes("Enquete com ID " + idEnquete + " nao encontrada.\n");
                        } else if (!enqueteAlvo.isStatus()) {
                            System.out.println("Enquete fechada: " + idEnquete);
                            saida.writeBytes("A enquete está fechada. Voto nao registrado.\n");
                        } else {
                            boolean sucesso = false;
                            for (Candidato c : enqueteAlvo.getCandidatos()) {
                                if (c.getNome().equalsIgnoreCase(nomeCandidato)) {
                                    c.incrementarVoto();
                                    sucesso = true;
                                    System.out.println("Voto registrado para: " + nomeCandidato);
                                    System.out.println("Total de votos atual: " + c.getVotos());
                                    break;
                                }
                            }
                            if (sucesso) {
                                String resposta = "Voto recebido para " + nomeCandidato + "\n";
                                System.out.println("Enviando resposta: " + resposta.trim());
                                saida.writeBytes(resposta);
                            } else {
                                System.out.println("Candidato nao encontrado: " + nomeCandidato);
                                saida.writeBytes("Candidato nao encontrado.\n");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao processar voto: " + e.getMessage());
                        e.printStackTrace();
                        saida.writeBytes("Erro ao processar voto: " + e.getMessage() + "\n");
                    }
                }
            } else if (comando.equals("INFO")) {
                boolean statusAtual = enquetes.get(0).isStatus();

                System.out.println("\nEnviando informacoes da enquete:");
                System.out.println("Título: " + enquetes.get(0).getTitulo());
                System.out.println("Status: " + (statusAtual ? "Aberta" : "Fechada"));
                System.out.println("Tempo de Abertura: " + enquetes.get(0).getTempoAbertura());
                System.out.println("Tempo de Duracao: " + enquetes.get(0).getTempoDuracao());
                System.out.println("\nVotos por candidato:");

                saida.writeBytes(enquetes.get(0).getTitulo() + "\n");
                saida.writeBytes(statusAtual + "\n");
                saida.writeBytes(enquetes.get(0).getTempoAbertura().toString() + "\n");
                saida.writeBytes(enquetes.get(0).getTempoDuracao() + "\n");

                for (Candidato c : enquetes.get(0).getCandidatos()) {
                    String info = c.getNome() + ":" + c.getVotos();
                    System.out.println(info);
                    saida.writeBytes(info + "\n");
                }
                saida.writeBytes("\n");

            } else if (comando.equals("CRIAR_ENQUETE")) {
                String titulo = entrada.readLine();
                System.out.println("Criando nova enquete: " + titulo);

                // Receber a lista de candidatos
                List<Candidato> candidatos = new ArrayList<>();
                String linha;
                while ((linha = entrada.readLine()) != null) {
                    // Se a linha contém uma data, é o tempo de abertura
                    if (linha.contains("T")) {
                        break;
                    }

                    // Ignora linhas vazias ou marcadores
                    if (linha.trim().isEmpty() || linha.equals("FIM_CANDIDATOS")) {
                        continue;
                    }

                    String[] partes = linha.split(":");
                    if (partes.length > 0) {
                        String nome = partes[0].trim();
                        if (!nome.isEmpty()) {
                            int votos = partes.length > 1 ? Integer.parseInt(partes[1]) : 0;
                            Candidato candidato = new Candidato(nome);
                            for (int i = 0; i < votos; i++) {
                                candidato.incrementarVoto();
                            }
                            candidatos.add(candidato);
                            System.out.println("Adicionado candidato: " + nome);
                        }
                    }
                }

                // Receber tempo de abertura (já lido no loop acima)
                LocalDateTime tempoAberturaRecebido = LocalDateTime.parse(linha);
                String tempoDuracao = entrada.readLine();
                boolean status = Boolean.parseBoolean(entrada.readLine());

                // Criar nova enquete e adicionar à lista
                Enquete novaEnquete = new Enquete(titulo, candidatos, tempoAberturaRecebido, tempoDuracao, status);
                enquetes.add(novaEnquete);

                System.out.println("Nova enquete criada: " + titulo);
                System.out.println("Total de candidatos: " + candidatos.size());

                // Confirmacao para o cliente
                saida.writeBytes("ENQUETE_CRIADA\n");

            } else if (comando.equals("LISTAR_ENQUETES")) {
                System.out.println("Listando enquetes disponíveis:");
                for (Enquete e : enquetes) {
                    System.out.println("Enviando enquete ID: " + e.getId());
                    saida.writeBytes(e.getId() + "\n"); // Envia o ID primeiro
                    saida.writeBytes(e.getTitulo() + "\n");
                    saida.writeBytes(e.isStatus() + "\n");
                    saida.writeBytes(e.getTempoAbertura().toString() + "\n");
                    saida.writeBytes(e.getTempoDuracao() + "\n");

                    // Enviar informacoes dos candidatos
                    for (Candidato c : e.getCandidatos()) {
                        saida.writeBytes(c.getNome() + ":" + c.getVotos() + "\n");
                    }
                    saida.writeBytes("FIM_CANDIDATOS\n");
                }
                saida.writeBytes("FIM_LISTA\n");
            }

            conexao.close();
            System.out.println("Conexao encerrada\n");
        }
    }
}
