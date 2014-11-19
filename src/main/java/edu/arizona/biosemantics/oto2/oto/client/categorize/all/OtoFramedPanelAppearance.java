package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.blue.client.panel.BlueFramedPanelAppearance;
import com.sencha.gxt.theme.blue.client.panel.BlueHeaderFramedAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.Template;

public class OtoFramedPanelAppearance extends BlueFramedPanelAppearance {

	  @Override
	  public HeaderDefaultAppearance getHeaderAppearance() {
		  
	    return new BlueHeaderFramedAppearance(GWT.<OtoHeaderResources> create(OtoHeaderResources.class), 
	    		GWT.<Template> create(Template.class));
	  }
	
}
