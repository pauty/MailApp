/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.server;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author pauty
 */
public class FileLocker {
    private static FileLocker singleInstance = null;
    private HashMap<String, ReadWriteLock> lockMap;
            
    private FileLocker(){
        lockMap = new HashMap<String, ReadWriteLock>();
    }
    
    public static FileLocker getInstance(){
        if(singleInstance == null)
            singleInstance = new FileLocker();
        return singleInstance;
    }
    
    public ReadWriteLock getLockForUser(String userAddress){
        ReadWriteLock lock = lockMap.get(userAddress);
        if(lock == null){
            lock = new ReentrantReadWriteLock();
            lockMap.put(userAddress, lock);
        }
        return lock;  
    }
}
