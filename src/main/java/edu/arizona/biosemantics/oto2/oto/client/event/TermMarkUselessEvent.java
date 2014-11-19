package edu.arizona.biosemantics.oto2.oto.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.TermMarkUselessEvent.MarkUselessTermHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermMarkUselessEvent extends GwtEvent<MarkUselessTermHandler> {

	public interface MarkUselessTermHandler extends EventHandler {
		void onMark(TermMarkUselessEvent event);
	}
	
    public static Type<MarkUselessTermHandler> TYPE = new Type<MarkUselessTermHandler>();
    
    private List<Term> terms = new LinkedList<Term>();
	private boolean newUseless;
    
    public TermMarkUselessEvent(Term term, boolean newUseless) {
        this.terms.add(term);
        this.newUseless = newUseless;
    }
    
    public TermMarkUselessEvent(List<Term> terms, boolean newUseless) {
        this.terms = terms;
        this.newUseless = newUseless;
    }
	
	@Override
	public Type<MarkUselessTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MarkUselessTermHandler handler) {
		handler.onMark(this);
	}

	public List<Term> getTerms() {
		return terms;
	}

	public boolean isNewUseless() {
		return newUseless;
	}
}
