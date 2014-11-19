package edu.arizona.biosemantics.oto2.oto.shared.model.community;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Categorization implements Serializable {
	
	private String term;
	private Set<String> categories = new HashSet<String>();
	
	public Categorization() { } 
	
	public Categorization(String term, Set<String> categories) {
		this.term = term;
		this.categories = categories;
	}
	public String getTerm() {
		return term;
	}
	public Set<String> getCategories() {
		return categories;
	}
}

