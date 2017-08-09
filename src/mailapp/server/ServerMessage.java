/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.io.Serializable;
import java.util.ArrayList;
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

    public ArrayList<User> getInvalidUserList() {
        return invalidUserList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
