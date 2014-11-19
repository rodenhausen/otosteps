package edu.arizona.biosemantics.oto2.oto.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelCreateEvent;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;

public class LabelAddDialog extends Dialog {
	
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	
	public LabelAddDialog(final EventBus eventBus, final Collection collection) {
		this.setHeadingText("Add Category");
		LabelInfoContainer labelInfoContainer = new LabelInfoContainer("", "");
	    this.add(labelInfoContainer);
	 
	    final TextField labelName = labelInfoContainer.getLabelName();
	    final TextArea labelDescription = labelInfoContainer.getLabelDescription();
	    
	    getButtonBar().clear();
	    TextButton add = new TextButton("Add");
	    add.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(!labelName.validate()) {
					AlertMessageBox alert = new AlertMessageBox("Category Name", "A category name is required");
					alert.show();
					return;
				}
				
				final Label newLabel = new Label(labelName.getText(), labelDescription.getText());
				collectionService.addLabel(newLabel, collection.getId(), new AsyncCallback<Label>() {
					@Override
					public void onSuccess(Label result) {
						eventBus.fireEvent(new LabelCreateEvent(result));
						LabelAddDialog.this.hide();
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.addLabelFailed(caught);
					}
				});
			}
	    });
	    TextButton cancel =  new TextButton("Cancel");
	    cancel.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				LabelAddDialog.this.hide();
			}
	    });
	    addButton(add);
	    addButton(cancel);
	}
}