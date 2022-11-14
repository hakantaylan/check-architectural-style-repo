package com.tngtech.archunit.example.layers.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.tngtech.archunit.example.layers.controller.marshaller.Unmarshaller;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface UnmarshalTransport {
    Class<? extends Unmarshaller<?>>[] value();
}
