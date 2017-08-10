/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.*;
import mailapp.EMail;
import mailapp.User;
import mailapp.server.task.GetInboxTask;
import mailapp.server.task.SendMailTask;

/**
 *
 * @author pauty
 */
public class MailServerImpl extends UnicastRemoteObject implements MailServer{
    
    private final int NUM_THREADS = 10;
    private Executor exec;
    private int nextMailID;
    private LogUpdater logUpdater;
    
    private class LogUpdater extends Observable{
        public void updateLog(String s){
            this.setChanged();
            this.notifyObservers(s);
        }
    }
    
    public MailServerImpl() throws RemoteException{
        super();
        exec = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            File file = new File("settings/mailID.txt");
            Scanner scan = new Scanner(file);
            nextMailID = scan.nextInt();
            scan.close();
        } catch (FileNotFoundException ex) {
            System.out.println("error opening setting");
        }
        logUpdater = new LogUpdater();
        
    }
    
    @Override
    public ServerMessage sendMail(EMail mail) throws RemoteException {
        System.out.println("server is sending mail");
        
        ServerMessage msg;
        ArrayList<User> receivers = mail.getReceivers();
        ArrayList<User> invalidReceivers = new ArrayList<User>();
        File file;
        for(int i = 0; i < receivers.size(); i++){
            file = new File("users/" + receivers.get(i).getAddress().replace("@mailapp.com", ""));
            if(!(file.exists() && file.isDirectory()))
                invalidReceivers.add(receivers.get(i));
        }
        
        if(invalidReceivers.isEmpty()){
            setMailID(mail);
            FutureTask<Boolean> ft = new FutureTask<>(new SendMailTask(mail));
            exec.execute(ft);
            Boolean res = false;
            try {
                res = ft.get();
            } catch (InterruptedException ex) { 
                System.out.println("Interrupred exception in sendMail");
            } catch (ExecutionException ex) {
                 System.out.println("execution exception in sendMail");
                 ex.printStackTrace();
            }

            
            msg = new ServerMessage();
        }
        else{
            msg = new ServerMessage(invalidReceivers);
        }
        System.out.println("server has finished sending");
        
        return msg;
    }
    
    @Override
    public ServerMessage deleteMail(User user, ArrayList<Integer> toDelete) throws RemoteException{
        return null;
    }

    @Override
    public ServerMessage getUserInbox(User user, int lastPulledID) throws RemoteException{
        logUpdater.updateLog("server is gettin user inbox\n");
        FutureTask<ServerMessage> ft = new FutureTask<>(new GetInboxTask(user, lastPulledID));
        exec.execute(ft);
        ServerMessage res = null;
        try {
            res = ft.get();
        } catch (InterruptedException ex) { 
            System.out.println("Interrupred exception in getUserInbox");
        } catch (ExecutionException ex) {
             System.out.println("execution exception in getUserInbox");
             ex.printStackTrace();
        }
        logUpdater.updateLog("server has finished\n");
        return res;
    }
    
    public void addLogObserver(Observer o){
        logUpdater.addObserver(o);
    }
    
    private synchronized void setMailID(EMail mail){
        mail.setID(nextMailID);
        nextMailID++;
        MailFileHandler.saveMail(mail);
    }
    
    public void destroy(){
        File file = new File("settings/mailID.txt");
        try {
            PrintWriter out = new PrintWriter(file);
            out.println("" + nextMailID);
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println("error not found");
        }
    }
    
}
