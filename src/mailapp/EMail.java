
package mailapp;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author pauty
 */
public class EMail implements Serializable, Comparable{
    private int ID;
    private User sender;
    private ArrayList<User> receivers;
    private String subject;
    private Date date;
    private int priority;
    private int inReplyTo;
    private String body;
    
    public enum Type{
        NEW,
        FORWARD,
        REPLY,
        REPLY_ALL
    }
    
    public EMail(int id, User from, ArrayList<User> to, String subj, String b, Date d, int p, int replyID){
        ID = id;
        sender = from;
        receivers = new ArrayList<User>(to);
        subject = subj;
        body = b;
        date = d;
        priority = p;
        inReplyTo = replyID;
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
    
    public String getDateString(String format){
        // "dd/MM/yyyy   HH:mm:ss"
        Format formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    
    public int getInReplyTo(){
        return inReplyTo;
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof EMail))
            return false;
        EMail mail = (EMail)o;
        return (this.ID == mail.getID());
    }
    
    //comparable by date
    @Override
    public int compareTo(Object t) {        
        EMail mail = (EMail)t;
        return this.date.compareTo(mail.getDate());
    }
    
}
