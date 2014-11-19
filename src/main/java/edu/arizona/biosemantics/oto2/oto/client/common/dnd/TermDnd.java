package edu.arizona.biosemantics.oto2.oto.client.common.dnd;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermDnd {

	protected Widget source;
	protected List<Term> terms = new LinkedList<Term>();
		
	public TermDnd(Widget source) {
		this.source = source;
	}
	
	public TermDnd(Widget source, List<Term> terms) {
		super();
		this.source = source;
		this.terms = terms;
	}

	public Widget getSource() {
		return source;
	}
	public void setSource(Widget source) {
		this.source = source;
	}
	public List<Term> getTerms() {
		return terms;
	}
	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}
	
}
