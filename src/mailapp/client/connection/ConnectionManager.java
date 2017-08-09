/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.client.connection;

import java.rmi.*;
import java.util.ArrayList;
import java.util.Observable;
import javax.naming.*;
import mailapp.EMail;
import mailapp.User;
import mailapp.server.MailServer;

/**
 *
 * @author pauty
 */ 
public class ConnectionManager extends Observable{
    
    private static ConnectionManager singleInstance = null;
    private User currentUser = null;
    private int pullInterval = 5000;
    private ArrayList<EMail> inboxMailList;
    Context namingContext = null;
    private MailServer mailServer = null;
    
    private ConnectionManager(){
        try{
            namingContext = new InitialContext();
            mailServer = (MailServer)Naming.lookup("//127.0.0.1/MailServer");
        }
        catch(Exception e){
            System.out.println("Problema: " + e.getMessage());
        }
    }
        
    public void connect(){
        new Thread(new MailPullLoop()).start();
    }
    
    public static ConnectionManager getInstance(){
        if(singleInstance == null)
            singleInstance = new ConnectionManager();
        return singleInstance;
    }
    
    private class MailPullLoop implements Runnable{
        private boolean isRunning = true;
        @Override
        public void run() {
            while(isRunning){
                try {
                    System.out.println("client asking for inbox");
                    if(mailServer != null && currentUser != null){
                        inboxMailList = mailServer.getUserInbox(currentUser);
                        setChanged();
                        notifyObservers(new ConnectionManagerMessage(ConnectionManagerMessage.Type.INBOX_RESPONSE, inboxMailList));
                    }
                } 
                catch (RemoteException ex) {
                    System.out.println("ERROR get userinbox");
                    ex.printStackTrace();
                }
                try {
                    Thread.sleep(pullInterval);
                } catch (InterruptedException ex) {
                }
            }
        }
        
        public void stop(){
            isRunning = false;
        }    
    }
    
    public void setCurrentUser(User u){
        currentUser = u;
    }
    
    public User getCurrentUser(){
        return currentUser;
    }
    
    public void setPullInterval(int millisec){
        pullInterval = millisec;
    }
    
    public ArrayList<EMail> getInboxMailList(){
        return inboxMailList;
    }
    
    public void getUserInbox(){
        
    }
    
    public void sendMail(EMail mail){
        
    }
    
    public void deleteMail(ArrayList<Integer> mailIDs){
        
    }
    
    
    

}
