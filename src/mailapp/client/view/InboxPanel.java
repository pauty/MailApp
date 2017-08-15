/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.client.view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
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
    MailAppClientView parentFrame;
    
    //CONTROLLER
    
    private class ButtonsListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("New Mail")){
                parentFrame.showMailWriterPanel(null, EMail.Type.NEW);
            }
            else if(ae.getActionCommand().equals("Delete")){
                int[] indices = mailList.getSelectedIndices();
                ArrayList<EMail> mails = new ArrayList<EMail>();
                for(int i = 0; i < indices.length; i++){
                    mails.add(listModel.get(indices[i]));
                }
                parentFrame.getConnectionManager().deleteMail(mails);
            }
            else if(ae.getActionCommand().equals("Logout")){

            }
        }      
    }
    
    private int previousSelectedIndex = -2;

    private class ListMouseListener extends MouseAdapter{  
        @Override
        public void mouseClicked(MouseEvent me) {
            if(me.isControlDown()){
                previousSelectedIndex = -2;
                return;
            }  
            int index = mailList.locationToIndex(me.getPoint());
            if(mailList.getCellBounds(index, index).contains(me.getPoint()) && index == previousSelectedIndex){
                parentFrame.showMailReaderPanel(listModel.get(index));
            }
            else{
                previousSelectedIndex = index;
            }
        }
    }
    
    //END CONTROLLER
    
    public InboxPanel(MailAppClientView parent){
        //set parent frame
        parentFrame = parent;
        
        //init GUI
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
        welcomeLabel.setMaximumSize(new Dimension(200, 20));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 50)));
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
        mailList.addMouseListener(new ListMouseListener());

        mailList.setModel(listModel);
        jScrollPane1.setViewportView(mailList);

        rightPanel.add(jScrollPane1, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);
        
        this.setBorder(new EmptyBorder(10,10,10,10));       
    }
    
    public void setUserLabel(User u){
        welcomeLabel.setText("Welcome back, " + u.getName());
    }
    
    public void updateInboxList(List<EMail> mailList){
        previousSelectedIndex = -2;
        ArrayList<EMail> toDelete = new ArrayList<EMail>();
        //remove mails no longer present in remote inbox (delete)
        for(int i = 0; i < listModel.size(); i++){
            if(!mailList.contains(listModel.get(i)))
                toDelete.add(listModel.get(i));
        }
        for(int i = 0; i < toDelete.size(); i++){
            listModel.removeElement(toDelete.get(i));
        }
        //add new mails not yet listed in local inbox (update)
        for(int i = 0; i < mailList.size(); i++){
            if(!listModel.contains(mailList.get(i)))
                listModel.insertElementAt(mailList.get(i), i);
        }   
    }
}
