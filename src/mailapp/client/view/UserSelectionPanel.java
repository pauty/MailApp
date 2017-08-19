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
import mailapp.User;

/**
 *
 * @author pauty
 */
public class UserSelectionPanel extends JPanel{
    //hard coded users    
    private final String USERNAME1 = "Alan Turing";
    private final String USERNAME2 = "John Von Neumann";
    private final String USERNAME3 = "Edsger Dijkstra";
    private final String USERADDRESS1 = "turing@mailapp.com";
    private final String USERADDRESS2 = "vonneumann@mailapp.com";
    private final String USERADDRESS3 = "dijkstra@mailapp.com";
    
    private JLabel infoLabel;
    private JLabel errorLabel;
    private JComboBox<String> userComboBox;
    private JButton loginButton;
    private MailAppClientView parentFrame;
    
    //CONTROLLER 
    
    private class loginButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            String userName = (String)userComboBox.getSelectedItem();
            String userAddress = null;
            User user;
            
            if(userName.equals(USERNAME1)){
                userAddress = USERADDRESS1;
            }
            else if(userName.equals(USERNAME2)){
                userAddress = USERADDRESS2;
            }
            else if(userName.equals(USERNAME3)){
                userAddress = USERADDRESS3;
            }
            
            //change te current user
            user = new User(userName, userAddress);
            parentFrame.getConnectionManager().setCurrentUser(user);
            boolean ok = parentFrame.getConnectionManager().connect();
            //make the view show show inbox panel
            if(ok){
                parentFrame.showInboxPanel();
                setErrorMessage("");
            }
            else{
                setErrorMessage("ERROR - Could not connect to server");
            }
        }
    }  
    
    //END CONTROLLER
    
    public UserSelectionPanel(MailAppClientView parent){
        //set parent
        parentFrame = parent;
        
        //init GUI
        infoLabel = new JLabel();
        errorLabel = new JLabel("");
        userComboBox = new JComboBox<String>();
        loginButton = new JButton();
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        infoLabel.setFont(new Font("Noto Sans", 0, 16)); 
        infoLabel.setText("Please select an user");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userComboBox.setFont(new Font("Noto Sans", 0, 16)); 
        userComboBox.setModel(new DefaultComboBoxModel<>(new String[] { USERNAME1, USERNAME2, USERNAME3 }));
        userComboBox.setMaximumSize(new Dimension(200,40));
        userComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginButton.setFont(new Font("Noto Sans", 1, 16));
        loginButton.addActionListener(new loginButtonListener());
        loginButton.setText("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        add(Box.createRigidArea(new Dimension(10, 100)));
        add(infoLabel);
        add(Box.createRigidArea(new Dimension(10, 20)));
        add(userComboBox);
        add(Box.createRigidArea(new Dimension(10, 20)));
        add(loginButton);
        add(Box.createRigidArea(new Dimension(10, 40)));
        add(errorLabel);
    }
    
    public void setErrorMessage(String msg){
        errorLabel.setText(msg);
    }
}
