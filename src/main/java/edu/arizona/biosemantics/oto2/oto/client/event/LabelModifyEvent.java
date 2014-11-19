package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelModifyEvent.ModifyLabelHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class LabelModifyEvent extends GwtEvent<ModifyLabelHandler> {

	public interface ModifyLabelHandler extends EventHandler {
		void onModify(LabelModifyEvent event);
	}
	
    public static Type<ModifyLabelHandler> TYPE = new Type<ModifyLabelHandler>();
    
    private Label label;
	private String newName;
	private String newDescription;
    
    public LabelModifyEvent(Label label, String newName, String newDescription) {
        this.label = label;
        this.newName = newName;
        this.newDescription = newDescription;
    }
	
	@Override
	public Type<ModifyLabelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModifyLabelHandler handler) {
		handler.onModify(this);
	}

	public Label getLabel() {
		return label;
	}

	public String getNewName() {
		return newName;
	}

	public String getNewDescription() {
		return newDescription;
	}

	
	
}
