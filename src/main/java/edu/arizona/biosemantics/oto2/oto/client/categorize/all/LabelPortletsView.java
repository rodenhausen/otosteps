package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.LabelAddDialog;
import edu.arizona.biosemantics.oto2.oto.client.common.LabelInfoContainer;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.HighlightLabel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto2.oto.shared.model.TrashLabel;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;

public class LabelPortletsView extends PortalLayoutContainer {
	
	public class LabelsMenu extends Menu implements BeforeShowHandler {

		public LabelsMenu() {
			this.setWidth(200);
			this.addBeforeShowHandler(this);			
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
			
			this.add(new HeaderMenuItem("Category"));
			MenuItem add = new MenuItem("Add");
			add.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					LabelAddDialog labelAddDialog = new LabelAddDialog(eventBus, collection);
					labelAddDialog.show();
				}
			});
			
			this.add(add);
			
			this.add(new HeaderMenuItem("View"));			
			MenuItem expand = new MenuItem("Expand All");
			expand.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					expandAll();
				}
			});
			this.add(expand);
			MenuItem collapse = new MenuItem("Collapse All");
			collapse.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collapseAll();
				}
			});
			this.add(collapse);
			
			MenuItem expandCollapseEmpty = new MenuItem("Expand Non-empty");
			expandCollapseEmpty.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					expandNonEmptyCollapseEmpty();
				}
			});
			this.add(expandCollapseEmpty);
			
			MenuItem collapseExpand = new MenuItem("Collapse/Expand");
			Menu collapseExpandMenu = new Menu();
			VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
			final Set<Label> collapseLabels = new HashSet<Label>();
			final Set<Label> expandLabels = new HashSet<Label>();
			final TextButton collapseExpandButton = new TextButton("Collapse/Expand");
			collapseExpandButton.setEnabled(false);	
			
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
			flowLayoutContainer.add(checkBoxPanel);
			flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
			flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
			for(final Label collectionLabel : collection.getLabels()) {
				LabelPortlet portlet = labelPortletsMap.get(collectionLabel);
				if(portlet != null) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(collectionLabel.getName());
					checkBox.setValue(portlet.isExpanded());
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue()) {
								expandLabels.add(collectionLabel); 
								collapseLabels.remove(collectionLabel);
							}
							else {
								collapseLabels.add(collectionLabel);
								expandLabels.remove(collectionLabel); 
							}
							collapseExpandButton.setEnabled(!collapseLabels.isEmpty() || !expandLabels.isEmpty());
						}
					});
					checkBoxPanel.add(checkBox);
				}
			}
			verticalLayoutContainer.add(flowLayoutContainer);
			if(verticalLayoutContainer.getWidgetCount() > 0) {
				collapseExpandButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						expand(expandLabels);
						collapse(collapseLabels);
						LabelsMenu.this.hide();
					}
				});
				verticalLayoutContainer.add(collapseExpandButton);
				collapseExpandMenu.add(verticalLayoutContainer);
				collapseExpand.setSubMenu(collapseExpandMenu);
				this.add(collapseExpand);
			}
			
			MenuItem expandSynonymGroups = new MenuItem("Expand Synonym Groups");
			expandSynonymGroups.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					expandSynonymGroups();
				}
			});
			this.add(expandSynonymGroups);
			
			MenuItem collapseSynonymGroups = new MenuItem("Collapse Synonym Groups");
			collapseSynonymGroups.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collapseSynonymGroups();
				}
			});
			this.add(collapseSynonymGroups);
		}
	}
	


	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private EventBus eventBus;
	private int portalColumnCount;
	private Map<Label, LabelPortlet> labelPortletsMap = new HashMap<Label, LabelPortlet>();
	private Collection collection;
	
	public LabelPortletsView(EventBus eventBus, int portalColumnCount) {
		super(portalColumnCount);
		this.eventBus = eventBus;
		this.portalColumnCount = portalColumnCount;
		double portalColumnWidth = 1.0 / portalColumnCount;
		for(int i=0; i<portalColumnCount; i++) {
			this.setColumnWidth(i, portalColumnWidth);
		}
		this.getElement().getStyle().setBackgroundColor("white");
		this.getContainer().setContextMenu(new LabelsMenu());
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(LabelsMergeEvent event) {
				List<Label> sources = event.getSources();
				Label destination = event.getDestination();
				for(Label source : sources) {
					LabelPortlet sourcePortlet = labelPortletsMap.remove(source);
					sourcePortlet.removeFromParent();
					//LabelsView.this.remove(sourcePortlet, LabelsView.this.getPortletColumn(sourcePortlet));
					LabelPortlet destinationPortlet = labelPortletsMap.get(destination);
					for(Term term : source.getMainTerms()) {
						if(destination.isSynonym(term)) {
							Term destinationMainTerm = destination.getMainTermOfSynonym(term);
							destinationPortlet.addSynonymTerm(destinationMainTerm, term);
						} else if(destination.isMainTerm(term) && !destinationPortlet.containsMainTerm(term)) {
							destinationPortlet.addMainTerm(term);
						}
						for(Term synonym : source.getSynonyms(term))
							if(destination.isSynonym(term)) {
								Term destinationMainTerm = destination.getMainTermOfSynonym(term);
								if(!destinationMainTerm.equals(synonym))
									destinationPortlet.addSynonymTerm(destinationMainTerm, synonym);
							} else if(destination.isMainTerm(term) && destination.isSynonym(term, synonym) && !destinationPortlet.containsMainTerm(synonym))
								destinationPortlet.addSynonymTerm(term, synonym);
					}
				}
			}
		});
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(LabelCreateEvent event) {
				Label label = event.getLabel();
				LabelPortlet labelPortlet = createLabelPortlet(label);
				add(labelPortlet, 0);
				labelPortletsMap.put(label, labelPortlet);
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				Label label = event.getLabel();
				LabelPortlet portlet = labelPortletsMap.remove(label);
				LabelPortletsView.this.remove(portlet, LabelPortletsView.this.getPortletColumn(portlet));
				portlet.removeFromParent();
			}
		});
	}

	protected LabelPortlet createLabelPortlet(Label label) {
		//labelPortlet = new LabelPortlet(GWT.<OtoFramedPanelAppearance> create(OtoFramedPanelAppearance.class), 
		//		eventBus, label, collection);
		LabelPortlet labelPortlet = new LabelPortlet(eventBus, label, collection, this);
		labelPortlet.setHeadingText(label.getName());
		return labelPortlet;
	}

	public void setCollection(final Collection collection) {
		this.collection = collection;

		clear();
		labelPortletsMap.clear();
		for(Label label : collection.getLabels()) {
			LabelPortlet labelPortlet = createLabelPortlet(label);
			labelPortlet.collapse();
			//add(labelPortlet, labelPortletsMap.size() % portalColumnCount);
			add(labelPortlet, getColumn(labelPortlet));
			labelPortletsMap.put(label, labelPortlet);
		}
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				forceLayout();
				expandNonEmptyCollapseEmpty();
			}
		});
	}

	private int getColumn(LabelPortlet labelPortlet) {
		//if default categories 
		// else labelPortletsMap.size() % portalColumnCount
		switch(labelPortlet.getLabel().getName()) {
		case "structure":
		case "structure_in_adjective_form":
		case "structure_subtype":
		case "taxon_name":
			return 0;
		case "architecture":
		case "arrangement":
		case "character":
		case "course":
		case "dehiscence":
		case "growth_form":
		case "growth_order":
		case "orientation":
		case "position":
		case "position_relational":
		case "prominence":
		case "quantity":
		case "shape":
			return 1;
		case "coating":
		case "pubescence":
		case "relief":
		case "texture":
		case "coloration":
		case "reflectance":
			return 2;
		case "fixation":
		case "fusion":
			return 3;
		case "depth":
		case "height":
		case "length":
		case "width":
		case "size":
		case "density":
			return 4;
		case "distribution":
		case "habitat":
		case "location":
			return 5;
		case "odor":
		case "taste":
		case "toxicity":
		case "function":
		case "substance":
			return 6;
		case "life_cycle":
		case "development":
		case "germination":
		case "derivation":
		case "maturation":
		case "nutrition":
		case "reproduction":
		case "ploidy":
			return 7;
		case "season":
		case "condition":
		case "behavior":
		case "duration":
		case "fragility":
		case "variability":
			return 8;
		default:
			return 0;
		}
	}

	protected void expandNonEmptyCollapseEmpty() {
		for(Label label : collection.getLabels()) {
			LabelPortlet portlet = labelPortletsMap.get(label);
			if(portlet != null) {
				if(label.hasTerms()) 
					portlet.expand();
				else
					portlet.collapse();
			}
		}
	}
	
	protected void expandAll() {
		for(Label label : collection.getLabels()) {
			LabelPortlet portlet = labelPortletsMap.get(label);
			if(portlet != null)
				portlet.expand();
		}
	}
	
	protected void collapseAll() {
		for(Label label : collection.getLabels()) {
			LabelPortlet portlet = labelPortletsMap.get(label);
			if(portlet != null)
				portlet.collapse();
		}
	}
	
	protected void collapse(Set<Label> collapseLabels) {
		for(Label collapseLabel : collapseLabels) {
			LabelPortlet portlet = labelPortletsMap.get(collapseLabel);
			if(portlet != null)
				portlet.collapse();
		}
	}

	protected void expand(Set<Label> expandLabels) {
		for(Label expandLabel : expandLabels) {
			LabelPortlet portlet = labelPortletsMap.get(expandLabel);
			if(portlet != null)
				portlet.expand();
		}
	}
	
	protected void collapseSynonymGroups() {
		for(Label label : labelPortletsMap.keySet())
			labelPortletsMap.get(label).collapseSynonyms();
	}

	protected void expandSynonymGroups() {
		for(Label label : labelPortletsMap.keySet())
			labelPortletsMap.get(label).expandSynonyms();
	}

	public void forceLayout() {
		((CssFloatLayoutContainer)getContainer()).forceLayout();
	}
	
}