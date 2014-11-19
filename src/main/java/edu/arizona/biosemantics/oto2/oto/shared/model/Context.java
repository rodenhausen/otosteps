package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public class Context implements Serializable {

	private int id = -1;
	private int collectionId;
	private String source = "";
	private String text = "";
	
	public Context() { }
	
	public Context(int collectionId, String source, String text) {
		this.collectionId = collectionId;
		this.source = source;
		this.text = text;
	}

	public Context(int id, int collectionId, String source, String text) {
		super();
		this.id = id;
		this.collectionId = collectionId;
		this.source = source;
		this.text = text;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
	public int getId() {
		return id;
	}
	public boolean hasId() {
		return id != -1;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Context other = (Context) obj;
		if (id != other.id)
			return false;
		return true;
	}	
	
}
