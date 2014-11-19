package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent.RemoveLabelHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class LabelRemoveEvent extends GwtEvent<RemoveLabelHandler> {

	public interface RemoveLabelHandler extends EventHandler {
		void onRemove(LabelRemoveEvent event);
	}
	
    public static Type<RemoveLabelHandler> TYPE = new Type<RemoveLabelHandler>();
    
    private Label label;
    
    public LabelRemoveEvent(Label label) {
        this.label = label;
    }
	
	@Override
	public Type<RemoveLabelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RemoveLabelHandler handler) {
		handler.onRemove(this);
	}

	public Label getLabel() {
		return label;
	}

}
