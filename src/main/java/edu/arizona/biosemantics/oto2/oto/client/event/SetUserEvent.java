package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.arizona.biosemantics.oto2.oto.client.event.SetUserEvent.SetUserHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class SetUserEvent extends GwtEvent<SetUserHandler> {

	public interface SetUserHandler extends EventHandler {
		void onSet(SetUserEvent event);
	}
	
    public static Type<SetUserHandler> TYPE = new Type<SetUserHandler>();
	private String user;

    public SetUserEvent(String user) {
    	this.user = user;
    }
    
	@Override
	public Type<SetUserHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetUserHandler handler) {
		handler.onSet(this);
	}

	public String getUser() {
		return user;
	}	
}
