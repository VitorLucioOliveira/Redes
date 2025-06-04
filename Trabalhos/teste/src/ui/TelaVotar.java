package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Enquete;
import model.Candidato;
import client.ClienteTCP;
import client.ClienteUDP;

public class TelaVotar extends JFrame implements ClienteUDP.AtualizacaoListener {
    private Enquete enquete;
    private JLayeredPane layeredPane;

    public TelaVotar() {
        this.setTitle("Trabalho Pratico Redes");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1300, 950);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        ImageIcon imageIcon = new ImageIcon("img/puc.png");
        this.setIconImage(imageIcon.getImage());

        BackgroundLabel background = new BackgroundLabel("PUC MINAS");
        background.setBounds(0, 0, 1300, 950);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1300, 950));
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);

        // Inicializa a enquete com dados do servidor
        enquete = ClienteTCP.obterInformacoesEnquete();
        if (enquete != null) {
            barraInformacoes(layeredPane);
            adicionarCardsGrid(layeredPane);
        }

        // Inicia o cliente UDP para receber atualizações
        ClienteUDP.iniciarRecebimento(this);

        this.setContentPane(layeredPane);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void onAtualizacao(Enquete novaEnquete) {
        // Atualiza a UI na thread de eventos
        SwingUtilities.invokeLater(() -> {
            enquete = novaEnquete;
            atualizarUI();
        });
    }

    private void atualizarUI() {
        // Remove apenas os componentes da camada PALETTE_LAYER
        Component[] components = layeredPane.getComponents();
        for (Component comp : components) {
            if (layeredPane.getLayer(comp) == JLayeredPane.PALETTE_LAYER) {
                layeredPane.remove(comp);
            }
        }

        if (enquete != null) {
            barraInformacoes(layeredPane);
            adicionarCardsGrid(layeredPane);
            layeredPane.revalidate();
            layeredPane.repaint();
        }
    }

    private void barraInformacoes(JLayeredPane pane) {
        List<Candidato> candidatos = enquete.getCandidatos();
        String tempo = enquete.getTempoDuracao();
        String abertura = enquete.getTempoAbertura();
        boolean status = enquete.isStatus();

        int barraAltura = 90;
        JPanel barra = new JPanel();
        barra.setLayout(null);
        barra.setBounds(0, 0, 1300, barraAltura);
        barra.setBackground(new Color(40, 40, 40, 230));

        Font infoFont = new Font("Segoe UI", Font.BOLD, 18);
        Font subFont = new Font("Segoe UI", Font.PLAIN, 15);
        Color textColor = new Color(230, 230, 230);

        JLabel statusLabel = new JLabel("Votação " + (status ? "aberta" : "fechada"));
        statusLabel.setFont(infoFont);
        statusLabel.setForeground(textColor);
        statusLabel.setBounds(30, 10, 250, 30);

        JLabel tempoLabel = new JLabel("Tempo: " + tempo + " | " + "Abertura: " + abertura);
        tempoLabel.setFont(subFont);
        tempoLabel.setForeground(textColor);
        tempoLabel.setBounds(30, 45, 350, 25);

        // Encontrar candidato mais votado
        Candidato maisVotado = null;
        for (Candidato c : candidatos) {
            if (maisVotado == null || c.getVotos() > maisVotado.getVotos()) {
                maisVotado = c;
            }
        }
        String maisVotadoStr = maisVotado != null ? maisVotado.getNome() + " - " + maisVotado.getVotos() + " votos"
                : "-";
        JLabel maisVotadoLabel = new JLabel("Mais votado: " + maisVotadoStr);
        maisVotadoLabel.setFont(infoFont);
        maisVotadoLabel.setForeground(textColor);
        maisVotadoLabel.setBounds(700, 25, 450, 30);
        maisVotadoLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        barra.add(statusLabel);
        barra.add(tempoLabel);
        barra.add(maisVotadoLabel);
        pane.add(barra, JLayeredPane.PALETTE_LAYER);
    }

    private void adicionarCardsGrid(JLayeredPane pane) {
        List<Candidato> candidatos = enquete.getCandidatos();
        int cardsPorLinha = 6;
        int cardWidth = 170;
        int cardHeight = 140;
        int spacing = 30;
        int startX = 60;
        int startY = 120;

        Color cardBg = new Color(60, 60, 60, 220);
        Color textColor = new Color(230, 230, 230);
        Font nomeFont = new Font("Segoe UI", Font.BOLD, 16);
        Font subFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        for (Candidato candidato : candidatos) {
            int i = candidatos.indexOf(candidato);
            int linha = i / cardsPorLinha;
            int coluna = i % cardsPorLinha;
            int x = startX + coluna * (cardWidth + spacing);
            int y = startY + linha * (cardHeight + spacing);

            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(cardBg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            card.setLayout(null);
            card.setBounds(x, y, cardWidth, cardHeight);
            card.setOpaque(false);

            JLabel nomeLabel = new JLabel(candidato.getNome());
            nomeLabel.setBounds(10, 15, cardWidth - 20, 25);
            nomeLabel.setFont(nomeFont);
            nomeLabel.setForeground(textColor);
            nomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Painel para votos com ícone
            JPanel votosPanel = new JPanel();
            votosPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            votosPanel.setBounds(10, 40, cardWidth - 20, 25);
            votosPanel.setOpaque(false);

            JLabel votosIcon = new JLabel("🗳️");
            votosIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            votosPanel.add(votosIcon);

            JLabel votosLabel = new JLabel(candidato.getVotos() + " votos");
            votosLabel.setFont(subFont);
            votosLabel.setForeground(textColor);
            votosPanel.add(votosLabel);

            JButton votarButton = new JButton("Votar");
            votarButton.setBounds(25, 75, cardWidth - 50, 35);
            votarButton.setFont(buttonFont);
            votarButton.setForeground(textColor);
            votarButton.setBackground(new Color(100, 100, 100));
            votarButton.setFocusPainted(false);
            votarButton.setBorderPainted(false);
            votarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            votarButton.addActionListener(e -> {
                String resposta = ClienteTCP.enviarVoto(candidato.getNome());
                JOptionPane.showMessageDialog(null, resposta);
                atualizarUI(); // Atualiza imediatamente após votar
            });

            card.add(nomeLabel);
            card.add(votosPanel);
            card.add(votarButton);
            pane.add(card, JLayeredPane.PALETTE_LAYER);
        }
    }
}
