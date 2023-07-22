package com.example.archunit.exceptionrelated;

import java.io.IOException;

public class Service2 {

    private final Service1 service;

    public Service2(Service1 service) {
        this.service = service;
    }

    public void method1() {
        System.out.println("aaaaaa");
    }

    public void method2() throws IOException {
        if(true)
            throw new IOException("aaaaa");
    }

    public void callsAnotherServiceAndThrowRuntimeException() throws RuntimeException {
        service.methodThatThrowsRuntimeException();
    }

    public void callsAnotherServiceAndThrowCustomRuntimeException() throws CustomRuntimeException {
        service.methodThatThrowsCustomRuntimeException();
    }

    public void callsAnotherServiceAndHidesThatItThrowRuntimeException() {
        service.methodThatThrowsRuntimeException();
    }

    public void callsAnotherServiceAndHidesThatItThrowCustomRuntimeException() {
        service.methodThatThrowsCustomRuntimeException();
    }
}
