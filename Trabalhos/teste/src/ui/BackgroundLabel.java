package ui;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class BackgroundLabel extends JLabel {
    public BackgroundLabel(String text) {
        super(text);

        ImageIcon image = new ImageIcon("img/puc2.png");
        Border border = BorderFactory.createLineBorder(Color.gray, 5);
        this.setIcon(image);

        // Texto sobre a imagem
        this.setHorizontalTextPosition(CENTER);
        this.setVerticalTextPosition(BOTTOM);

        this.setVerticalAlignment(CENTER);
        this.setHorizontalAlignment(CENTER);

        this.setForeground(Color.gray);
        this.setFont(new Font("Arial", Font.BOLD, 24));
        this.setIconTextGap(10);
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        this.setBorder(border);

        // Deixe o layout cuidar do tamanho
        this.setPreferredSize(new Dimension());
    }
}
