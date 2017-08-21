
package mailapp.client.connection;

/**
 *
 * @author pauty
 */
public class ConnectionManagerMessage {
    private String updatedFolderName;
    private Type type;
    public enum Type{
        MAIL_FOLDER_UPDATE,
        DISCONNECT
    }
    
    public ConnectionManagerMessage(String folderName){
        type = Type.MAIL_FOLDER_UPDATE;
        updatedFolderName = folderName;
    }
    
    public ConnectionManagerMessage(){
        type = Type.DISCONNECT;
        updatedFolderName = null;
    }
    
    public Type getType(){
        return type;
    }
    
    public String getUpdatedFolderName(){
        return updatedFolderName;
    }
}
