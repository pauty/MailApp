
package mailapp.server.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author pauty
 * this tool clean up mails that have been completely removed from every user folder.
 */
public class MailCleaner {
    
    static final String MAIL_PATH = "mails/";
    static final String USER_PATH = "users/";
    static final String INBOX_FILENAME = "inbox.txt";
    static final String SENT_FILENAME = "sent.txt";
    static final String DELETED_FILENAME = "deleted.txt";
    static HashMap<String, Boolean> mailMap;
    
    public static void main(String[] args){
        
        mailMap = new HashMap<String, Boolean>();
    
        File[] mailFiles = new File(MAIL_PATH).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null. 
        
        //put all mail names (ids) in hasmap
        for (File mail : mailFiles) {
            if (mail.isFile()) {
                mailMap.put(mail.getName(), false); //mail are saved with names, as "17.txt"
            }
        }
       
       
        File[] userDirFiles = new File(USER_PATH).listFiles();
       
        //loop on users directories
        for (File dir : userDirFiles) {
            if (dir.isDirectory()) {
                readUserFolder(dir.getAbsolutePath() + "/" + INBOX_FILENAME);
                readUserFolder(dir.getAbsolutePath() + "/" + SENT_FILENAME);
                readUserFolder(dir.getAbsolutePath() + "/" + DELETED_FILENAME);
            }
        }
        
        //DELETE
        File toDelete;
        boolean done;
        Set<String> keys = mailMap.keySet();
        //delete mails in hashmap that were not found in any user folder
        for(String key : keys){
            if(!mailMap.get(key)){ //key associated to false
                toDelete = new File(MAIL_PATH + key);
                done = toDelete.delete();
                if(done){
                    System.out.println("Mail with id " + key + " successfully deleted.");
                }
                else{
                    System.out.println("Could not delete mail with id " + key + "." );
                }
            }
        }
    }
    
    public static void readUserFolder(String folderName){
        Scanner scan;
        try {
            scan = new Scanner(new File(folderName));
        } catch (FileNotFoundException ex) {
            System.out.println("Folder " + folderName + " not found. Proceeding." );
            return;
        }
        
        int ID;   
        while(scan.hasNextInt()){
            //if id was read, mark it in hashmap
            ID = scan.nextInt();
            System.out.println("Found mail " + ID + " in a user folder, must not be deleted" );
            mailMap.put(ID + ".txt", true);
        }       
        scan.close();
    }
    
}
