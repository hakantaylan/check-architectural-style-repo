package com.tngtech.archunit.example.layers.persistence.first.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.tngtech.archunit.example.layers.persistence.first.dao.SomeDao;
import com.tngtech.archunit.example.layers.persistence.first.dao.domain.PersistentObject;
import com.tngtech.archunit.example.layers.security.Secured;

public class SomeJpa implements SomeDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Secured
    public SomeJpa() {
    }

    @Override
    public PersistentObject findById(long id) {
        return entityManager.find(PersistentObject.class, id);
    }
}
