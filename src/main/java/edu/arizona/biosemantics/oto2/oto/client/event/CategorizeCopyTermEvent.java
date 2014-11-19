package edu.arizona.biosemantics.oto2.oto.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent.CategorizeCopyTermHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;

public class CategorizeCopyTermEvent extends GwtEvent<CategorizeCopyTermHandler> {

	public interface CategorizeCopyTermHandler extends EventHandler {
		void onCategorize(CategorizeCopyTermEvent event);
	}
	
    public static Type<CategorizeCopyTermHandler> TYPE = new Type<CategorizeCopyTermHandler>();
    
	private Label sourceCategory;
	private List<Label> targetCategories = new LinkedList<Label>();

	private SelectedTerms selectedTerms;
	
	public CategorizeCopyTermEvent(SelectedTerms selectedTerms, Label sourceCategory, List<Label> targetCategories) {
		this.selectedTerms = selectedTerms;
		this.sourceCategory = sourceCategory;
		this.targetCategories = targetCategories;
	}

	public CategorizeCopyTermEvent(SelectedTerms selectedTerms, Label sourceCategory, Label targetCategory) {
		this.selectedTerms = selectedTerms;
		this.sourceCategory = sourceCategory;
		this.targetCategories.add(targetCategory);
	}

	@Override
	public Type<CategorizeCopyTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CategorizeCopyTermHandler handler) {
		handler.onCategorize(this);
	}

	public Label getSourceCategory() {
		return sourceCategory;
	}

	public List<Label> getTargetCategories() {
		return targetCategories;
	}

	public SelectedTerms getSelectedTerms() {
		return selectedTerms;
	}
	
}
