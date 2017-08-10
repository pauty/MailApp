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
import mailapp.client.connection.ConnectionManager;

/**
 *
 * @author pauty
 */
public class UserSelectionPanel extends JPanel{
        
    private final String USERNAME1 = "Alan Turing";
    private final String USERNAME2 = "John Von Neumann";
    private final String USERNAME3 = "Dijkstra";
    private final String USERADDRESS1 = "turing@mailapp.com";
    private final String USERADDRESS2 = "vonneumann@mailapp.com";
    private final String USERADDRESS3 = "dijkstra@mailapp.com";
    private JLabel jLabel1;
    
    private JComboBox<String> userComboBox;
    private JButton loginButton;
    
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
            ConnectionManager.getInstance().setCurrentUser(user);
            //make the view show show inbox panel
            MailAppClientView.getInstance().showInboxPanel();
        }
    }  
    
    public UserSelectionPanel(){
        jLabel1 = new javax.swing.JLabel();
        userComboBox = new JComboBox<String>();
        loginButton = new JButton();

        jLabel1.setFont(new Font("Noto Sans", 0, 14)); 
        jLabel1.setText("Please select an user   ");
        add(jLabel1);

        userComboBox.setFont(new Font("Noto Sans", 0, 14)); 
        userComboBox.setModel(new DefaultComboBoxModel<>(new String[] { USERNAME1, USERNAME2, USERNAME3 }));
        add(userComboBox);
        
        loginButton.addActionListener(new loginButtonListener());
        loginButton.setText("Login");
        add(loginButton);
    }
}