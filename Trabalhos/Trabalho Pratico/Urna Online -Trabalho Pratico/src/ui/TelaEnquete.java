package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Enquete;
import client.ClienteTCP;
import client.ClienteUDP;

public class TelaEnquete extends JFrame {
    private JLayeredPane layeredPane;
    private List<Enquete> enquetes;
    private JScrollPane enquetesScrollPane;

    public TelaEnquete() {
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

        enquetes = ClienteTCP.listarEnquetes();
        if (enquetes != null) {
            adicionarBarraSuperior(layeredPane);
            adicionarListaEnquetes(layeredPane);
        } else {
            // If enquetes is null, maybe show an error or empty state
            System.out.println("Nenhuma enquete disponível.");
            adicionarBarraSuperior(layeredPane);
        }

        this.setContentPane(layeredPane);
        this.pack();
        this.setVisible(true);

        // Inicia o listener UDP para atualizações
        ClienteUDP.iniciarRecebimento(enquetes -> {
            // Callback automático quando novas enquetes são recebidas via UDP
            SwingUtilities.invokeLater(() -> {
                System.out.println("[UDP] Atualizando enquetes recebidas...");
                atualizarEnquetesComDados(enquetes); // ou atualizarEnquetes(), se já acessa
                                                     // ClienteUDP.getUltimasEnquetes()
            });
        });
    }

    private void adicionarBarraSuperior(JLayeredPane pane) {
        int barraAltura = 90;
        JPanel barra = new JPanel();
        barra.setLayout(null);
        barra.setBounds(0, 0, 1300, barraAltura);
        barra.setBackground(new Color(40, 40, 40, 230));

        Font infoFont = new Font("Segoe UI", Font.BOLD, 18);
        Color textColor = new Color(230, 230, 230);

        JLabel tituloLabel = new JLabel("Enquetes Disponíveis");
        tituloLabel.setFont(infoFont);
        tituloLabel.setForeground(textColor);
        tituloLabel.setBounds(30, 25, 300, 30);

        JButton criarEnqueteButton = new JButton("Criar Nova Enquete");
        criarEnqueteButton.setBounds(1000, 25, 250, 40);
        criarEnqueteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        criarEnqueteButton.setForeground(textColor);
        criarEnqueteButton.setBackground(new Color(100, 100, 100));
        criarEnqueteButton.setFocusPainted(false);
        criarEnqueteButton.setBorderPainted(false);
        criarEnqueteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        criarEnqueteButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                TelaCriarEnquete telaCriarEnquete = new TelaCriarEnquete();
                telaCriarEnquete.setVisible(true);
                this.dispose();
            });
        });

        barra.add(tituloLabel);
        barra.add(criarEnqueteButton);
        pane.add(barra, JLayeredPane.PALETTE_LAYER);
    }

    private void adicionarListaEnquetes(JLayeredPane pane) {
        int cardWidth = 300;
        int cardHeight = 180;
        int spacing = 20;
        int initialYPadding = 30;
        int startX = 20;
        int cardsPorLinha = 4;

        Color cardBg = new Color(60, 60, 60, 220);
        Color textColor = new Color(230, 230, 230);
        Font tituloFont = new Font("Segoe UI", Font.BOLD, 16);
        Font infoFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        // Create a panel to hold all cards
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(null);
        cardsPanel.setOpaque(false);

        // Calculate total height needed for the cardsPanel
        int totalRows = (int) Math.ceil((double) enquetes.size() / cardsPorLinha);
        int contentHeight = (totalRows * cardHeight) + (totalRows > 0 ? (totalRows - 1) * spacing : 0);
        int calculatedHeight = initialYPadding + contentHeight + initialYPadding; // Add padding at bottom
        if (enquetes.isEmpty()) {
            calculatedHeight = 100; // A reasonable default height if no enquetes
        }
        cardsPanel.setPreferredSize(new Dimension(1300, calculatedHeight));

        for (int i = 0; i < enquetes.size(); i++) {
            Enquete enquete = enquetes.get(i);
            int linha = i / cardsPorLinha;
            int coluna = i % cardsPorLinha;
            int x = startX + coluna * (cardWidth + spacing);
            int y = initialYPadding + linha * (cardHeight + spacing);

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

            JLabel tituloLabel = new JLabel(enquete.getTitulo());
            tituloLabel.setBounds(15, 15, cardWidth - 30, 25);
            tituloLabel.setFont(tituloFont);
            tituloLabel.setForeground(textColor);

            JLabel statusLabel = new JLabel("Status: " + (enquete.isStatus() ? "Aberta" : "Fechada"));
            statusLabel.setBounds(15, 45, cardWidth - 30, 20);
            statusLabel.setFont(infoFont);
            statusLabel.setForeground(textColor);

            JLabel duracaoLabel = new JLabel("Duração: " + enquete.getTempoDuracao());
            duracaoLabel.setBounds(15, 70, cardWidth - 30, 20);
            duracaoLabel.setFont(infoFont);
            duracaoLabel.setForeground(textColor);

            JLabel candidatosLabel = new JLabel("Candidatos: " + enquete.getCandidatos().size());
            candidatosLabel.setBounds(15, 95, cardWidth - 30, 20);
            candidatosLabel.setFont(infoFont);
            candidatosLabel.setForeground(textColor);

            JButton entrarButton = new JButton("Entrar");
            entrarButton.setBounds(15, 125, cardWidth - 30, 35);
            entrarButton.setFont(buttonFont);
            entrarButton.setForeground(textColor);
            entrarButton.setBackground(new Color(100, 100, 100));
            entrarButton.setFocusPainted(false);
            entrarButton.setBorderPainted(false);
            entrarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            entrarButton.addActionListener(e -> abrirTelaVotar(enquete));

            card.add(tituloLabel);
            card.add(statusLabel);
            card.add(duracaoLabel);
            card.add(candidatosLabel);
            card.add(entrarButton);
            cardsPanel.add(card);
        }

        // Create scroll pane and assign to member variable
        this.enquetesScrollPane = new JScrollPane(cardsPanel);
        this.enquetesScrollPane.setBounds(0, 90, 1300, 860); // Position below the top bar
        this.enquetesScrollPane.setOpaque(false);
        this.enquetesScrollPane.getViewport().setOpaque(false);
        this.enquetesScrollPane.setBorder(null);

        // Customize scrollbar appearance
        this.enquetesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.enquetesScrollPane.getVerticalScrollBar().setBackground(new Color(40, 40, 40));
        this.enquetesScrollPane.getVerticalScrollBar().setForeground(new Color(100, 100, 100));

        pane.add(this.enquetesScrollPane, JLayeredPane.PALETTE_LAYER);
    }

    private void atualizarEnquetesComDados(List<Enquete> novasEnquetes) {
        SwingUtilities.invokeLater(() -> {
            if (novasEnquetes != null) {
                this.enquetes = novasEnquetes;

                // Remove existing scroll pane if it exists
                if (this.enquetesScrollPane != null) {
                    layeredPane.remove(this.enquetesScrollPane);
                }

                // Only re-add the list of enquetes, the background and top bar are permanent
                adicionarListaEnquetes(layeredPane);

                layeredPane.repaint();
                layeredPane.revalidate();
            }
        });
    }

    private void abrirTelaVotar(Enquete enquete) {
        TelaVotar telaVotar = new TelaVotar(enquete);
        telaVotar.setVisible(true);
        this.dispose();
    }
}
