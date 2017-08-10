/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.rmi.*;
import java.util.ArrayList;
import mailapp.EMail;
import mailapp.User;

/**
 *
 * @author pauty
 */
public interface MailServer extends Remote{
    
    public ServerMessage sendMail(EMail mail) throws RemoteException;
    
    public ServerMessage deleteMail(User user, ArrayList<Integer> toDelete) throws RemoteException;
    
    public ServerMessage getUserInbox(User user, int lastPulledID) throws RemoteException;
   
}
