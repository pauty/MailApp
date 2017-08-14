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
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import mailapp.User;
import mailapp.server.FileLocker;

/**
 *
 * @author pauty
 */

public class DeleteMailTask implements Callable<Boolean>{
        User user;
        ArrayList<Integer> toDelete;
        public DeleteMailTask(User u, ArrayList<Integer> ids){
            user = u;
            toDelete = ids;     
        }

        @Override
        public Boolean call() {
            File filein = new File("users/" + user.getAddress().replace("@mailapp.com","") + "/inbox.txt");
            File fileout = new File("users/" + user.getAddress().replace("@mailapp.com","") + "/inbox.tmp");
            Scanner scanner = null;
            PrintWriter writer = null;
            int mailID;
            Lock lock = null;
            try {
                scanner = new Scanner(filein);
                writer = new PrintWriter(fileout);
                lock = FileLocker.getInstance().getLockForUser(user.getAddress());
                lock.lock();
                try {
                    while(scanner.hasNextInt()) {
                        try {
                            mailID = scanner.nextInt();
                            if(!toDelete.contains(mailID)){
                                writer.println(mailID);
                            }
                        }
                        catch(NumberFormatException e){
                            System.out.println("Erroneous parse:");
                        }
                    } 
                } 
                finally {
                    
                }
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
            filein.delete();
            boolean renamed = fileout.renameTo(filein);
            
            return renamed;
        }
    }