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
import mailapp.server.ServerMessage;

/**
 *
 * @author pauty
 */ 
public class ConnectionManager extends Observable{
    
    private final static int DEFAULT_PULL_INTERVAL = 8000;
    private static ConnectionManager singleInstance = null;
    private User currentUser = null;
    private int pullInterval = DEFAULT_PULL_INTERVAL ;
    private ArrayList<EMail> inboxMailList;
    Context namingContext = null;
    private MailServer mailServer = null;
    private int lastPulledID = -1;
    
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
                updateCurrentUserInbox();
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
    
    private void updateCurrentUserInbox(){
        ServerMessage msg;
        
        setPullInterval(DEFAULT_PULL_INTERVAL);
        
        try {
            System.out.println("client asking for inbox");
            if(mailServer != null && currentUser != null){
                
                synchronized(this){
                    msg = mailServer.getUserInbox(currentUser, lastPulledID);
                }
                
                if(lastPulledID < 0){
                    inboxMailList = msg.getInboxList();
                }
                else{
                    ArrayList<EMail> newList = msg.getInboxList();
                    for(int i = 0; i < newList.size(); i ++){
                        inboxMailList.add(i , newList.get(i));
                    }
                }
                if(msg.getInboxSize() < inboxMailList.size()){ 
                    //if the remote inbox is smaller than the local inbox after the update,
                    // a delete event occurred on another istance of client; ask for a fast update
                    lastPulledID = -1; // get a completely new list of mail
                    setPullInterval(400); //immediately update again
                }
                else{
                    lastPulledID = msg.getLastPulledID();
                }
                setChanged();
                notifyObservers(); //notify to the gui it's time to update

            }
        } 
        catch (RemoteException ex) {
            System.out.println("ERROR get userinbox");
            ex.printStackTrace();
        }
    }
   
    
    public void sendMail(EMail mail){
        if(mailServer != null){
            try {
                synchronized(this){
                    mailServer.sendMail(mail);
                }
            } catch (RemoteException ex) {
                System.out.println("Error sending mail !!!");
            }
        }
    }
    
    public void deleteMail(ArrayList<Integer> mailIDs){
        
    }
}
