package com.example.archunit.persistenceservice;

import com.example.archunit.persistence.IPersistenceSession;
import com.example.archunit.persistence.PersistenceSession;
import com.example.archunit.util.ResourceCloser;

public class PersistenceConsumer1 {
    PersistenceConsumer2 anotherConsumer = new PersistenceConsumer2();

    public void closesSessionItself() {
        PersistenceSession session = null;
        try {
            session = session.getPersistenceSession();
            System.out.println("bla bla bla");
        } catch (Exception e) {

        } finally {
            if (session != null)
                session.close();
        }
    }

    public void delegatedClosingToUtilityFunction() {
        PersistenceSession session = null;
        try {
            session = session.getPersistenceSession();
            System.out.println("bla bla bla");
        } catch (Exception e) {

        } finally {
            ResourceCloser.close(session);
        }
    }

    public void passesSessionToAnotherMethod() {
        PersistenceSession session = null;
        try {
            session = session.getPersistenceSession();
            takesSessionAsParameter(session);
        } catch (Exception e) {

        } finally {
            if (session != null)
                session.close();
        }
    }

    public void passesSessionToAnotherMethod2() {
        PersistenceSession session = null;
        try {
            session = session.getPersistenceSession();
            takesSessionThroughInterfaceAsParameter(session);
        } catch (Exception e) {

        } finally {
            if (session != null)
                session.close();
        }
    }

    public void passesSessionToAnotherMethod3() {
        PersistenceSession session = null;
        try {
            session = session.getPersistenceSession();
            takesSessionAsParameter(session);
            takesSessionThroughInterfaceAsParameter(session);
        } catch (Exception e) {

        } finally {
            ResourceCloser.close(session);
        }
    }

    private void takesSessionAsParameter(PersistenceSession session) {
        try {
            System.out.println("using given session");
        } catch (Exception e) {

        }
    }

    private void takesSessionThroughInterfaceAsParameter(IPersistenceSession session) {

        try {
            System.out.println("using given session");
        } catch (Exception e) {

        }
    }

    public void passesSessionToAnotherInstance() {
        PersistenceSession session = null;
        try {
            session = session.getPersistenceSession();
            anotherConsumer.takesSessionAsParameter(session);
        } catch (Exception e) {

        } finally {
            if (session != null)
                session.close();
        }
    }

    public void passesSessionToAnotherInstance2() {
        PersistenceSession session = null;
        try {
            session = session.getPersistenceSession();
            anotherConsumer.takesSessionThroughInterfaceAsParameter(session);
        } catch (Exception e) {

        } finally {
            if (session != null)
                session.close();
        }
    }

    public void notClosesSession() {
        PersistenceSession session = null;
        try {
            session = session.getPersistenceSession();
            takesSessionAsParameter(session);
            takesSessionThroughInterfaceAsParameter(session);
        } catch (Exception e) {

        }
    }
}
