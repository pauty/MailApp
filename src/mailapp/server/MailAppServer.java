/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;
import java.rmi.RemoteException;
import mailapp.server.view.MailAppServerView;

/**
 *
 * @author pauty
 */
public class MailAppServer {
        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        try{
           //create and start the server
           MailServerImpl server;
           server = new MailServerImpl();
           //add the view
           MailAppServerView view = new MailAppServerView(server);
           view.setVisible(true);

        }
        catch(RemoteException e) {
            e.printStackTrace();
        }
    }
}
