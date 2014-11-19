package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.LoadEvent.LoadHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class LoadEvent extends GwtEvent<LoadHandler> {

	public interface LoadHandler extends EventHandler {
		void onLoad(LoadEvent event);
	}
	
    public static Type<LoadHandler> TYPE = new Type<LoadHandler>();
	private Collection collection;
	private boolean initializeFromHistory;

    public LoadEvent(Collection collection, boolean initializeFromHistory) {
    	this.collection = collection;
    	this.initializeFromHistory = initializeFromHistory;
    }
    
	@Override
	public Type<LoadHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadHandler handler) {
		handler.onLoad(this);
	}

	public Collection getCollection() {
		return collection;
	}

	public boolean isInitializeFromHistory() {
		return initializeFromHistory;
	}
	
}
