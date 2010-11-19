package com.kagii.clickcounter.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Counter group information object saved to the database.
 * 
 * @author kevingalligan
 */
@DatabaseTable
public class ClickGroup implements Serializable {

	private static final long serialVersionUID = -7874823823497497357L;

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String name;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name == null ? "<None>" : name;
	}
}
