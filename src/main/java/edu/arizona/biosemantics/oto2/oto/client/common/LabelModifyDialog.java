package edu.arizona.biosemantics.oto2.oto.client.common;


import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto2.oto.shared.LabelNameNormalizer;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class LabelModifyDialog extends Dialog {
		
		public LabelModifyDialog(final EventBus eventBus, final Label label) {
			this.setHeadingText("Modify Category");	
			LabelInfoContainer labelInfoContainer = new LabelInfoContainer(label.getName(), label.getDescription());
		    this.add(labelInfoContainer);
		 
		    final TextField labelName = labelInfoContainer.getLabelName();
		    final TextArea labelDescription = labelInfoContainer.getLabelDescription();
		    
		    getButtonBar().clear();
		    TextButton save = new TextButton("Save");
		    save.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					if(!labelName.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Category Name", "A category name is required");
						alert.show();
						return;
					}
					String normalizedLabelName = LabelNameNormalizer.normalize(labelName.getText());
					eventBus.fireEvent(new LabelModifyEvent(label, normalizedLabelName, labelDescription.getText()));
					LabelModifyDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					LabelModifyDialog.this.hide();
				}
		    });
		    addButton(save);
		    addButton(cancel);
		}
	
	}