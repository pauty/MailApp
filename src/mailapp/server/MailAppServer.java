/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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

        MailAppServerView view = new MailAppServerView();
        view.setVisible(true);
        
        /*
        try { 
            //special exception handler for registry creation
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } 
        catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }*/
        
        try{
           MailServerImpl server;
           server = new MailServerImpl();
           view.setServer(server);
           server.addLogObserver(view);
           
           server.start();

        /*   Naming.rebind("//127.0.0.1/MailServer", server);
           System.out.println("\nServer: " + server);
           System.out.println("PeerServer bound in registry");*/
        }
        catch(RemoteException e) {
            e.printStackTrace();
        }/* catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
    }
}
