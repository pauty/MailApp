/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.client;

import mailapp.client.connection.ConnectionManager;
import mailapp.client.view.MailAppClientView;

/**
 *
 * @author pauty
 */
public class MailAppClient {
        public static void main(String[] args) {
        
        //creates and start connection manager
        ConnectionManager.getInstance(); //init
        ConnectionManager.getInstance().connect();
        
        //creates GUI
        MailAppClientView.getInstance().setVisible(true);
    }
}
