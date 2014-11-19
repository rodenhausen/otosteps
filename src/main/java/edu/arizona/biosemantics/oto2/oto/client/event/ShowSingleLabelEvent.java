package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.ShowSingleLabelEvent.ShowSingleLabelHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class ShowSingleLabelEvent extends GwtEvent<ShowSingleLabelHandler> {

	public interface ShowSingleLabelHandler extends EventHandler {
		void onSave(ShowSingleLabelEvent event);
	}
	
    public static Type<ShowSingleLabelHandler> TYPE = new Type<ShowSingleLabelHandler>();
	private Label label;

    public ShowSingleLabelEvent(Label label) {
    	this.label = label;
    }
    
	@Override
	public Type<ShowSingleLabelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowSingleLabelHandler handler) {
		handler.onSave(this);
	}

	public Label getLabel() {
		return label;
	}
	
}
