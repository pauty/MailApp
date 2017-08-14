/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.*;
import mailapp.EMail;
import mailapp.User;
import mailapp.server.task.DeleteMailTask;
import mailapp.server.task.GetInboxTask;
import mailapp.server.task.SendMailTask;

/**
 *
 * @author pauty
 */
public class MailServerImpl  /*extends UnicastRemoteObject*/ implements MailServer{
    
    private final int NUM_THREADS = 10;
    private ExecutorService exec;
    private static int nextMailID;
    private LogUpdater logUpdater;
    private boolean isServerUp;
    private Registry registry;
    
    private class LogUpdater extends Observable{
        public void updateLog(String s){
            this.setChanged();
            this.notifyObservers(s);
        }
    }
    
    public MailServerImpl() throws RemoteException {
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
        isServerUp = true;
    }
    
    public void start(){
                
        try {
            MailServer stub = (MailServer) UnicastRemoteObject.exportObject(this, 6667);
            registry = LocateRegistry.createRegistry(6667);
            registry.rebind("MailServer", stub); 
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public ServerMessage getUserInbox(User user, int lastPulledID, int inboxSize){
        //logUpdater.updateLog("server is gettin user inbox\n");
        FutureTask<ServerMessage> ft = new FutureTask<>(new GetInboxTask(user, lastPulledID, inboxSize));
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
        //logUpdater.updateLog("server has finished\n");
        return res;
    }
    
    @Override
    public ServerMessage sendMail(EMail mail){
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
            MailFileHandler.saveMail(mail);
            
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
            msg = new ServerMessage(ServerMessage.Type.SEND_MAIL_SUCCESS);
        }
        else{
            msg = new ServerMessage("ee");
        }
        System.out.println("server has finished sending");
        
        return msg;
    }
    
    @Override
    public ServerMessage deleteMail(User user, ArrayList<Integer> toDelete){
        ServerMessage msg = null;
        
        FutureTask<Boolean> ft = new FutureTask<>(new DeleteMailTask(user, toDelete));
        exec.execute(ft);
        Boolean res = false;
        try {
            res = ft.get();
        } catch (InterruptedException ex) { 
            System.out.println("Interrupred exception in deleteMail");
        } catch (ExecutionException ex) {
             System.out.println("execution exception in deleteMail");
             ex.printStackTrace();
        }
        if(msg!=null)
            System.out.println(msg.getType());
        if(res){
            msg = new ServerMessage(ServerMessage.Type.DELETE_SUCCESS);
        }
        else{
            msg = new ServerMessage("eerrrror");
        }
        return msg;
    }
    
    public void addLogObserver(Observer o){
        logUpdater.addObserver(o);
    }
    
    private static synchronized void setMailID(EMail mail){
        mail.setID(nextMailID);
        nextMailID++;
    }
    
    public void shutdown(){
        boolean success = false;
        exec.shutdown();
        File file = new File("settings/mailID.txt");
        try {
            PrintWriter out = new PrintWriter(file);
            out.println("" + nextMailID);
            out.close();
            success = true;
        } catch (FileNotFoundException ex) {
            System.out.println("error not found");
        }
        
        try {
            registry.unbind("MailServer");
            UnicastRemoteObject.unexportObject(this, true);
            
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NotBoundException ex) {
            ex.printStackTrace();
        }   
        System.out.println("shut down");
        
    }
    
}
