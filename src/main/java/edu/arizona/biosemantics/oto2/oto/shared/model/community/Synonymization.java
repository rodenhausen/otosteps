package edu.arizona.biosemantics.oto2.oto.shared.model.community;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Synonymization implements Serializable {
		private String label;
		private String mainTerm;
		private Set<String> synonyms = new HashSet<String>();
		public Synonymization(String label, String mainTerm, Set<String> synonyms) {
			this.label = label;
			this.mainTerm = mainTerm;
			this.synonyms = synonyms;
		}
		public String getLabel() {
			return label;
		}
		public String getMainTerm() {
			return mainTerm;
		}
		public Set<String> getSynonyms() {
			return synonyms;
		}
	}