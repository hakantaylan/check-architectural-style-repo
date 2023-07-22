package com.example.archunit.exceptionrelated;

import com.example.archunit.outerpackage.Message;

public class WrongMessageConsumer {

    public void wrongMethod(){
        Message m = new Message();
        System.out.println(m.getMessage());
    }

    public void correctMethod(){
        com.example.archunit.exceptionrelated.Message m = new com.example.archunit.exceptionrelated.Message();
        System.out.println(m.getMessage());
    }
}
