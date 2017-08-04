
package mailapp;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author pauty
 */
public class EMail {
    private int ID;
    private User sender;
    private ArrayList<User> receivers;
    private String subject;
    private String body;
    private Date date;
    private int priority;
    private int replyTo;
    
    public EMail(int id, User from, ArrayList<User> to, String subj, String b, Date d, int p){
        ID = id;
        sender = from;
        receivers = new ArrayList<User>(to);
        subject = subj;
        body = b;
        date = d;
        priority = p;
    }
    
    public void setID(int id){
        ID = id;
    }

    public int getID() {
        return ID;
    }

    public User getSender() {
        return sender;
    }

    public ArrayList<User> getReceivers() {
        return receivers;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public int getPriority() {
        return priority;
    }
    
    
}
