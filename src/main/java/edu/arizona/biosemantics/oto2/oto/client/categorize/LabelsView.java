package edu.arizona.biosemantics.oto2.oto.client.categorize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelBottomAppearance;

import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortletsView;
import edu.arizona.biosemantics.oto2.oto.client.categorize.single.SingleLabelView;
import edu.arizona.biosemantics.oto2.oto.client.event.ShowSingleLabelEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class LabelsView extends TabPanel {
	
	private LabelPortletsView labelPortletsView;
	private SingleLabelView singleLabelView;
	private EventBus eventBus;

	public LabelsView(EventBus eventBus, int portalColumnCount) {
		super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		this.eventBus = eventBus;
		labelPortletsView = new LabelPortletsView(eventBus, portalColumnCount);
		singleLabelView = new SingleLabelView(eventBus, portalColumnCount);
		
		this.add(labelPortletsView, "All Categories");
		this.add(singleLabelView, "Single Category");
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(ShowSingleLabelEvent.TYPE, new ShowSingleLabelEvent.ShowSingleLabelHandler() {
			@Override
			public void onSave(ShowSingleLabelEvent event) {
				Label label = event.getLabel();
				LabelsView.this.setActiveWidget(singleLabelView);
			}
		});
	}

	public void setCollection(Collection collection) {
		labelPortletsView.setCollection(collection);
		singleLabelView.setCollection(collection);
	}
}
