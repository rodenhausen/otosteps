package edu.arizona.biosemantics.oto2.oto.client.common;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class HelpView implements IsWidget {

	@Override
	public Widget asWidget() {
		TabPanel panel = new TabPanel();
		panel.setBorders(false);
		
		Instructions instructions = new Instructions();
		LearnTermCategorization learnTermCategorization = new LearnTermCategorization();

		panel.add(instructions, new TabItemConfig("Instructions"));
		panel.add(learnTermCategorization, new TabItemConfig("Learn Term Categorization"));
		return panel;
	}

	

}
