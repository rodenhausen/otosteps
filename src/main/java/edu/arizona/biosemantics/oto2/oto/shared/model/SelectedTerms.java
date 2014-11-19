package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.util.LinkedList;
import java.util.List;

public class SelectedTerms {

	private List<MainTermSynonyms> mainTermSynonyms = new LinkedList<MainTermSynonyms>();
	private List<Term> additionalTerms = new LinkedList<Term>();
	
	public SelectedTerms(MainTermSynonyms mainTermSynonyms) {
		this.mainTermSynonyms.add(mainTermSynonyms);
	}
	
	public SelectedTerms(List<MainTermSynonyms> mainTermSynonyms) {
		this.mainTermSynonyms = mainTermSynonyms;
	}
	
	public SelectedTerms(List<MainTermSynonyms> mainTermSynonyms,
			List<Term> additionalTerms) {
		super();
		this.mainTermSynonyms = mainTermSynonyms;
		this.additionalTerms = additionalTerms;
	}


	public List<MainTermSynonyms> getMainTermSynonyms() {
		return mainTermSynonyms;
	}


	public List<Term> getAdditionalTerms() {
		return additionalTerms;
	}
	
	public List<Term> getTerms() {
		List<Term> result = new LinkedList<Term>();
		for(MainTermSynonyms mainTermSynonym : mainTermSynonyms) {
			result.addAll(mainTermSynonym.getTerms());
		}
		result.addAll(additionalTerms);
		return result;
	}


	public boolean isEmpty() {
		return getTerms().isEmpty();
	}
	
	public boolean isExplicitelySelectedSingleTerm() {
		return (mainTermSynonyms.size() == 1 && additionalTerms.size() == 0) ||
				(mainTermSynonyms.size() == 0 && additionalTerms.size() == 1);
	}
		
}
