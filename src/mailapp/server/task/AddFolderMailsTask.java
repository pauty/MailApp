
package mailapp.server.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import mailapp.User;
import mailapp.server.FileLocker;

public class AddFolderMailsTask implements Runnable{
        User user;
        String folderName;
        List<Integer> toAdd;
        public AddFolderMailsTask(User u, String folder, List<Integer> ids){
            user = u;
            folderName = folder;  
            toAdd = new ArrayList<Integer>(ids);
        }

        @Override
        public void run() {
            File file = new File("users/" + user.getAddress().replace("@mailapp.com","") + "/" + folderName + ".txt");
            PrintWriter out = null;
            try {
                if(!file.exists()){
                    file.createNewFile();
                }
                out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                Lock lock = (FileLocker.getInstance().getLockForUser(user.getAddress()+ "-" + folderName)).writeLock();
                lock.lock();
                try {
                    for(int i = toAdd.size()-1; i >= 0; i--){
                        out.println(toAdd.get(i));
                    }    
                } 
                finally {
                    lock.unlock();
                }
            } 
            catch(FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } 
            finally {
                if(out != null)
                    out.close();
            }
        }
    }