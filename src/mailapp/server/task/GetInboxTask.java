/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import mailapp.EMail;
import mailapp.User;
import mailapp.server.MailFileHandler;

public class GetInboxTask implements Callable<ArrayList<EMail>>{
        User user;
        public GetInboxTask(User u){
            user = u;
        }

        @Override
        public ArrayList<EMail> call() {
            File file = new File("users/" + user.getAddress().replace("@mailapp.com","") + "/inbox.txt");
            RandomAccessFile in = null;
            ArrayList<EMail> list= new ArrayList<EMail>();
            int mailID;
            EMail mail;
            String toParse;
            boolean finish = false;
            try {
                in = new RandomAccessFile(file, "rw");
                FileLock lock = in.getChannel().lock();
                try {
                    while (!finish) {
                        toParse = in.readLine();
                        if(toParse == null || toParse.isEmpty())
                            break;
                        try{
                            mailID = Integer.parseInt(toParse);
                            mail = MailFileHandler.openMail(mailID);
                            System.out.println("opened mail "+mailID);
                            if(mail != null)
                                list.add(0, mail);
                        }
                        catch(NumberFormatException e){
                            finish = true;
                            System.out.println(toParse);
                        }
                    }
                } 
                finally {
                    lock.release();
                }
            } 
            catch(FileNotFoundException e) {
                e.printStackTrace();
            } 
            catch(IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    in.close();
                } 
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
    }
