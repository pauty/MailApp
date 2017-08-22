/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import mailapp.User;
import mailapp.server.FileLocker;

/**
 *
 * @author pauty
 */

public class DeleteFolderMailsTask implements Callable<List<Integer>>{
    User user;
    String folderName;
    List<Integer> toDelete;
    public DeleteFolderMailsTask(User u, String folder, List<Integer> ids){
        user = u;
        folderName = folder;
        toDelete = new ArrayList<Integer>(ids);     
    }

    @Override
    public List<Integer> call() {
        File filein = new File("users/" + user.getAddress().replace("@mailapp.com","") + "/" + folderName + ".txt");
        File fileout = new File("users/" + user.getAddress().replace("@mailapp.com","") + "/" + folderName + ".tmp");
        Scanner scanner = null;
        PrintWriter writer = null;
        int mailID;
        Lock lock = null;
        ArrayList<Integer> actuallyDeleted = new ArrayList<Integer>();
        try {
            scanner = new Scanner(filein);
            writer = new PrintWriter(fileout);
            lock = (FileLocker.getInstance().getLockForUser(user.getAddress() + "-" + folderName)).writeLock();
            lock.lock();
            while(scanner.hasNextInt()) {
                mailID = scanner.nextInt();
                if(!toDelete.contains(mailID)){
                    writer.println(mailID);
                }
                else{
                    actuallyDeleted.add(mailID);
                }
            } 
            filein.delete();
            fileout.renameTo(filein);
        } 
        catch(FileNotFoundException e) {
            e.printStackTrace();
        } 
        finally {
            if(scanner != null)
                scanner.close();
            if(writer != null)
                writer.close();
            if(lock != null)
            lock.unlock();
        }
        return actuallyDeleted;
    }     
}