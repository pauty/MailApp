
package mailapp.client.connection;

/**
 *
 * @author pauty
 */
public class ConnectionManagerMessage {
    public static enum Type{
        INBOX_RESPONSE,
        SEND_RESPONSE,
        DELETE_RESPONSE
    }
    Type type;
    Object content;
    
    public ConnectionManagerMessage(Type t, Object o){
        type = t;
        content = o;
    }
    
    public Type getType(){
        return type;
    }
    
    public Object getContent(){
        return content;
    }
    
}
