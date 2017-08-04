
package mailapp.client.view;

import javax.swing.*;
import java.awt.*;

public class MailWriterPanel extends javax.swing.JPanel {
    private JPanel buttonsPanel;
    private JButton cancelButton;
    private JPanel fieldsPanel;
    private JPanel formalLabelPanel;
    private JPanel messagePanel;
    private JScrollPane messageScrollPane;
    private JTextArea messageTextArea;
    private JButton sendButton;
    private JTextField subjectField;
    private JLabel subjectLabel;
    private JTextField toField;
    private JLabel toLabel;
    private JPanel topPanel;
    
    public MailWriterPanel() {
        
        topPanel = new JPanel();
        formalLabelPanel = new JPanel();
        subjectLabel = new JLabel("Subject");
        toLabel = new JLabel("To");
        fieldsPanel = new JPanel();
        subjectField = new JTextField();
        toField = new JTextField();
        buttonsPanel = new JPanel();
        cancelButton = new JButton("Cancel");
        sendButton = new JButton("Send");
        messagePanel = new JPanel();
        messageScrollPane = new JScrollPane();
        messageTextArea = new JTextArea();

        setLayout(new BorderLayout());

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout());

        formalLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        formalLabelPanel.setLayout(new GridLayout(2, 1));

        subjectLabel.setFont(new Font("Noto Sans", 1, 14)); 
        formalLabelPanel.add(subjectLabel);

        toLabel.setFont(new Font("Noto Sans", 1, 14)); 
        formalLabelPanel.add(toLabel);

        topPanel.add(formalLabelPanel, BorderLayout.WEST);

        fieldsPanel.setLayout(new GridLayout(2, 1));

        subjectField.setFont(new Font("Noto Sans", 0, 14)); 
        fieldsPanel.add(subjectField);

        toField.setFont(new Font("Noto Sans", 0, 14));
        fieldsPanel.add(toField);

        topPanel.add(fieldsPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new GridLayout());

        cancelButton.setFont(new Font("Noto Sans", 1, 16));
        buttonsPanel.add(cancelButton);

        sendButton.setFont(new Font("Noto Sans", 1, 16));
        buttonsPanel.add(sendButton);

        topPanel.add(buttonsPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.PAGE_START);

        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.LINE_AXIS));

        messageTextArea.setColumns(20);
        messageTextArea.setRows(5);
        messageScrollPane.setViewportView(messageTextArea);

        messagePanel.add(messageScrollPane);

        add(messagePanel, BorderLayout.CENTER);
    }
                 
}