package com.tngtech.archunit.example.layers.persistence.first.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Violates Rule that Entities must reside in a package 'domain'
@Entity
public class EntityInWrongPackage {
    @Id
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
