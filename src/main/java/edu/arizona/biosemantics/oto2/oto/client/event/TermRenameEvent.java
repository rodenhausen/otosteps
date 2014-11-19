package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent.RenameTermHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermRenameEvent extends GwtEvent<RenameTermHandler> {

	public interface RenameTermHandler extends EventHandler {
		void onRename(TermRenameEvent event);
	}
	
    public static Type<RenameTermHandler> TYPE = new Type<RenameTermHandler>();
    
    private Term term;
    private String oldName;
    private String newName;
    private boolean validRename = true;
    
    public TermRenameEvent(Term term, String oldName, String newName, Collection collection) {
        this.term = term;
        this.oldName = oldName;
        this.newName = newName;
        
		for(Term collectionTerm : collection.getTerms()) {
			if(collectionTerm.getTerm().equals(newName)) {
				Alerter.alertTermWithNameExists(newName);
				validRename = false;
			}
		}
    }
	
	@Override
	public Type<RenameTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RenameTermHandler handler) {
		if(validRename)
			handler.onRename(this);
	}

	public Term getTerm() {
		return term;
	}

	public String getNewName() {
		return newName;
	}

	public String getOldName() {
		return oldName;
	}
	
}
