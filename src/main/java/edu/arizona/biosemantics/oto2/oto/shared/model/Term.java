package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Term implements Serializable, Comparable<Term> {

	public static class TermComparator implements Comparator<Term> {
		@Override
		public int compare(Term o1, Term o2) {
			return o1.getTerm().compareTo(o2.getTerm());
		}
	}
	
	private int id = -1;
	private String term = "";
	private String originalTerm = "";
	private boolean useless;
	private List<Comment> comments = new LinkedList<Comment>();

	public Term() { }
	
	public Term(String term) {
		this.term = term;
		this.originalTerm = term;
	}
	
	public Term(int id, String term, String originalTerm, boolean useless) {
		super();
		this.id = id;
		this.term = term;
		this.originalTerm = originalTerm;
		this.useless = useless;
	}
	
	public Term(int id, String term, String originalTerm, boolean useless, List<Comment> comments) {
		super();
		this.id = id;
		this.term = term;
		this.originalTerm = originalTerm;
		this.useless = useless;
		this.comments = comments;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	public String getOriginalTerm() {
		return originalTerm;
	}

	public void setOriginalTerm(String originalTerm) {
		this.originalTerm = originalTerm;
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

	public void setUseless(boolean useless) {
		this.useless = useless;
	}
	
	public boolean getUseless() {
		return useless;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public void setComment(Comment comment) {
		Iterator<Comment> iterator = comments.iterator();
		while(iterator.hasNext()) {
			Comment c = iterator.next();
			if(c.getUser().equals(comment.getUser())) {
				iterator.remove();
			}
		}
		if(comment.getComment() != null)
			comments.add(comment);
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
		Term other = (Term) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public boolean hasChangedSpelling() {
		return !this.term.equals(this.originalTerm);
	}

	@Override
	public int compareTo(Term o) {
		return this.getTerm().compareTo(o.getTerm());
	}	
	
}
