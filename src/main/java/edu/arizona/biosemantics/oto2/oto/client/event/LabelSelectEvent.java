package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelSelectEvent.LabelSelectHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class LabelSelectEvent extends GwtEvent<LabelSelectHandler> {

	public interface LabelSelectHandler extends EventHandler {
		void onSelect(LabelSelectEvent event);
	}
	
    public static Type<LabelSelectHandler> TYPE = new Type<LabelSelectHandler>();
    
    private Label label;
    
    public LabelSelectEvent(Label label) {
        this.label = label;
    }
	
	@Override
	public Type<LabelSelectHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LabelSelectHandler handler) {
		handler.onSelect(this);
	}

	public Label getLabel() {
		return label;
	}

}
