package com.example.archunit.exceptionrelated;

public class Service1 {

    public void methodThatThrowsRuntimeException() throws RuntimeException {
        if(true)
            throw new RuntimeException("aaaaaa");
    }

    public void methodThatThrowsCustomRuntimeException() throws CustomRuntimeException {
        if(true)
            throw new CustomRuntimeException("aaaaaa");
    }

    public void methodHidesThatItThrowsRuntimeException() {
        if(true)
            throw new RuntimeException("aaaaaa");
    }

    public void methodHidesThatItThrowsCustomRuntimeException() {
        if(true)
            throw new CustomRuntimeException("aaaaaa");
    }
}
