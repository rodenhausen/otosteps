package edu.arizona.biosemantics.oto2.oto.client.event;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent.CategorizeMoveTermHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class CategorizeMoveTermEvent extends GwtEvent<CategorizeMoveTermHandler> {

	public interface CategorizeMoveTermHandler extends EventHandler {
		void onCategorize(CategorizeMoveTermEvent event);
	}
	
    public static Type<CategorizeMoveTermHandler> TYPE = new Type<CategorizeMoveTermHandler>();
    
	private Label sourceCategory;
	private Label targetCategory;
	private SelectedTerms selectedTerms;
	
    public CategorizeMoveTermEvent(SelectedTerms selectedTerms, Label sourceCategory, Label targetCategory) {
        this.selectedTerms = selectedTerms;
        this.sourceCategory = sourceCategory;
        this.targetCategory = targetCategory;
    }
	
	@Override
	public Type<CategorizeMoveTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CategorizeMoveTermHandler handler) {
		handler.onCategorize(this);
	}

	public Label getSourceCategory() {
		return sourceCategory;
	}

	public Label getTargetCategory() {
		return targetCategory;
	}

	public SelectedTerms getSelectedTerms() {
		return selectedTerms;
	}	
	
}
