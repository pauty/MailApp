
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
        logUpdater = new LogUpdater();  
    }
    
    public boolean start(){ 
        logUpdater.updateLog("> Starting mail server...\n");
        
        exec = Executors.newFixedThreadPool(NUM_THREADS);
        
        File file = new File("settings/mailID.txt");
        try {
            logUpdater.updateLog("> Initializing next mail ID...");
            Scanner scan = new Scanner(file);
            nextMailID = scan.nextInt();
            scan.close();
            logUpdater.updateLog(" DONE.\n");
        } catch (FileNotFoundException ex) {
            logUpdater.updateLog("\n> !! ERROR - Could not open file: "+ file.getName() + "\n");
            return false;
        }
       
        try {
            logUpdater.updateLog("> Setting up connection...");
            MailServer stub = (MailServer) UnicastRemoteObject.exportObject(this, 6667);
            registry = LocateRegistry.createRegistry(6667);
            registry.rebind("MailServer", stub); 
            logUpdater.updateLog(" DONE.\n");
        } catch (RemoteException e) {
            logUpdater.updateLog("\n> !! ERROR - Remote exception occurred while exporting RMI object.\nCould not start server.\n");
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    @Override
    public ServerMessage getFolderMails(User user, String folderName, List<Integer> pulled){
        FutureTask<ServerMessage> ft = new FutureTask<>(new ReadFolderMailsTask(user, folderName, pulled));
        exec.execute(ft);
        ServerMessage res = null;
        try {
            res = ft.get();
            logUpdater.updateLog("> Update \"" + folderName + "\" folder request from user "+user.getAddress() + " successfully completed.\n");
        } catch (InterruptedException ex) { 
            System.out.println("Interrupred exception in getUserInbox");
            logUpdater.updateLog("> !! ERROR - Interrupted while updating \"" + folderName + "\" folder, user: "+user.getAddress() + "\n");
        } catch (ExecutionException ex) {
             System.out.println("execution exception in getUserInbox");
             logUpdater.updateLog("> !! ERROR - Exception while updating \"" + folderName + "\" folder, user: "+user.getAddress() + "\n");
        }
        
        return res;
    }
    
    @Override
    public void sendMail(EMail mail){
        
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
        Runnable addTask = new AddFolderMailsTask(mail.getSender(), SENT_FOLDERNAME, id);
        exec.execute(addTask);
        
        Runnable sendTask;
        if(invalidReceivers.isEmpty()){  
            sendTask = new SendMailTask(mail);  
            logUpdater.updateLog("> Sending Email #" + mail.getID() + "; sender: "+ mail.getSender().getAddress() + "\n");
        }
        else{
            EMail errorMail = createErrorMail(mail, invalidReceivers);
            MailFileHandler.saveMail(errorMail);
            sendTask = new SendMailTask(errorMail);
            logUpdater.updateLog("> !! ERROR - Could not send Email #" + mail.getID() + " due to invalid receiver address\n");
        }
        exec.execute(sendTask);  
    }
    
    @Override
    public void deleteFolderMails(User user, String folderName, List<Integer> toDelete){
        FutureTask<List<Integer>> deleteTask = new FutureTask<>(new DeleteFolderMailsTask(user, folderName, toDelete));
        exec.execute(deleteTask);
        //since concurrent deletion can happen in different clients,
        //we must know what mails we actually deleted to avoid adding duplicated ids in delete list
        List<Integer> actuallyDeleted;
        try {
            logUpdater.updateLog("> Deleting "+ toDelete.size() + "mail(s) from " + user.getAddress() + "  \"" + folderName + "\" folder.\n");
            actuallyDeleted = deleteTask.get();
            if(!folderName.equals(DELETED_FOLDERNAME) && !actuallyDeleted.isEmpty()){
                Runnable addTask = new AddFolderMailsTask(user, DELETED_FOLDERNAME, actuallyDeleted);
             exec.execute(addTask);
            } 
        } catch (InterruptedException ex) { 
            System.out.println("Interrupred exception in getUserInbox");
            logUpdater.updateLog("> !! ERROR - Interrupted while deleting from \"" + folderName + "\" folder, user: "+user.getAddress() + "\n");
        } catch (ExecutionException ex) {
             System.out.println("execution exception in getUserInbox");
             logUpdater.updateLog("> !! ERROR - Exception while deleting from \"" + folderName + "\" folder, user: "+user.getAddress() + "\n");
        }
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
    
    public boolean shutdown(){
        boolean success = true;
        logUpdater.updateLog("\n> Server started shutdown procedure.\n");
        //close the connection    
        try {
            logUpdater.updateLog("> Closing connection...");
            registry.unbind("MailServer");
            UnicastRemoteObject.unexportObject(this, true);
            UnicastRemoteObject.unexportObject(registry, true);
            logUpdater.updateLog(" DONE.\n");
        } catch (RemoteException ex) {
            logUpdater.updateLog("\n> !! ERROR - Remote exception occurred while unexporting RMI server.\n");
            ex.printStackTrace();
            success = false;
        } catch (NotBoundException ex) {
            logUpdater.updateLog("\n> !! ERROR - NotBoundException occurred while unexporting RMI server.\n");
            ex.printStackTrace();
            success = false;
        }   
        
        try {
            logUpdater.updateLog("> Waiting for tasks to complete... ");
            Thread.sleep(2000); //wait for tasks to complete
            logUpdater.updateLog(" DONE.\n");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            success = false;
            logUpdater.updateLog("\n> !! ERROR - Interrupted exception occurred while waiting for tasks to complete.\n");
        }
        
        //do not accept new task requests
        exec.shutdown();
        try {
            logUpdater.updateLog("> Shutting down task executor...");
            exec.awaitTermination(2000, TimeUnit.MILLISECONDS);
            logUpdater.updateLog(" DONE.\n");
        } catch (InterruptedException ex) {
            logUpdater.updateLog("\n> !! ERROR - Interrupted exception occurred while shutting down task executor.\n");
            ex.printStackTrace(); 
            success = false;
        }
        
        //save the current mail ID
        File file = new File("settings/mailID.txt");
        try {
            PrintWriter out = new PrintWriter(file);
            out.println("" + nextMailID);
            out.close();
            success = true;
            logUpdater.updateLog("> Next mail ID "+ nextMailID + " saved on file successfully.\n");
        } catch (FileNotFoundException ex) {
            success = false;
            logUpdater.updateLog("> !! ERROR - Could not locate file for saving next mail ID: "+ nextMailID +"\n");
        }
        
        if(success)
            logUpdater.updateLog("\n> Server was shut down successfully. Please clik exit again to close application.\n\n");
        
        return success;
    }
    
}
