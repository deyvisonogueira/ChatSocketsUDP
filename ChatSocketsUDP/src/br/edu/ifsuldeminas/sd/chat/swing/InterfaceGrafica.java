package br.edu.ifsuldeminas.sd.chat.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.Border;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;
//@Deyvison&Dayane
public class InterfaceGrafica implements MessageContainer {

    private JFrame frame;
    private JTextField textLocal, textRemota, textName, msgText;
    private JTextArea textRecebimento;

    private int localPort, serverPort;
    private String from;
    private Sender sender;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                InterfaceGrafica window = new InterfaceGrafica();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public InterfaceGrafica() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Chat Socket");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400); // Tamanho ajustado para layout horizontal
        frame.setLayout(new BorderLayout());

        frame.add(createHeaderPanel(), BorderLayout.NORTH);
        frame.add(createChatPanel(), BorderLayout.CENTER);
        frame.add(createInputPanel(), BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null); // Centraliza a janela na tela
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        headerPanel.setBackground(new Color(245, 245, 245));  // Cinza Claro
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        headerPanel.add(createLabel("Porta local:"));
        textLocal = createTextField(6);
        headerPanel.add(textLocal);

        headerPanel.add(createLabel("Porta remota:"));
        textRemota = createTextField(6);
        headerPanel.add(textRemota);

        headerPanel.add(createLabel("Nome:"));
        textName = createTextField(10);
        headerPanel.add(textName);

        JButton btnConfirmar = new JButton(new ImageIcon(InterfaceGrafica.class.getResource("/Img/click-here.png")));
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setContentAreaFilled(false);
        btnConfirmar.setFocusPainted(false);  // Remover a borda de foco
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.addActionListener(this::btnConfirmarActionPerformed);
        headerPanel.add(btnConfirmar);

        return headerPanel;
    }

    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(new Color(248, 248, 248));  // Cinza Claro
        chatPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        textRecebimento = new JTextArea();
        textRecebimento.setEditable(false);
        textRecebimento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textRecebimento.setLineWrap(true);
        textRecebimento.setWrapStyleWord(true);
        textRecebimento.setMargin(new Insets(5, 5, 5, 5));
        textRecebimento.setForeground(new Color(50, 50, 50));  // Cinza Escuro
        textRecebimento.setBackground(new Color(255, 255, 255));  // Branco
        textRecebimento.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollPane = new JScrollPane(textRecebimento);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));  // Bordas Cinza Claro
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        return chatPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(255, 255, 255));  // Branco
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        msgText = new JTextField();
        msgText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msgText.setForeground(new Color(50, 50, 50));  // Cinza Escuro
        msgText.setBackground(new Color(240, 240, 240));  // Cinza Muito Claro
        msgText.setBorder(createTextFieldBorder());
        inputPanel.add(msgText, BorderLayout.CENTER);

        JButton sendButton = new JButton(new ImageIcon(InterfaceGrafica.class.getResource("/Img/enviar.png")));
        sendButton.setBorderPainted(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setFocusPainted(false);  // Remover a borda de foco
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> enviarMensagem());
        inputPanel.add(sendButton, BorderLayout.EAST);

        return inputPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 50));  // Cinza Escuro
        return label;
    }

    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(new Color(50, 50, 50));  // Cinza Escuro
        textField.setBackground(new Color(240, 240, 240));  // Cinza Muito Claro
        textField.setBorder(createTextFieldBorder());
        return textField;
    }

    private Border createTextFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 122, 204), 2), // Azul Sofisticado
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void btnConfirmarActionPerformed(ActionEvent evt) {
        try {
            localPort = Integer.parseInt(textLocal.getText());
            serverPort = Integer.parseInt(textRemota.getText());
            from = textName.getText();
            sender = ChatFactory.build("localhost", serverPort, localPort, this);
            textRecebimento.append("Conectado com sucesso!\n");
        } catch (ChatException | NumberFormatException e) {
            textRecebimento.append("Erro ao conectar: " + e.getMessage() + "\n");
        }
    }

    private void enviarMensagem() {
        try {
            String message = msgText.getText();
            if (!message.trim().isEmpty()) {
                String conversa = String.format("%s: %s%n", from, message);
                textRecebimento.append(conversa);
                message = String.format("%s%s%s", message, MessageContainer.FROM, from);
                sender.send(message);
                msgText.setText("");
                msgText.requestFocus();
            }
        } catch (ChatException e) {
            textRecebimento.append("Erro ao enviar mensagem: " + e.getMessage() + "\n");
        }
    }

    @Override
    public void newMessage(String message) {
        if (message == null || message.isEmpty()) return;
        String[] messageParts = message.split(MessageContainer.FROM);
        String formattedMessage = String.format("%s: %s%n", messageParts[1].trim(), messageParts[0].trim());
        textRecebimento.append(formattedMessage);
    }
}
