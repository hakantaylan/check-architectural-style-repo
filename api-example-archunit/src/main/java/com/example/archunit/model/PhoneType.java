package com.example.archunit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "phoneType")
public class PhoneType extends Base {

	private static final long serialVersionUID = 1697687804373017457L;

	@Column(nullable = false, length = 250)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getId(), 
				this.getActive(), 
				this.getVersion(),
				name);
	}

	@Override
	public boolean equals(Object obj) {
		return Objects.equal(this, obj);
	}
}