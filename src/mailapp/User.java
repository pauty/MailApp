
package mailapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof User))
            return false;
        
        User user = (User)obj;
        return (this.address.equals(user.getAddress()));
    }
    
    //Utility
    public static String printUserNamesList(List<User> list, String separator){
        String res = "";
        for(int i = 0; i < list.size() - 1; i++){
            res += list.get(i).getName() + separator;
        }
        res += list.get(list.size() - 1).getName();
        return res;
    }
    
    public static String printUserAddressesList(List<User> list, String separator){
        String res = "";
        for(int i = 0; i < list.size() - 1; i++){
            res += list.get(i).getAddress() + separator;
        }
        res += list.get(list.size() - 1).getAddress();
        return res;
    }

}
