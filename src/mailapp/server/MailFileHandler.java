/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import mailapp.EMail;
import mailapp.User;

/**
 *
 * @author pauty
 */
public class MailFileHandler {
    private final static String FORMAT_STRING = "dd/MM/yyyy HH:mm:ss";
    private final static DateFormat formatter = new SimpleDateFormat(FORMAT_STRING);
    
    public static EMail openMail(int mailID){
        File file = new File("mails/" + mailID + ".txt");
        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.out.println("Error opening mail: " + mailID);
            return null;
        }
        
        //sender
        String userStr = in.nextLine();
        String[] userData = userStr.split("#");
        User sender = new User(userData[0], userData[1]);
        //receivers
        ArrayList<User> receivers = new ArrayList<User>();
        String[] userListStr = in.nextLine().split(", ");
        for(int i = 0; i < userListStr.length; i++){
            userData = userListStr[i].split("#");
            receivers.add(new User(userData[0], userData[1]));
        }
        //subject 
        String subject = in.nextLine();
        //date
        Date date;
        try {
            date = formatter.parse(in.nextLine());
        } catch (ParseException ex) {
            System.out.println("Error parsing date for mail: " + mailID);
            return null;
        }
        //priority
        int priority = Integer.parseInt(in.nextLine());
        //inReplyTo
        int inReplyTo = Integer.parseInt(in.nextLine());
        //body
        String body = "";
        while(in.hasNextLine()){
            body += in.nextLine() + "\n";
        }
        EMail res = new EMail(mailID, sender, receivers, subject, body, date, priority, inReplyTo);
        
        return res;
    }
    
    public static void saveMail(EMail mail){
        File file = new File("mails/" + mail.getID() + ".txt");
        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
        } catch (IOException ex) {
            System.out.println("Error saving mail: " + mail.getID());
        }
        
        try{
            //sender
            String userStr = mail.getSender().getName() + "#" + mail.getSender().getAddress();
            out.println(userStr);
            //receivers
            ArrayList<User> receivers = mail.getReceivers();
            for(int i = 0; i < receivers.size() -1; i++){
                out.print(receivers.get(i).getName() + "#" + receivers.get(i).getAddress() + ", ");
            }
            out.println(receivers.get(receivers.size()-1).getName() + "#" + receivers.get(receivers.size()-1).getAddress());
            //subject 
            out.println(mail.getSubject());
            //date
            out.println(mail.getDateString(FORMAT_STRING));
            //priority
            out.println(mail.getPriority());
            //inReplyTo
            out.println(mail.getInReplyTo());
            //body
            out.println(mail.getBody());
        }
        catch(Exception e){
                
        }
        
    }
}
