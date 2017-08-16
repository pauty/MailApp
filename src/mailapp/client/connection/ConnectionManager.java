/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.client.connection;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import mailapp.EMail;
import mailapp.User;
import mailapp.server.ServerMessage;
import mailapp.server.MailServer;

/**
 *
 * @author pauty
 */ 
public class ConnectionManager extends Observable{
    
    private final static int DEFAULT_PULL_INTERVAL = 8000;
    private final static int DEFAULT_SERVER_PORT = 6667;
    private final static String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
    private int pullInterval = DEFAULT_PULL_INTERVAL ;
    private MailServer mailServer = null;
    private Registry registry = null;
    private User currentUser = null;
    private HashMap<String , List<EMail>> listMap;
    private String currentFolder = "inbox";
    
    public ConnectionManager(){
        listMap = new HashMap<String, List<EMail>>();
        listMap.put("inbox", new ArrayList<EMail>());
        listMap.put("sent", new ArrayList<EMail>());
        listMap.put("deleted", new ArrayList<EMail>());
        
               
        try{
            registry = LocateRegistry.getRegistry(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);
            mailServer = (MailServer) (registry.lookup("MailServer"));
        }
        catch(NotBoundException | RemoteException e){
            System.out.println("Problema: " + e.getMessage());
        }
    }
        
    public void connect(){
        new Thread(new MailPullLoop()).start();
    }
    
    private class MailPullLoop implements Runnable{
        private volatile boolean isRunning = true;
        private final Object lockObj = new Object();
        @Override
        public void run() {
            while(isRunning){
                updateFolderMails();
                synchronized (lockObj) {
                    try{
                        lockObj.wait(pullInterval);
                    } catch(InterruptedException e){
                        //Handle Exception
                    }
                }
            }
        }
        
        public void wakeup() {
            synchronized (lockObj) {
                lockObj.notify();
            }
        }
        
        public void stop(){
            isRunning = false;
        }    
    }
    
    private synchronized void updateFolderMails(){
        ServerMessage msg = null;

        System.out.println("client asking for inbox");
        
        if(mailServer != null && currentUser != null){
        
            ArrayList<Integer> pulledIDs = new ArrayList<Integer>();
            List<EMail> mailList = listMap.get(currentFolder);
            for(int i = 0; i < mailList.size(); i++){
                pulledIDs.add(mailList.get(i).getID());
            }

            try{
                msg = mailServer.getFolderMails(currentUser, currentFolder, pulledIDs );

                ArrayList<EMail> newList = (ArrayList<EMail>) msg.getFolderMailList();
                for(int i = 0; i < newList.size(); i ++){
                    mailList.add(i , newList.get(i));
                }
                
                ArrayList<Integer> remoteIDs = (ArrayList<Integer>) msg.getFolderIDList();
                ArrayList<EMail> toDelete = new ArrayList<EMail>();
                for(int i = 0; i < mailList.size(); i++){
                    if(!remoteIDs.contains(mailList.get(i).getID()))
                        toDelete.add(mailList.get(i));
                }
                for(int i = 0; i < toDelete.size(); i++){
                    mailList.remove(toDelete.get(i));
                }

                setChanged();
                notifyObservers(); //notify to the gui it's time to update
                
            } catch (RemoteException ex) {
                System.out.println("ERROR get userinbox");
                ex.printStackTrace();
            }
        }     
    }
   
    
    public synchronized boolean sendMail(String to, String subject, String body, int inReplyTo){
        if(mailServer != null){
            
            //sender
            User sender = this.currentUser;
            //receivers
            String[] receiversStr = to.split(",");
            ArrayList<User> receivers = new ArrayList<User>();
            for(int i = 0; i < receiversStr.length; i++){
                //"u" for unknown; in future server may resolve address into username
                receivers.add(new User("u",receiversStr[i].trim()));
            }
            //date
            Date date = new Date();
            //priority
            int priority = 0; 
            
            EMail mail = new EMail(-99, sender, receivers, subject, body, date, priority, inReplyTo);
            
            try {
                
                mailServer.sendMail(mail);
                
            } catch (RemoteException ex) {
                System.out.println("Error sending mail !!!");
            }
        }
        return true;
    }
    
    public synchronized void deleteMail(ArrayList<EMail> mails){
        ServerMessage msg = null;
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(int i = 0; i< mails.size(); i++){
            ids.add(mails.get(i).getID());
        }
        try {
            
            msg = mailServer.deleteFolderMails(currentUser, currentFolder, ids);
            
        } catch (RemoteException ex) {
            System.out.println("Error deleting mail !!!");
        }
        if(msg != null && msg.getType() == ServerMessage.Type.DELETE_SUCCESS){
            for(int i = 0; i< mails.size(); i++){
                listMap.get(currentFolder).remove(mails.get(i));
            }
            
            setChanged();
            notifyObservers();
        } 
    }
    
    public synchronized void setCurrentFolder(String folderName){
        currentFolder = folderName;
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
    
    public List<EMail> getFolderMails(String folderName){
        return listMap.get(folderName);
    }
    
    public synchronized List<EMail> getCurrentFolderMails(){
        return listMap.get(currentFolder);
    }
    
}
