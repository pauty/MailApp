/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server.task;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import mailapp.EMail;
import mailapp.User;
import mailapp.server.FileLocker;
import mailapp.server.MailFileHandler;
import mailapp.server.ServerMessage;

public class ReadFolderMailsTask implements Callable<ServerMessage>{
        User user;
        String folderName;
        List<Integer> alreadyPulled;
        public ReadFolderMailsTask(User u, String folder, List<Integer> pulled){
            user = u;
            folderName = folder;  
            alreadyPulled = new ArrayList<Integer>(pulled);
        }

        @Override
        public ServerMessage call() {
            File file = new File("users/" + user.getAddress().replace("@mailapp.com","") + "/" + folderName + ".txt");
            Scanner scan = null;
            ArrayList<EMail> mailList = new ArrayList<EMail>();
            ArrayList<Integer> idList = new ArrayList<Integer>();
            int mailID;
            EMail mail;
            try {
                scan = new Scanner(file);
                Lock lock = (FileLocker.getInstance().getLockForUser(user.getAddress()+ "-" + folderName)).readLock();
                lock.lock();
                try {
                    while(scan.hasNextInt()) {
                        mailID = scan.nextInt();
                        idList.add(mailID);
                        if(!alreadyPulled.contains(mailID)){
                            mail = MailFileHandler.openMail(mailID);
                            mailList.add(mail);
                        }     
                    }      
                } 
                finally {
                    lock.unlock();
                }
            } 
            catch(FileNotFoundException e) {
                e.printStackTrace();
            } 
            finally {
                if(scan != null)
                    scan.close();
            }
            return new ServerMessage(mailList, idList);
        }
    }
