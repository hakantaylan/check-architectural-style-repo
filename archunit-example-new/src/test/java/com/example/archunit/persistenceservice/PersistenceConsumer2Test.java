package com.example.archunit.persistenceservice;

import com.example.archunit.persistence.PersistenceSession;
import org.junit.jupiter.api.Test;

public class PersistenceConsumer2Test {
    private PersistenceConsumer2 consumer2;

    @Test
    public void takesSessionAsParameter() {
        consumer2.takesSessionAsParameter(PersistenceSession.getPersistenceSession());
    }

    @Test
    public void takesSessionThroughInterfaceAsParameter() {
        consumer2.takesSessionThroughInterfaceAsParameter(PersistenceSession.getPersistenceSession());
    }
}
