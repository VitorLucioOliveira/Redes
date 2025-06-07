package ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.Candidato;
import model.Enquete;
import client.ClienteTCP;

public class TelaCriarEnquete extends JFrame {
    private JLayeredPane layeredPane;
    private JTextField tituloField;
    private JTextField candidatoField;
    private JList<String> candidatosList;
    private DefaultListModel<String> candidatosListModel;
    private JTextField tempoDuracaoField;

    public TelaCriarEnquete() {
        this.setTitle("Criar Nova Enquete");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        ImageIcon imageIcon = new ImageIcon("img/puc.png");
        this.setIconImage(imageIcon.getImage());

        BackgroundLabel background = new BackgroundLabel("PUC MINAS");
        background.setBounds(0, 0, 800, 600);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);

        criarFormulario(layeredPane);

        this.setContentPane(layeredPane);
        this.pack();
        this.setVisible(true);
    }

    private void criarFormulario(JLayeredPane pane) {
        // Painel principal do formulário
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 60, 60, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        formPanel.setLayout(null);
        formPanel.setBounds(50, 50, 700, 500);
        formPanel.setOpaque(false);

        // Título
        JLabel tituloLabel = new JLabel("Título da Enquete:");
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setBounds(30, 30, 200, 30);
        formPanel.add(tituloLabel);

        tituloField = new JTextField();
        tituloField.setBounds(30, 60, 640, 35);
        tituloField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(tituloField);

        // Candidatos
        JLabel candidatosLabel = new JLabel("Candidatos:");
        candidatosLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        candidatosLabel.setForeground(Color.WHITE);
        candidatosLabel.setBounds(30, 110, 200, 30);
        formPanel.add(candidatosLabel);

        // Campo de entrada para novo candidato
        candidatoField = new JTextField();
        candidatoField.setBounds(30, 140, 500, 35);
        candidatoField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(candidatoField);

        // Botão para adicionar candidato
        JButton addButton = new JButton("+");
        addButton.setBounds(540, 140, 35, 35);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(new Color(0, 120, 0));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> adicionarCandidato());
        formPanel.add(addButton);

        // Lista de candidatos
        candidatosListModel = new DefaultListModel<>();
        candidatosList = new JList<>(candidatosListModel);
        candidatosList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        candidatosList.setBackground(new Color(45, 45, 45));
        candidatosList.setForeground(Color.WHITE);
        candidatosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(candidatosList);
        scrollPane.setBounds(30, 185, 500, 150);
        scrollPane.setBackground(new Color(45, 45, 45));
        formPanel.add(scrollPane);

        // Botão para remover candidato
        JButton removeButton = new JButton("Remover");
        removeButton.setBounds(540, 185, 120, 35);
        removeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        removeButton.setForeground(Color.WHITE);
        removeButton.setBackground(new Color(180, 0, 0));
        removeButton.setFocusPainted(false);
        removeButton.setBorderPainted(false);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButton.addActionListener(e -> removerCandidato());
        formPanel.add(removeButton);

        // Tempo de Duração
        JLabel tempoLabel = new JLabel("Tempo de Duração (HH:MM:SS):");
        tempoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tempoLabel.setForeground(Color.WHITE);
        tempoLabel.setBounds(30, 355, 250, 30);
        formPanel.add(tempoLabel);

        tempoDuracaoField = new JTextField("00:01:00");
        tempoDuracaoField.setBounds(30, 385, 200, 35);
        tempoDuracaoField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(tempoDuracaoField);

        // Botões
        JButton voltarButton = new JButton("← Voltar");
        voltarButton.setBounds(30, 440, 120, 40);
        voltarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        voltarButton.setForeground(Color.WHITE);
        voltarButton.setBackground(new Color(100, 100, 100));
        voltarButton.setFocusPainted(false);
        voltarButton.setBorderPainted(false);
        voltarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltarButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                TelaEnquete telaEnquete = new TelaEnquete();
                telaEnquete.setVisible(true);
                this.dispose();
            });
        });
        formPanel.add(voltarButton);

        JButton criarButton = new JButton("Criar Enquete");
        criarButton.setBounds(550, 440, 120, 40);
        criarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        criarButton.setForeground(Color.WHITE);
        criarButton.setBackground(new Color(0, 120, 0));
        criarButton.setFocusPainted(false);
        criarButton.setBorderPainted(false);
        criarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        criarButton.addActionListener(e -> criarEnquete());
        formPanel.add(criarButton);

        // Adiciona listener para Enter no campo de candidato
        candidatoField.addActionListener(e -> adicionarCandidato());

        pane.add(formPanel, JLayeredPane.PALETTE_LAYER);
    }

    private void adicionarCandidato() {
        String nome = candidatoField.getText().trim();
        if (!nome.isEmpty()) {
            if (candidatosListModel.size() >= 30) {
                JOptionPane.showMessageDialog(this,
                        "Limite máximo de 30 candidatos atingido!",
                        "Limite Excedido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            candidatosListModel.addElement(nome);
            candidatoField.setText("");
            candidatoField.requestFocus();
        }
    }

    private void removerCandidato() {
        int selectedIndex = candidatosList.getSelectedIndex();
        if (selectedIndex != -1) {
            candidatosListModel.remove(selectedIndex);
        }
    }

    private void criarEnquete() {
        String titulo = tituloField.getText().trim();
        String tempoDuracao = tempoDuracaoField.getText().trim();

        // Validação
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um título para a enquete.");
            return;
        }

        if (candidatosListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira pelo menos um candidato.");
            return;
        }

        if (!tempoDuracao.matches("\\d{2}:\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o tempo de duração no formato HH:MM:SS.");
            return;
        }

        // Processa os candidatos
        List<Candidato> candidatos = new ArrayList<>();
        for (int i = 0; i < candidatosListModel.size(); i++) {
            candidatos.add(new Candidato(candidatosListModel.getElementAt(i)));
        }

        // Cria a enquete
        String resposta = ClienteTCP.enviarNovaEnquete(
                titulo,
                candidatos,
                LocalDateTime.now().toString(),
                tempoDuracao,
                true);

        JOptionPane.showMessageDialog(this, resposta);

        // Se a criação foi bem-sucedida, volta para a tela de enquetes
        if (resposta.contains("sucesso")) {
            SwingUtilities.invokeLater(() -> {
                TelaEnquete telaEnquete = new TelaEnquete();
                telaEnquete.setVisible(true);
                this.dispose();
            });
        }
    }
}