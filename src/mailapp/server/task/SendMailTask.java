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

public class SendMailTask implements Callable<Boolean>{
        EMail toSend;
        public SendMailTask(EMail mail){
            toSend = mail;
        }

        @Override
        public Boolean call() {
            File file = null; 
            RandomAccessFile out = null; 
            ArrayList<User> receivers = toSend.getReceivers();
            for(int i = 0; i < receivers.size(); i ++){
                try {
                    file = new File("users/" + receivers.get(i).getAddress().replace("@mailapp.com","") + "/inbox.txt");
                    out = new RandomAccessFile(file, "rw");
                    FileLock lock = out.getChannel().lock();
                    try {
                        out.writeChars(toSend.getID() + "\n");
                    }
                    catch(IOException e){
                        e.printStackTrace();
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
                        out.close();
                    } 
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }  
    }
