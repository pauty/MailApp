/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.rmi.*;
import java.util.List;
import mailapp.EMail;
import mailapp.User;

/**
 *
 * @author pauty
 */
public interface MailServer extends Remote{
    
    public void sendMail(EMail mail) throws RemoteException;
    
    public void deleteFolderMails(User user, String folderName, List<Integer> toDelete) throws RemoteException;
    
    public ServerMessage getFolderMails(User user, String folderName, List<Integer> pulled) throws RemoteException;
   
}
