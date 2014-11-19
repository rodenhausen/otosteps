package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.util.LinkedList;
import java.util.List;

public class MainTermSynonyms {
	private Term mainTerm;
	private List<Term> synonyms;

	public MainTermSynonyms(Term mainTerm, List<Term> synonyms) {
		this.mainTerm = mainTerm;
		this.synonyms = synonyms;
	}

	public Term getMainTerm() {
		return mainTerm;
	}

	public void setMainTerm(Term mainTerm) {
		this.mainTerm = mainTerm;
	}

	public List<Term> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<Term> synonyms) {
		this.synonyms = synonyms;
	}
	
	public List<Term> getTerms() {
		List<Term> result = new LinkedList<Term>();
		result.add(mainTerm);
		result.addAll(synonyms);
		return result;
	}
	
}