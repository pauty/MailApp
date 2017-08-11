/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.client.view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
import mailapp.EMail;
import mailapp.User;
    
/**
 *
 * @author pauty
 */
public class InboxPanel extends JPanel{
    private JButton newMailButton;
    private JButton deleteMailButton;
    private JButton logoutButton;
    private JLabel welcomeLabel;
    private JList<EMail> mailList;
    private DefaultListModel<EMail> listModel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel buttonsPanel;
    private JScrollPane jScrollPane1;
    
    private class ButtonsListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("New Mail")){
                MailAppClientView.getInstance().showMailWriterPanel(null, EMail.Type.NEW);
            }
            else if(ae.getActionCommand().equals("Delete")){
            }
            else if(ae.getActionCommand().equals("Logout")){
            }
            else{
                //SOULD NOT BE HERE
            }
        }      
    }
    
    private int previousSelectedIndex = -2;
    
    private class ListListener extends MouseAdapter{  
        @Override
        public void mouseClicked(MouseEvent me) {
            if(me.isControlDown()){
                previousSelectedIndex = -2;
                return;
            }  
            int index = mailList.locationToIndex(me.getPoint());
            if(index == previousSelectedIndex){
                MailAppClientView.getInstance().showMailReaderPanel(listModel.get(index));
            }
            else{
                previousSelectedIndex = index;
            }
        }
    }
    
    public InboxPanel(){
        leftPanel = new JPanel();
        welcomeLabel = new JLabel();
        rightPanel = new JPanel();
        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        newMailButton = new JButton("New Mail");
        deleteMailButton = new JButton("Delete");
        logoutButton = new JButton("Logout");
        jScrollPane1 = new JScrollPane();
        mailList = new JList<>();

        setLayout(new BorderLayout());
        
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);
        leftPanel.setMaximumSize(new Dimension(100, 2000000));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        leftPanel.add(logoutButton);

        add(leftPanel, BorderLayout.LINE_START);

        rightPanel.setLayout(new BorderLayout());

        buttonsPanel.add(newMailButton);
        buttonsPanel.add(deleteMailButton);

        rightPanel.add(buttonsPanel, BorderLayout.PAGE_START);
        
        ButtonsListener buttonListener = new ButtonsListener();
        newMailButton.addActionListener(buttonListener);
        deleteMailButton.addActionListener(buttonListener);
 
        listModel = new DefaultListModel<EMail>();
  
        ListCellRenderer<EMail> r = (ListCellRenderer<EMail>)new InboxMailRenderer();
        mailList.setCellRenderer(r);
        mailList.addMouseListener(new ListListener());

        mailList.setModel(listModel);
        jScrollPane1.setViewportView(mailList);

        rightPanel.add(jScrollPane1, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);
        
        this.setBorder(new EmptyBorder(10,10,10,10));       
    }
    
    public void setUserLabel(User u){
        welcomeLabel.setText("Welcome back, " + u.getName());
    }
    
    public void updateInboxList(ArrayList<EMail> mailList){
        listModel.clear();
        previousSelectedIndex = -2;

        for(int i = 0; i < mailList.size(); i++){
            listModel.addElement(mailList.get(i));
        }   
    }
}
