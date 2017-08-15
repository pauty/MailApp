/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.util.List;
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
    List<EMail> mailList = null;
    List<Integer> idList = null;
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
        
    public ServerMessage(List<EMail> mails, List<Integer> ids){
        type = Type.UPDATE_INBOX_SUCCESS;
        mailList = new ArrayList<EMail>(mails); 
        idList = new ArrayList<Integer>(ids);
    }
    
    public List<EMail> getFolderMailList(){
        return mailList;
    }
    
    public List<Integer> getFolderIDList(){
        return idList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
