package edu.arizona.biosemantics.oto2.oto.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent.TermUncategorizeHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermUncategorizeEvent extends GwtEvent<TermUncategorizeHandler> {

	public interface TermUncategorizeHandler extends EventHandler {
		void onUncategorize(TermUncategorizeEvent event);
	}
	
    public static Type<TermUncategorizeHandler> TYPE = new Type<TermUncategorizeHandler>();
    
    private List<Term> terms;
	private List<Label> oldLabels;
	
	public TermUncategorizeEvent(Term term, Label oldLabel) {
		this.terms = new LinkedList<Term>();
		this.terms.add(term);
		this.oldLabels = new LinkedList<Label>();
		oldLabels.add(oldLabel);
	}
	
	public TermUncategorizeEvent(Term term, List<Label> oldLabels) {
		this.terms = new LinkedList<Term>();
		this.terms.add(term);
		this.oldLabels = oldLabels;
	}
	
	public TermUncategorizeEvent(List<Term> terms, List<Label> oldLabels) {
		this.terms = terms;
		this.oldLabels = oldLabels;
	}
    
    /*public TermUncategorizeEvent(List<Term> terms, Label oldLabel) {
        this.terms = terms;
        this.oldLabels = new HashSet<Label>(1);
        oldLabels.add(oldLabel);
    }
	
    public TermUncategorizeEvent(List<Term> terms, Set<Label> oldLabels) {
        this.terms = terms;
        this.oldLabels = oldLabels;
    }*/
	
	@Override
	public Type<TermUncategorizeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TermUncategorizeHandler handler) {
		handler.onUncategorize(this);
	}

	public List<Term> getTerms() {
		return terms;
	}

	public List<Label> getOldLabels() {
		return oldLabels;
	}

}
