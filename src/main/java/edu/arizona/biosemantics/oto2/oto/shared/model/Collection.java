package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Collection implements Serializable {

	private int id = -1;
	private String name = "";
	private String type = "";
	private String secret = "";
	private List<Bucket> buckets = new LinkedList<Bucket>();
	private List<Label> labels = new LinkedList<Label>();

	public Collection() { }
	
	public Collection(String name, String type, String secret) {
		this.name = name;
		this.type = type;
		this.secret = secret;
	}
	
	public Collection(int id, String secret) {
		this.id = id;
		this.secret = secret;
	}	
	
	public Collection(int id, String name, String type, String secret) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.secret = secret;
	}

	public int getId() {
		return id;
	}
	
	public void setBuckets(List<Bucket> buckets) {
		for(Bucket bucket : buckets)
			bucket.setCollection(this.getId());
		this.buckets = buckets;
	}
	
	public void setLabels(List<Label> labels) {
		for(Label label : labels)
			label.setCollection(this.getId());
		this.labels = labels;
	}

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public List<Label> getLabels() {
		return labels;
	}
	
	public boolean hasId() {
		return id != -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void add(Bucket bucket) {
		bucket.setCollection(this.getId());
		buckets.add(bucket);
	}

	public void add(Label label) {
		label.setCollection(this.getId());
		labels.add(label);
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
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
		Collection other = (Collection) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void addLabel(Label label) {
		label.setCollection(this.id);
		this.labels.add(label);
	}

	public void removeLabel(Label label) {
		this.labels.remove(label);
	}

	public void removeLabels(List<Label> labels) {
		this.labels.removeAll(labels);
	}

	public List<Label> getLabels(Term term) {
		List<Label> result = new LinkedList<Label>();
		for(Label label : labels) {
			for(Term mainTerm : label.getMainTerms()) {
				if(mainTerm.equals(term))
					result.add(label);
				for(Term synonym : label.getSynonyms(mainTerm)) {
					if(synonym.equals(term))
						result.add(label);
				}
			}
		}
		return result;
	}

	public List<Term> getTerms() {
		List<Term> result = new LinkedList<Term>();
		for(Bucket bucket : buckets) {
			result.addAll(bucket.getTerms());
		}
		return result;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
