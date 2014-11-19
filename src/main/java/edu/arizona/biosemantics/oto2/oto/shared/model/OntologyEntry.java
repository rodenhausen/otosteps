package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public class OntologyEntry implements Serializable {

	private String id;
	private String ontologyAcronym;
	private String ontologyName;
	private String label;
	private String definition;
	private String url;
	
	public OntologyEntry() { }
	
	public OntologyEntry(String id, String ontologyAcronym, String ontologyName, String label, String definition, String url) {
		super();
		this.id = id;
		this.ontologyAcronym = ontologyAcronym;
		this.ontologyName = ontologyName;
		this.label = label;
		this.definition = definition;
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		OntologyEntry other = (OntologyEntry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getOntologyAcronym() {
		return ontologyAcronym;
	}
	
	public void setOntologyAcronym(String ontologyAcronym) {
		this.ontologyAcronym = ontologyAcronym;
	}

	
	
}
