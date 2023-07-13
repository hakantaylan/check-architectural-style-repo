package com.example.archunit.persistence;

public class PersistenceSession implements IPersistenceSession{

    public static PersistenceSession getPersistenceSession() {
        return new PersistenceSession();
    }

    @Override
    public void close() {

    }
}
