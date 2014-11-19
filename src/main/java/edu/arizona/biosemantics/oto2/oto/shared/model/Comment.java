package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public class Comment implements Serializable, Comparable<Comment> {

	private int id = -1;
	private String user = "";
	private String comment = "";
	
	public Comment() {}
	
	public Comment(String user, String comment) {
		this.user = user;
		this.comment = comment;
	}
	
	public Comment(int id, String user, String comment) {
		this.id = id;
		this.user = user;
		this.comment = comment;
	}
	
	public boolean hasId() {
		return id != -1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
		Comment other = (Comment) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(Comment o) {
		return this.getComment().compareTo(o.getComment());
	}
		
}
