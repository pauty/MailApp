
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
        ConnectionManager connectionManager = new ConnectionManager();
        
        //creates GUI
        MailAppClientView view = new MailAppClientView(connectionManager);
        view.setVisible(true);
    }
}
