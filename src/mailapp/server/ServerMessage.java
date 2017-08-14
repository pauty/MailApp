/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.io.Serializable;
import java.util.ArrayList;
import mailapp.EMail;


/**
 *
 * @author pauty
 */
public class ServerMessage implements Serializable{
    private Type type;
    public enum Type{
        UPDATE_INBOX_SUCCESS,
        UPDATE_INBOX_ERROR,
        DELETE_SUCCESS,
        DELETE_ERROR,
        SEND_MAIL_SUCCESS,
        INVALID_USER_ERROR,
        GENERIC_ERROR,
    }
    ArrayList<EMail> inboxList = null;
    int serverInboxSize = -1;
    int lastPulledID = -1;
    String errorMessage = null;
    
    public ServerMessage(Type t){
        type = t;
    }
    
    public ServerMessage(String errorMsg){
        type = Type.GENERIC_ERROR; 
        errorMessage = errorMsg;
    }
    
    public Type getType() {
        return type;
    }
        
    public ServerMessage(ArrayList<EMail> mailList, int lastPulled, int size){
        type = Type.UPDATE_INBOX_SUCCESS;
        inboxList = new ArrayList<EMail>(mailList);
        lastPulledID = lastPulled;
        serverInboxSize = size;
    }
    
    public ArrayList<EMail> getInboxList(){
        return inboxList;
    }
    
    public int getServerInboxSize(){
        return serverInboxSize;
    }
    
    public int getLastPulledID(){
        return lastPulledID;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
