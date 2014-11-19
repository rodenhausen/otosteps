package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent.SaveHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class SaveEvent extends GwtEvent<SaveHandler> {

	public interface SaveHandler extends EventHandler {
		void onSave(SaveEvent event);
	}
	
    public static Type<SaveHandler> TYPE = new Type<SaveHandler>();
	private Collection collection;

    public SaveEvent(Collection collection) {
    	this.collection = collection;
    }
    
	@Override
	public Type<SaveHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveHandler handler) {
		handler.onSave(this);
	}

	public Collection getCollection() {
		return collection;
	}
	
}
