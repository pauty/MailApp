/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.io.Serializable;
import java.util.ArrayList;
import mailapp.EMail;
import mailapp.User;

/**
 *
 * @author pauty
 */
public class ServerMessage implements Serializable{
    private Type type;
    public enum Type{
        SUCCESS,
        GENERIC_ERROR,
        INVALID_USER_ERROR
    }
    ArrayList<User> invalidUserList = null;
    ArrayList<EMail> inboxList = null;
    int inboxSize = -1;
    int lastPulledID = -1;
    String errorMessage = null;
    
    public ServerMessage(){
        type = Type.SUCCESS;
    }
    
    public ServerMessage(String errorMsg){
        type = Type.GENERIC_ERROR; 
        errorMessage = errorMsg;
    }
    
    public ServerMessage(ArrayList<User> userList){
        type = Type.INVALID_USER_ERROR;
        invalidUserList = new ArrayList<User>(userList);
    }
    
    public Type getType() {
        return type;
    }
        
    public ServerMessage(ArrayList<EMail> mailList, int lastPulled, int size){
        type = Type.SUCCESS;
        inboxList = new ArrayList<EMail>(mailList);
        lastPulledID = lastPulled;
        inboxSize = size;
    }

    public ArrayList<User> getInvalidUserList() {
        return invalidUserList;
    }
    
    public ArrayList<EMail> getInboxList(){
        return inboxList;
    }
    
    public int getInboxSize(){
        return inboxSize;
    }
    
    public int getLastPulledID(){
        return lastPulledID;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
