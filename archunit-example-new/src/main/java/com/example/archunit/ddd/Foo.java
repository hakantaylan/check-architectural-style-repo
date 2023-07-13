package com.example.archunit.ddd;

public class Foo {

    Hello h = new Hello();

    public void method1() {
    }

    public void method2() {
    }

    public void method3() {
    }

    public void method4() {
        h.A();
        h.B();
    }
}
