package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.TermSplitEvent.SplitTermHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermSplitEvent extends GwtEvent<SplitTermHandler> {

	public interface SplitTermHandler extends EventHandler {
		void onSplit(TermSplitEvent event);
	}
	
    public static Type<SplitTermHandler> TYPE = new Type<SplitTermHandler>();
    
    private Term term;
    private String newName;
    
    public TermSplitEvent(Term term, String newName) {
        this.term = term;
        this.newName = newName;
    }
	
	@Override
	public Type<SplitTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SplitTermHandler handler) {
		handler.onSplit(this);
	}

	public Term getTerm() {
		return term;
	}

	public String getNewName() {
		return newName;
	}

}
