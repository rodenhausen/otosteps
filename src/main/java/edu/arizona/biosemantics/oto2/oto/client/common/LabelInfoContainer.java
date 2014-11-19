package edu.arizona.biosemantics.oto2.oto.client.common;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

public class LabelInfoContainer extends SimpleContainer {
		
		private TextField labelName;
		private TextArea labelDescription;

		public LabelInfoContainer(String initialName, String initialDescription) {
			FieldSet fieldSet = new FieldSet();
		    fieldSet.setHeadingText("Category Information");
		    fieldSet.setCollapsible(false);
		    this.add(fieldSet, new MarginData(10));
		 
		    VerticalLayoutContainer p = new VerticalLayoutContainer();
		    fieldSet.add(p);
		    
		    //remove whenever this is supposed to be generalized to be used itside of oto -> markup chain
		    p.add(new FieldLabel(new HTML("Use a set of meaningful categories. The categories used have a direct impact on your " +
		    		"markup result."), "Beware"));
		    
		    labelName = new TextField();
		    labelName.setAllowBlank(false);
		    labelName.setValue(initialName);
		    p.add(new FieldLabel(labelName, "Name"), new VerticalLayoutData(1, -1));
		 
		    labelDescription = new TextArea();
		    labelDescription.setValue(initialDescription);
		    labelDescription.setAllowBlank(true);
		    p.add(new FieldLabel(labelDescription, "Description"), new VerticalLayoutData(1, -1));
		}

		public TextField getLabelName() {
			return labelName;
		}

		public TextArea getLabelDescription() {
			return labelDescription;
		}
	}
	
	