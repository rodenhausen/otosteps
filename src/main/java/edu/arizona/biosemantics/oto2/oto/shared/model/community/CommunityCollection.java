package edu.arizona.biosemantics.oto2.oto.shared.model.community;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CommunityCollection implements Serializable {

	private Set<Categorization> categorizations = new HashSet<Categorization>();
	private Set<Synonymization> synonymizations = new HashSet<Synonymization>();
	
	public CommunityCollection() { }
	
	public CommunityCollection(Set<Categorization> categorizations,
			Set<Synonymization> synonymizations) {
		super();
		this.categorizations = categorizations;
		this.synonymizations = synonymizations;
	}
	public Set<Categorization> getCategorizations() {
		return categorizations;
	}
	public Set<Synonymization> getSynonymizations() {
		return synonymizations;
	}	
}
