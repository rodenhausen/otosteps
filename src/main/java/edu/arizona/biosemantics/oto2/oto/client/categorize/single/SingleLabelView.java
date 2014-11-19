package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.DragEndEvent;
import com.sencha.gxt.fx.client.DragHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.PortalDropEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet;
import edu.arizona.biosemantics.oto2.oto.client.common.UncategorizeDialog;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.TermDnd;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.TermLabelDnd;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.uncategorize.TermsView;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.LabelProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.MainTermSynonyms;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class SingleLabelView extends SimpleContainer {
	
	public class TermsMenu extends Menu implements BeforeShowHandler {

		public TermsMenu() {
			this.setWidth(140);
			this.addBeforeShowHandler(this);			
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
						
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
			final Set<Term> collapseTerms = new HashSet<Term>();
			final Set<Term> expandTerms = new HashSet<Term>();
			final TextButton collapseExpandButton = new TextButton("Collapse/Expand");
			collapseExpandButton.setEnabled(false);	
			
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
			flowLayoutContainer.add(checkBoxPanel);
			flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
			flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
			for(final Term mainTerm : currentLabel.getMainTerms()) {
				MainTermPortlet portlet = termPortletsMap.get(mainTerm);
				if(portlet != null) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(mainTerm.getTerm());
					checkBox.setValue(portlet.isExpanded());
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue()) {
								expandTerms.add(mainTerm); 
								collapseTerms.remove(mainTerm);
							}
							else {
								collapseTerms.add(mainTerm);
								expandTerms.remove(mainTerm); 
							}
							collapseExpandButton.setEnabled(!collapseTerms.isEmpty() || !expandTerms.isEmpty());
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
						expand(expandTerms);
						collapse(collapseTerms);
						TermsMenu.this.hide();
					}
				});
				verticalLayoutContainer.add(collapseExpandButton);
				collapseExpandMenu.add(verticalLayoutContainer);
				collapseExpand.setSubMenu(collapseExpandMenu);
				this.add(collapseExpand);
			}
		}
	}

	private EventBus eventBus;
	private Collection collection;
	private LabelProperties labelProperites = GWT.create(LabelProperties.class);
	private ListStore<Label> labelStore;
	private PortalLayoutContainer portalLayoutContainer;
	private Map<Term, MainTermPortlet> termPortletsMap = new HashMap<Term, MainTermPortlet>();
	private int portalColumnCount;
	protected Label currentLabel;
	private ComboBox<Label> labelComboBox;
	protected boolean needsRefresh;
	protected AutoProgressMessageBox refreshBox;

	public SingleLabelView(final EventBus eventBus, final int portalColumnCount) {
		super();
		this.eventBus = eventBus;
		this.portalColumnCount = portalColumnCount;
		
		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(createToolBar(),new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(createPortalLayoutContainer(), new VerticalLayoutData(1,1));
		this.setWidget(verticalLayoutContainer);

		setupDnd();
		bindEvents();
	}

	private void setupDnd() {
		DropTarget dropTarget = new DropTarget(this) {
			private AutoScrollSupport scrollSupport;
			
			//scrollSupport can only work correctly when initialized once the element to be scrolled is already attached to the page
			protected void onDragEnter(DndDragEnterEvent event) {
				super.onDragEnter(event);
				//AutoScrollSupport scrollSupport = ((PortalLayoutContainer)LabelPortlet.this.getParentLayoutWidget()).getScrollSupport();
				if (scrollSupport == null) {
					scrollSupport = new AutoScrollSupport(portalLayoutContainer.getElement());
					scrollSupport.setScrollRegionHeight(50);
					scrollSupport.setScrollDelay(100);
					scrollSupport.setScrollRepeatDelay(100);
				}	
				scrollSupport.start();
			}
		};
		dropTarget.setAllowSelfAsSource(false);
		// actual drop action is taken care of by events
		dropTarget.setOperation(Operation.COPY);
		dropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				Object data = event.getData();
				if(data instanceof TermDnd) {
					TermDnd termDnd = (TermDnd)data;
					if(termDnd.getSource().getClass().equals(TermsView.class))
						eventBus.fireEvent(new TermCategorizeEvent(termDnd.getTerms(), currentLabel));
					if(termDnd.getSource().getClass().equals(MainTermPortlet.class) && termDnd instanceof TermLabelDnd) {
						TermLabelDnd termLabelDnd = (TermLabelDnd)termDnd;
						MainTermPortlet sourcePortlet = (MainTermPortlet) termDnd.getSource();
						eventBus.fireEvent(new SynonymRemovalEvent(currentLabel, sourcePortlet.getMainTerm(), termDnd.getTerms()));
					}
				}
			}
		});
	}

	private void bindEvents() {
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				removeMainTerms(event.getTerms());
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(CategorizeCopyRemoveTermEvent event) {
				removeMainTerms(event.getTerms());
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				if(event.getLabels().contains(currentLabel)) {
					addMainTerms(event.getTerms(), 0);
				}
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() { 
			@Override
			public void onCategorize(CategorizeCopyTermEvent event) {
				if(event.getTargetCategories().contains(currentLabel)) {
					addMainTerms(event.getSelectedTerms(), 0);
				}
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(CategorizeMoveTermEvent event) {
				if(event.getTargetCategory().equals(currentLabel)) {
					addMainTerms(event.getSelectedTerms(), 0);
				}
				if(event.getSourceCategory().equals(currentLabel)) {
					removeMainTerms(event.getSelectedTerms());
				}
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(SynonymCreationEvent event) {
				if(event.getLabel().equals(currentLabel)) {
					removeMainTerms(event.getSynonymTerm());
				}
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(SynonymRemovalEvent event) {
				if(event.getLabel().equals(currentLabel)) {
					addMainTerms(event.getSynonyms(), 0);
				}
			}
		});
		
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(LabelCreateEvent event) {
				labelStore.add(event.getLabel());
			}
		});
		eventBus.addHandler(LabelModifyEvent.TYPE, new LabelModifyEvent.ModifyLabelHandler() {
			@Override
			public void onModify(LabelModifyEvent event) {
				labelStore.update(event.getLabel());
				if(event.getLabel().equals(currentLabel))
					refreshToolTip();
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				labelStore.remove(event.getLabel());
			}
		});
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(LabelsMergeEvent event) {
				if(event.getDestination().equals(currentLabel)) {
					for(Label label : event.getSources()) {
						labelStore.remove(label);
						
						for(Term mainTerm : label.getMainTerms()) {
							addMainTerm(mainTerm, 0);
						}
					}
				}
			}
		});
		
		//labelComboBox.addValueChangeHandler(new ValueChangeHandler<Label>() {
		/*
		 * don't refresh on value change. Value's are changed due to LabelSelectEvent from other views and refresh can be expensive. Makes other views "freeze" until done
		 * Instead refresh once this view is selected with current label */
		eventBus.addHandler(LabelSelectEvent.TYPE, new LabelSelectEvent.LabelSelectHandler() {
			@Override
			public void onSelect(LabelSelectEvent event) {
				if(event.getLabel() != null && labelStore.findModel(event.getLabel()) != null) {
					setCurrentLabel(event.getLabel());
				}
			}
		});
		labelComboBox.addSelectionHandler(new SelectionHandler<Label>() {
			@Override
			public void onSelection(SelectionEvent<Label> event) {
				if(event.getSelectedItem() != null && labelStore.findModel(event.getSelectedItem()) != null) {
					needsRefresh = true;
					setCurrentLabel(event.getSelectedItem());
					refresh();
					eventBus.fireEvent(new LabelSelectEvent(currentLabel));
				}
				//if(SingleLabelView.this.isVisible()) {
				//} else
				//	needsRefresh = true;
			}
		});
		this.addShowHandler(new ShowHandler() {
			@Override
			public void onShow(ShowEvent event) {
				refresh();
			}
		});
	}

	protected void removeMainTerms(SelectedTerms selectedTerms) {
		for(MainTermSynonyms mainTermSynonyms : selectedTerms.getMainTermSynonyms())
			removeMainTerm(mainTermSynonyms.getMainTerm());
		removeMainTerms(selectedTerms.getAdditionalTerms());
	}

	protected void addMainTerms(SelectedTerms selectedTerms, int column) {
		for(MainTermSynonyms mainTermSynonym : selectedTerms.getMainTermSynonyms()) {
			addMainTerm(mainTermSynonym.getMainTerm(), column);
			MainTermPortlet portlet = termPortletsMap.get(mainTermSynonym.getMainTerm());
			portlet.addSynonymTerms(mainTermSynonym.getSynonyms());
		}
		addMainTerms(selectedTerms.getAdditionalTerms(), column);
	}

	protected void setCurrentLabel(Label label) {
		needsRefresh = (needsRefresh || currentLabel == null || !currentLabel.equals(label));
		currentLabel = label;
		refreshToolTip();
		labelComboBox.setValue(label, true);
	}

	private void refreshToolTip() {
		if(!currentLabel.getDescription().trim().isEmpty()) {
			this.setToolTip(currentLabel.getName() + ":</br>" + currentLabel.getDescription());
			//this.setTitle(currentLabel.getName() + ":</br>" + currentLabel.getDescription());
		} else {
			this.setToolTip(null);
			//this.setTitle(null);
		}
	}

	public void refresh() {		
		refreshBox = new AutoProgressMessageBox("Progress", "Loading terms, please wait...");
		refreshBox.setProgressText("Loading...");
		refreshBox.setClosable(true);
		refreshBox.auto();
		refreshBox.show();
		if(needsRefresh) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					portalLayoutContainer.clear();
					termPortletsMap.clear();
					for(Term mainTerm : currentLabel.getMainTerms()) {
						addMainTerm(mainTerm, termPortletsMap.size() % portalColumnCount);
					}
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							((CssFloatLayoutContainer)portalLayoutContainer.getContainer()).forceLayout();
							expandNonEmptyCollapseEmpty();
							refreshBox.hide();
						}
					});
					needsRefresh = false;
				}
			});
		} else {
			refreshBox.hide();
		}
	}
	
	protected void addMainTerms(List<Term> mainTerms, int column) {
		final AutoProgressMessageBox box = new AutoProgressMessageBox("Progress", "Loading, please wait...");
		box.setProgressText("Loading...");
		box.setClosable(true);
		box.auto();
		box.show();
		for(Term mainTerm : mainTerms) {
			if(!termPortletsMap.containsKey(mainTerm)) {
				MainTermPortlet mainTermPortlet = new MainTermPortlet(eventBus, collection, currentLabel, mainTerm, portalLayoutContainer);
				portalLayoutContainer.add(mainTermPortlet, column);
				termPortletsMap.put(mainTerm, mainTermPortlet);
			}
		}
		if(this.isVisible()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					((CssFloatLayoutContainer)portalLayoutContainer.getContainer()).forceLayout();
					box.hide();
				}
			});
		} else {
			needsRefresh = true;
			box.hide();
		}
	}

	protected void addMainTerm(Term mainTerm, int column) {
		List<Term> mainTerms = new LinkedList<Term>();
		mainTerms.add(mainTerm);
		addMainTerms(mainTerms, column);
	}

	protected void removeMainTerm(Term mainTerm) {
		List<Term> mainTerms = new LinkedList<Term>();
		mainTerms.add(mainTerm);
		removeMainTerms(mainTerms);
	}

	protected void removeMainTerms(List<Term> mainTerms) {
		final AutoProgressMessageBox box = new AutoProgressMessageBox("Progress", "Loading, please wait...");
		box.setProgressText("Loading...");
		box.setClosable(true);
		box.auto();
		box.show();
		for(Term mainTerm : mainTerms) {
			if(termPortletsMap.containsKey(mainTerm)) {
				MainTermPortlet portlet = termPortletsMap.remove(mainTerm);
				
				//could already be removed by dnd
				if(portalLayoutContainer.getPortletColumn(portlet) != -1) {
					portalLayoutContainer.remove(portlet, portalLayoutContainer.getPortletColumn(portlet));
					portlet.removeFromParent();
				}
			}
		}
		box.hide();
	}

	private ToolBar createToolBar() {
		labelStore = new ListStore<Label>(labelProperites.key());
		labelComboBox = new ComboBox<Label>(labelStore, labelProperites.nameLabel());
		labelComboBox.setForceSelection(true);
		labelComboBox.setTriggerAction(TriggerAction.ALL);
		
		ToolBar toolBar = new ToolBar();
		//toolBar.add(new FillToolItem());
		toolBar.add(new com.google.gwt.user.client.ui.Label("Category:"));
		toolBar.add(labelComboBox);
		return toolBar;
	}

	private PortalLayoutContainer createPortalLayoutContainer() {
		this.portalLayoutContainer = new PortalLayoutContainer(
				portalColumnCount) {
			protected void onPortletDragEnd(DragEndEvent de) {
				//System.out.println("on portlet drag end");
				
				dummy.removeFromParent();

				if (insertCol != -1 && insertRow != -1) {
					if (startCol == insertCol && insertRow > startRow) {
						insertRow--;
					}
					active.setVisible(true);
					active.removeFromParent();
					
					//fix bug in 3.1.0 implementation, where one would drag and drop a panel outside of this container. Due to the already removed "active" from the panel
				    //the size is smaller than calculated by insertRow. Can simply be reproduced with a single cell in a column and dragging and dropping it out of this container.
				    if(insertRow < 0 || insertRow > getWidget(insertCol).getWidgetCount()) {
				    	insertRow = 0;
				    }
				    //end fix
					
					if(dropOnWidget != null && dropOnWidget instanceof MainTermPortlet && active instanceof MainTermPortlet) {
						//System.out.println("synonym creation");
						MainTermPortlet droppedPortlet = (MainTermPortlet)active;
						Term droppedMainTerm = droppedPortlet.getMainTerm();
						MainTermPortlet dropOnPortlet = (MainTermPortlet)dropOnWidget;
						Term droppedOnMainTerm = dropOnPortlet.getMainTerm();
						List<Term> droppedTerms = new LinkedList<Term>();
						droppedTerms.add(droppedMainTerm);
						droppedTerms.addAll(currentLabel.getSynonyms(droppedMainTerm));
						eventBus.fireEvent(new SynonymCreationEvent(currentLabel, droppedOnMainTerm, droppedTerms));
						dropOnWidget = null;
					} else {
						//System.out.println("regular drop");
						getWidget(insertCol).insert(active, insertRow);
						getWidget(insertCol).forceLayout();
	
						fireEvent(new PortalDropEvent(active, startCol, startRow,
							insertCol, insertRow));
					}
				}
				active.setVisible(true);
				active = null;
				insertCol = -1;
				insertRow = -1;
				if (autoScroll) {
					getAutoScrollSupport().stop();
				}
			}
		};
		double portalColumnWidth = 1.0 / portalColumnCount;
		for(int i=0; i<portalColumnCount; i++) {
			portalLayoutContainer.setColumnWidth(i, portalColumnWidth);
		}
		portalLayoutContainer.getElement().getStyle().setBackgroundColor("white");
		portalLayoutContainer.getContainer().setContextMenu(new TermsMenu());
		return portalLayoutContainer;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		labelStore.clear();
		
		portalLayoutContainer.clear();
		for(Label label : collection.getLabels()) {
			labelStore.add(label);
		}
		setCurrentLabel(collection.getLabels().get(0));
	}
	
	protected void collapseAll() {
		for(Term mainTerm : currentLabel.getMainTerms()) {
			MainTermPortlet portlet = termPortletsMap.get(mainTerm);
			if(portlet != null)
				portlet.collapse();
		}
	}
	
	protected void expandAll() {
		for(Term mainTerm : currentLabel.getMainTerms()) {
			MainTermPortlet portlet = termPortletsMap.get(mainTerm);
			if(portlet != null)
				portlet.expand();
		}
	}
	
	protected void expandNonEmptyCollapseEmpty() {
		for(Term mainTerm : currentLabel.getMainTerms()) {
			MainTermPortlet portlet = termPortletsMap.get(mainTerm);
			if(portlet != null) {
				if(currentLabel.hasSynonyms(mainTerm)) 
					portlet.expand();
				else
					portlet.collapse();
			}
		}
	}
	
	protected void collapse(Set<Term> collapseTerms) {
		for(Term collapseTerm : collapseTerms) {
			MainTermPortlet portlet = termPortletsMap.get(collapseTerm);
			if(portlet != null)
				portlet.collapse();
		}
	}

	protected void expand(Set<Term> expandTerms) {
		for(Term expandTerm : expandTerms) {
			MainTermPortlet portlet = termPortletsMap.get(expandTerm);
			if(portlet != null)
				portlet.expand();
		}
	}	
}
