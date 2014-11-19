package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.LabelModifyDialog;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class LabelMenu extends Menu implements BeforeShowHandler {

	private EventBus eventBus;
	private LabelPortlet labelPortlet;
	private Collection collection;

	public LabelMenu(EventBus eventBus, final LabelPortlet labelPortlet, final Collection collection) {
		this.eventBus = eventBus;
		this.labelPortlet = labelPortlet;
		this.collection = collection;
		this.addBeforeShowHandler(this);
		this.setWidth(200);
	}

	@Override
	public void onBeforeShow(BeforeShowEvent event) {
		this.clear();
		
		this.add(new HeaderMenuItem("Category"));
		MenuItem modify = new MenuItem("Modify");
		modify.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				LabelModifyDialog modifyDialog = new LabelModifyDialog(eventBus, labelPortlet.getLabel());
				modifyDialog.show();
			}
		});
		this.add(modify);
		MenuItem remove = new MenuItem("Remove");
		remove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new LabelRemoveEvent(labelPortlet.getLabel()));
			}
		});
		this.add(remove);
		
		if(collection.getLabels().size() > 1) {
			MenuItem merge = new MenuItem("Merge with");
			Menu mergeMenu = new Menu();
			VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
			final List<Label> mergeLabels = new LinkedList<Label>();
			final TextButton mergeButton = new TextButton("Merge");
			mergeButton.setEnabled(false);
			
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
			flowLayoutContainer.add(checkBoxPanel);
			flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
			flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
			for(final Label collectionLabel : collection.getLabels()) {
				if(!labelPortlet.getLabel().equals(collectionLabel)) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(collectionLabel.getName());
					checkBox.setValue(false);
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue())
								mergeLabels.add(collectionLabel);
							else
								mergeLabels.remove(collectionLabel);
							mergeButton.setEnabled(!mergeLabels.isEmpty());
						}
					});
					checkBoxPanel.add(checkBox);
				}
			}
			verticalLayoutContainer.add(flowLayoutContainer);
			mergeButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					LabelMenu.this.hide();
					Alerter.mergeWarning(eventBus, new LabelsMergeEvent(labelPortlet.getLabel(), mergeLabels));
				}
			});
			verticalLayoutContainer.add(mergeButton);
			mergeMenu.add(verticalLayoutContainer);
			merge.setSubMenu(mergeMenu);
			this.add(merge);
			
			this.add(new HeaderMenuItem("View"));
			MenuItem expandSynonymGroups = new MenuItem("Expand Synonym Groups");
			expandSynonymGroups.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					labelPortlet.expandSynonyms();
				}
			});
			this.add(expandSynonymGroups);
			
			MenuItem collapseSynonymGroups = new MenuItem("Collapse Synonym Groups");
			collapseSynonymGroups.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					labelPortlet.collapseSynonyms();
				}
			});
			this.add(collapseSynonymGroups);
		}
		
		if(this.getWidgetCount() == 0)
			event.setCancelled(true);
	}
}
