/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server.task;

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

public class GetInboxTask implements Callable<ServerMessage>{
        User user;
        int lastPulledID;
        int clientInboxSize;
        public GetInboxTask(User u, int lastPulled, int inboxSize){
            user = u;
            lastPulledID = lastPulled;
            
        }

        @Override
        public ServerMessage call() {
            File file = new File("users/" + user.getAddress().replace("@mailapp.com","") + "/inbox.txt");
            Scanner scan = null;
            ArrayList<EMail> list= new ArrayList<EMail>();
            int mailID;
            EMail mail;
            int inboxSize = 0;
            int newLastPulled = lastPulledID;
            boolean mustAdd = false;
            try {
                scan = new Scanner(file);
                Lock lock = FileLocker.getInstance().getLockForUser(user.getAddress());
                lock.lock();
                try {
                    while(scan.hasNextInt()) {
                        //toParse = scan.nextLine();
                        try {
                            mailID = scan.nextInt();//Integer.parseInt(toParse);
                            if(lastPulledID < 0 || mustAdd){
                                mail = MailFileHandler.openMail(mailID);
                                System.out.println("opened mail "+mailID);
                                if(mail != null)
                                    list.add(0, mail);
                                newLastPulled = mailID;
                            }
                            if(mailID == lastPulledID){
                                //last pulled ID found; every
                                // next mail must be pulled
                                mustAdd = true;
                            }
                            inboxSize++;
                        }
                        catch(NumberFormatException e){
                            System.out.println("Erroneous parse:");
                        }
                    }
                    if(lastPulledID >= 0 && mustAdd == false){
                        //mi aspettavo di trovare almeno l'id
                        //pullato in precedenza, ma non l'ho incontrato:
                        //un altro client ha fatto delete, setta inboxSize a -1 per forzare update
                        inboxSize = -1;
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
            return new ServerMessage(list, newLastPulled, inboxSize);
        }
    }
