/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.*;
import mailapp.EMail;
import mailapp.User;
import mailapp.server.task.AddFolderMailsTask;
import mailapp.server.task.DeleteFolderMailsTask;
import mailapp.server.task.ReadFolderMailsTask;
import mailapp.server.task.SendMailTask;

/**
 *
 * @author pauty
 */
public class MailServerImpl implements MailServer{
    
    private final int NUM_THREADS = 10;
    private ExecutorService exec;
    private static int nextMailID;
    private LogUpdater logUpdater;
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
    public ServerMessage getFolderMails(User user, String folderName, List<Integer> pulled){
        //logUpdater.updateLog("server is gettin user inbox\n");
        FutureTask<ServerMessage> ft = new FutureTask<>(new ReadFolderMailsTask(user, folderName, pulled));
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
    public void sendMail(EMail mail){
        System.out.println("server is sending mail");
        
        ArrayList<User> receivers = mail.getReceivers();
        ArrayList<User> invalidReceivers = new ArrayList<User>();
        File file;
        
        for(int i = 0; i < receivers.size(); i++){
            file = new File("users/" + receivers.get(i).getAddress().replace("@mailapp.com", ""));
            if(!(file.exists() && file.isDirectory()))
                invalidReceivers.add(receivers.get(i));
        }
        
        setMailID(mail);
        MailFileHandler.saveMail(mail);
        
        ArrayList<Integer> id = new ArrayList<Integer>();
        id.add(mail.getID());
        Runnable addTask = new AddFolderMailsTask(mail.getSender(), "sent", id);
        exec.execute(addTask);
        
        Runnable sendTask;
        if(invalidReceivers.isEmpty()){  
            sendTask = new SendMailTask(mail);  
        }
        else{
            EMail errorMail = createErrorMail(mail, invalidReceivers);
            MailFileHandler.saveMail(errorMail);
            sendTask = new SendMailTask(errorMail);
        }
        exec.execute(sendTask);  
        
        System.out.println("server has finished sending");
    }
    
    @Override
    public ServerMessage deleteFolderMails(User user, String folderName, List<Integer> toDelete){
        ServerMessage msg = null;
        
        Runnable deleteTask = new DeleteFolderMailsTask(user, folderName, toDelete);
        Runnable addTask = new AddFolderMailsTask(user, "deleted", toDelete);
        exec.execute(deleteTask);
        exec.execute(addTask);
        
        msg = new ServerMessage(ServerMessage.Type.DELETE_SUCCESS);
  
        return msg;
    }
    
    private EMail createErrorMail(EMail mail, List<User> invalidReceivers){
        User sender = new User("Server Error Message", " ");
        ArrayList<User> receivers = new ArrayList<User>();
        receivers.add(mail.getSender());
        String subject = "Fatal error sending mail";
        String body = "A fatal error occurred while sending your mail.\n" + 
                   "Mail server could not resolve the following addresses: \n\n" +
                    User.printUserAddressesList(invalidReceivers, ", ") + "\n\n" +
                    "Please check for errors and retry." + 
                    "\n\n----------------------------------------------------------------\n\n" +
                    "original message:\n\n" +
                    "Subject: " + mail.getSubject() + "\n" +
                    "From: " + mail.getSender().getAddress() + "\n" +
                    "To: " + User.printUserAddressesList(mail.getReceivers(), ", ") + "\n" +
                    "Date: " + mail.getDateString("dd/MM/yyyy   HH:mm:ss") + "\n\n";
        String[] bodyLines = mail.getBody().split("\n");
        for(int i = 0; i < bodyLines.length; i++){
            body += "|   " + bodyLines[i] + "\n";
        }
        EMail errorMail = new EMail(-99, sender, receivers, subject, body, new Date(), 0, -1);
        setMailID(errorMail);
        return errorMail;
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
        
        //close the connection    
        try {
            registry.unbind("MailServer");
            UnicastRemoteObject.unexportObject(this, true);
            UnicastRemoteObject.unexportObject(registry, true);
            
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NotBoundException ex) {
            ex.printStackTrace();
        }   
        
        try {
            Thread.sleep(2000); //wait for tasks to complete
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        //do not accept new task requests
        exec.shutdown();
        try {
            exec.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace(); 
        }
        
        //save the current mail ID
        File file = new File("settings/mailID.txt");
        try {
            PrintWriter out = new PrintWriter(file);
            out.println("" + nextMailID);
            out.close();
            success = true;
        } catch (FileNotFoundException ex) {
            System.out.println("error not found");
        }
 
        System.out.println("shut down");
        
    }
    
}
