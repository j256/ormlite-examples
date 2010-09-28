package com.example.notifyservice.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Example data element we are storing in the service.
 * 
 * @author kevingalligan
 */
@DatabaseTable
public class Thing {

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer thingId) {
		this.id = thingId;
	}
}
