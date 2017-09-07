
package mailapp.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
    private JComboBox<String> folderComboBox;
    private JLabel welcomeLabel;
    private JList<EMail> mailList;
    private DefaultListModel<EMail> listModel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel buttonsPanel;
    private JScrollPane jScrollPane1;
    private MailAppClientView parentFrame;
    
    //CONTROLLER
    
    private class ButtonsListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("New Mail")){
                parentFrame.showMailWriterPanel(null, EMail.Type.NEW);
            }
            else if(ae.getActionCommand().equals("Delete")){
                int[] indices = mailList.getSelectedIndices();
                if(indices.length == 0)
                    return;
                ArrayList<EMail> mails = new ArrayList<EMail>();
                for(int i = 0; i < indices.length; i++){
                    mails.add(listModel.get(indices[i]));
                }
                String folder = (String)folderComboBox.getSelectedItem();
                parentFrame.getConnectionManager().deleteMail(folder, mails);
            }
            else if(ae.getActionCommand().equals("Logout")){
                parentFrame.getConnectionManager().disconnect();
                listModel.clear();
                parentFrame.showUserSelectionPanel();
            }
        }      
    }
    
    private class ComboBoxListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("fired");
                updateFolderMails();
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
            Rectangle cellBounds = mailList.getCellBounds(index, index);
            if(cellBounds == null){
                previousSelectedIndex = -2;
                mailList.clearSelection();
            }
            else if(cellBounds.contains(me.getPoint()) && index == previousSelectedIndex){
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
        Font bigFont = new Font("Noto Sans", 1, 16);
        
        //init combo box
        String[] folderStrings = parentFrame.getConnectionManager().getFolderNames();
        folderComboBox = new JComboBox<String>(folderStrings);
        folderComboBox.setSelectedIndex(0);
        folderComboBox.addItemListener(new ComboBoxListener());
        folderComboBox.setFont(bigFont);
        folderComboBox.setMaximumSize(new Dimension(150,30));
        
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        
        welcomeLabel.setMaximumSize(new Dimension(200, 40));
        welcomeLabel.setMinimumSize(new Dimension(200, 40));
        welcomeLabel.setPreferredSize(new Dimension(200, 40));
        
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        folderComboBox.setAlignmentX(CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);
        
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.setMinimumSize(new Dimension(250, 10));
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(6, 50)));
        leftPanel.add(folderComboBox);
        leftPanel.add(Box.createVerticalGlue());
        logoutButton.setFont(bigFont);
        leftPanel.add(logoutButton);
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        add(leftPanel);

        rightPanel.setLayout(new BorderLayout());
        
        newMailButton.setFont(bigFont);
        deleteMailButton.setFont(bigFont);
        buttonsPanel.add(newMailButton);
        buttonsPanel.add(deleteMailButton);

        rightPanel.add(buttonsPanel, BorderLayout.PAGE_START);
        
        ButtonsListener buttonListener = new ButtonsListener();
        newMailButton.addActionListener(buttonListener);
        deleteMailButton.addActionListener(buttonListener);
        logoutButton.addActionListener(buttonListener);
 
        listModel = new DefaultListModel<EMail>();
  
        ListCellRenderer<EMail> r = (ListCellRenderer<EMail>)new InboxMailRenderer();
        mailList.setCellRenderer(r);
        mailList.addMouseListener(new ListMouseListener());

        mailList.setModel(listModel);
        jScrollPane1.setViewportView(mailList);

        rightPanel.add(jScrollPane1, BorderLayout.CENTER);

        add(rightPanel);
        
        this.setBorder(new EmptyBorder(10, 10, 10, 10)); 
          
    }
    
    public void setUserLabel(User u){
        welcomeLabel.setText("<html>Welcome back,<br>" + u.getName() +"</html>");
    }
    
    public String getCurrentFolderName(){
        return (String)folderComboBox.getSelectedItem();
    }
    
    public synchronized void updateFolderMails(){
        //the list model must be updated by swing event queue (see update of main view)
        String folder = (String)folderComboBox.getSelectedItem();
        List<EMail> mails = parentFrame.getConnectionManager().getFolderMails(folder);

        //Note: add happens in reverse order: newer mails are on top (lower list index)
        if(mails.size() == listModel.size()){
            boolean modified = false;
            for(int i = 0; !modified && i < mails.size(); i++){
                if(!mails.get(i).equals(listModel.get(listModel.size() - 1 - i)))
                    modified = true;
            }
            if(!modified)
                return;
        }

        previousSelectedIndex = -2;
        listModel.clear();
        for(int i = mails.size() - 1; i >= 0; i--){
            listModel.addElement(mails.get(i));
        }
    }
}
