package com.example.archunit.persistenceservice;

import com.example.archunit.persistence.IPersistenceSession;
import com.example.archunit.persistence.PersistenceSession;

public class PersistenceConsumer2 {

    public void takesSessionAsParameter(PersistenceSession session){
        try{
            System.out.println("using given session");
        }
        catch(Exception e) {

        }
    }

    public void takesSessionThroughInterfaceAsParameter(IPersistenceSession session){

        try{
            System.out.println("using given session");
        }
        catch(Exception e) {

        }
    }
}
