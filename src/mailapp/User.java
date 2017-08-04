
package mailapp;

import java.io.Serializable;

/**
 *
 * @author pauty
 */
public class User implements Serializable{
    private String name;
    private String address;
    
    public User(String n, String a){
        name = n;
        address = a;
    }
    
    public String getName(){
        return name;
    }
    
    public String getAddress(){
        return address;
    }
}
