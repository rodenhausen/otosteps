package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.TermSelectEvent.TermSelectHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermSelectEvent extends GwtEvent<TermSelectHandler> {

	public interface TermSelectHandler extends EventHandler {
		void onSelect(TermSelectEvent event);
	}
	
    public static Type<TermSelectHandler> TYPE = new Type<TermSelectHandler>();
    
    private Term term;
    
    public TermSelectEvent(Term term) {
        this.term = term;
    }
	
	@Override
	public Type<TermSelectHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TermSelectHandler handler) {
		handler.onSelect(this);
	}

	public Term getTerm() {
		return term;
	}

}
