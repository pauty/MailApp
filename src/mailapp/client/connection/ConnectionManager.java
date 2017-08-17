
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
    public enum LastAction{
        INBOX_UPDATE,
        OTHER_UPDATE,
        DISCONNECT
    }
    public static String INBOX = "inbox"; 
    public static String SENT = "sent"; 
    public static String DELETED = "deleted"; 
    private final static int DEFAULT_PULL_INTERVAL = 5000;
    private final static int DEFAULT_SERVER_PORT = 6667;
    private final static String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
    private int pullInterval = DEFAULT_PULL_INTERVAL ;
    private String[] folderNames = { INBOX , SENT, DELETED };
    private int[] updatePattern = {0, 1, 0, 2};
    private int currentFolder = 0;
    private MailServer mailServer = null;
    private Registry registry = null;
    private User currentUser = null;
    private HashMap<String , List<EMail>> listMap = null;
    private ArrayList<EMail> newInboxMails= new ArrayList<EMail>();
    private boolean initDone = false;
    
    private MailPullLoop pullLoop = null;
    
    private class MailPullLoop implements Runnable{
        private volatile boolean isRunning = true;
        private final Object lockObj = new Object();
        @Override
        public void run(){
            while(isRunning){
                
                if(!initDone){
                    for(int i = 0; i < folderNames.length; i++)
                        updateFolderMails(folderNames[i]);
                    initDone = true;
                }
                else{
                    updateFolderMails(folderNames[updatePattern[currentFolder]]);
                    currentFolder++;
                    currentFolder = currentFolder % updatePattern.length;
                }
                
                synchronized (lockObj){
                    try{
                        lockObj.wait(pullInterval);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        
        public void wakeup(){
            synchronized (lockObj){
                lockObj.notify();
            }
        }
        
        public void stop(){
            isRunning = false;
        }    
    }
    
    public ConnectionManager(){
        listMap = new HashMap<String, List<EMail>>();
        listMap.put("inbox", new ArrayList<EMail>());
        listMap.put("sent", new ArrayList<EMail>());
        listMap.put("deleted", new ArrayList<EMail>());     
    }
        
    public boolean connect(){
        boolean success = true;
        try{
            registry = LocateRegistry.getRegistry(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);
            mailServer = (MailServer)registry.lookup("MailServer");
        }
        catch(NotBoundException | RemoteException e){
            System.out.println("Problema: " + e.getMessage());
            success = false;
        }
        if(success){
            pullLoop = new MailPullLoop();
            new Thread(pullLoop).start();
        }
        initDone = false;
        currentFolder = 0;
        return success;
    }
    
    public void disconnect(){
        if(pullLoop != null){
            pullLoop.stop(); 
            pullLoop.wakeup();
            pullLoop = null;
        }
        registry = null;
        mailServer = null;
        listMap.get("inbox").clear();
        listMap.get("sent").clear();
        listMap.get("deleted").clear();
    }
    
    private synchronized void updateFolderMails(String folderName){
        ServerMessage msg = null;

        System.out.println("client asking for inbox");
        
        if(mailServer != null && currentUser != null){
            ConnectionManager.LastAction action = ConnectionManager.LastAction.OTHER_UPDATE;
            
            ArrayList<Integer> pulledIDs = new ArrayList<Integer>();
            List<EMail> mailList = listMap.get(folderName);
            for(int i = 0; i < mailList.size(); i++){
                pulledIDs.add(mailList.get(i).getID());
            }

            try{
                msg = mailServer.getFolderMails(currentUser, folderName, pulledIDs );

                ArrayList<EMail> newList = (ArrayList<EMail>)msg.getFolderMailList();
                for(int i = 0; i < newList.size(); i++){
                    mailList.add(0 , newList.get(i));
                }
                if(folderName.equals(INBOX) && initDone){ //init update does not show dialog
                    newInboxMails = newList;
                    action = ConnectionManager.LastAction.INBOX_UPDATE;
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
                notifyObservers(action); //notify to the gui it's time to update
                
            } catch (RemoteException ex) {
                System.out.println("ERROR get userinbox");
                ex.printStackTrace();
            }
        }     
    }
   
    public synchronized void sendMail(String to, String subject, String body, int inReplyTo){
        if(mailServer != null && currentUser != null){
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
    }
    
    public synchronized void deleteMail(String folderName, ArrayList<EMail> mails){
        if(mailServer != null && currentUser != null){
            
            ArrayList<Integer> ids = new ArrayList<Integer>();
            for(int i = 0; i< mails.size(); i++){
                ids.add(mails.get(i).getID());
            }
            try {
                mailServer.deleteFolderMails(currentUser, folderName, ids);
            } catch (RemoteException ex) {
                System.out.println("Error deleting mail !!!");
            }
            
            //local remove and add to deleted folder
            EMail mail;
            for(int i = 0; i < mails.size(); i++){
                mail = mails.get(i);
                listMap.get(folderName).remove(mail);
                if(!folderName.equals(DELETED))
                    listMap.get(DELETED).add(i, mail);
            }

            setChanged();
            notifyObservers(LastAction.OTHER_UPDATE);    
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
    
    public List<EMail> getFolderMails(String folderName){
        return listMap.get(folderName);
    }
    
    public String[] getFolderNames(){
        return folderNames;
    }
    
    public List<EMail> getNewInboxMails(){
        return newInboxMails;
    }
    
}
