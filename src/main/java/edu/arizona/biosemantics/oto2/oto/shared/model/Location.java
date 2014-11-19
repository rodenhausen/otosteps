package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public class Location implements Serializable {

	private String instance;
	private Label categorization;
	
	public Location() { }
	
	public Location(String instance, Label categorization) {
		super();
		this.instance = instance;
		this.categorization = categorization;
	}
	
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public Label getCategorization() {
		return categorization;
	}
	public void setCategorization(Label categorization) {
		this.categorization = categorization;
	}
}
