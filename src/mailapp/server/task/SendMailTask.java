/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import mailapp.EMail;
import mailapp.User;
import mailapp.server.FileLocker;

public class SendMailTask implements Callable<Boolean>{
        EMail toSend;
        public SendMailTask(EMail mail){
            toSend = mail;
        }

        @Override
        public Boolean call() {
            File file = null; 
            PrintWriter out = null; 
            Lock lock = null;
            ArrayList<User> receivers = toSend.getReceivers();
            for(int i = 0; i < receivers.size(); i++){
                try {
                    file = new File("users/" + receivers.get(i).getAddress().replace("@mailapp.com","") + "/inbox.txt");
                    out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                    lock = FileLocker.getInstance().getLockForUser(receivers.get(i).getAddress());
                    lock.lock();
                    out.println(toSend.getID());
                }
                catch(FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } 
                finally {
                    if(lock != null)
                        lock.unlock();
                    if(out != null)
                    out.close();
                }
            }
            return true;
        }  
    }