package edu.arizona.biosemantics.oto2.oto.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.TabPanel;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.SelectOntologiesDialog;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class TermInfoView extends TabPanel {
	private LocationsView locationsView;
	private ContextView contextView;
	private OntologiesView ontologiesView;

	public TermInfoView(final EventBus eventBus) {
		super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		locationsView = new LocationsView(eventBus);
		contextView = new ContextView(eventBus);
		ontologiesView = new OntologiesView(eventBus);
		add(locationsView, "Locations");
		add(contextView, "Context");
		add(ontologiesView, "Ontologies");
		
		this.addBeforeSelectionHandler(new BeforeSelectionHandler<Widget>() {
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Widget> event) {
				if(event.getItem().equals(ontologiesView) && 
						(ontologiesView.getSelectedOntologies() == null ||
						ontologiesView.getSelectedOntologies().isEmpty())) {
					Dialog dialog = new SelectOntologiesDialog(eventBus);
					dialog.show();
					Alerter.alertNoOntoloygySelected();
					//event.cancel();
				}
			}
		});
		/*this.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				if(event.getSelectedItem().equals(ontologiesView) && 
						ontologiesView.getSelectedOntologies().isEmpty()) {
					Alerter.alertNoOntoloygySelected();
					event.
				}
			}
		});
		this.*/
	}

	public void setCollection(Collection collection) {
		contextView.setCollection(collection);
	}
}