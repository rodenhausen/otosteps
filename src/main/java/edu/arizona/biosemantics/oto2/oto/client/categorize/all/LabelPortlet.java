package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent.DndDragLeaveHandler;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.categorize.TermMenu;
import edu.arizona.biosemantics.oto2.oto.client.categorize.single.MainTermPortlet;
import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.AllowSurpressSelectEventsTreeSelectionModel;
import edu.arizona.biosemantics.oto2.oto.client.common.CopyMoveMenu;
import edu.arizona.biosemantics.oto2.oto.client.common.SelectedTermsExtractor;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.MainTermSynonymsLabelDnd;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.TermDnd;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.TermLabelDnd;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.ShowSingleLabelEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.uncategorize.TermsView;
import edu.arizona.biosemantics.oto2.oto.client.uncategorize.TermsView.BucketTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.HighlightLabel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.MainTermSynonyms;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.TrashLabel;

public class LabelPortlet extends Portlet {
		
	public class MainTermTreeNode extends TermTreeNode {
		public MainTermTreeNode(Term term) {
			super(term);
		}
	}
	
	public class SynonymTermTreeNode extends TermTreeNode {
		public SynonymTermTreeNode(Term term) {
			super(term);
		} 
	}
	
	private static int ID;
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);

	private int id = ID++;
	private TreeStore<TermTreeNode> portletStore;
	private Label label;
	private Tree<TermTreeNode, String> tree;
	private AllowSurpressSelectEventsTreeSelectionModel<TermTreeNode> treeSelectionModel;
	private EventBus eventBus;
	private Map<Term, TermTreeNode> termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
	private Collection collection;
	private LabelPortletsView labelPortletsView;
	private ToolButton toolButton;
	private SelectedTermsExtractor selectedTermsExtractor = new SelectedTermsExtractor();
	
	public LabelPortlet(EventBus eventBus, Label label, Collection collection, LabelPortletsView labelPortletsView) {
		this(GWT.<FramedPanelAppearance> create(FramedPanelAppearance.class), eventBus, label, collection, labelPortletsView);
	}
	
	public LabelPortlet(FramedPanelAppearance appearance, final EventBus eventBus, Label label, final Collection collection, LabelPortletsView labelPortletsView) {
		super(appearance);
		this.eventBus = eventBus;
		this.label = label;
		this.collection = collection; 
		this.labelPortletsView = labelPortletsView;
		this.setHeadingText(label.getName());
		this.setExpanded(false);
		this.setAnimationDuration(500);
		this.setCollapsible(true);
		this.setAnimCollapse(true);
		refreshToolTip();
		
		toolButton = new ToolButton(ToolButton.GEAR);
		if(!(this.label instanceof TrashLabel)) {
			this.getHeader().addTool(toolButton);
			this.setContextMenu(new LabelMenu(eventBus, this, collection));
		}
		
		portletStore = new TreeStore<TermTreeNode>(textTreeNodeProperties.key());
		portletStore.setAutoCommit(true);
		portletStore.addSortInfo(new StoreSortInfo<TermTreeNode>(new Comparator<TermTreeNode>() {
			@Override
			public int compare(TermTreeNode o1, TermTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		tree = new Tree<TermTreeNode, String>(portletStore, textTreeNodeProperties.text());
		tree.getElement().setAttribute("source", "labelportlet-" + id);
		treeSelectionModel = new AllowSurpressSelectEventsTreeSelectionModel<TermTreeNode>();
		tree.setSelectionModel(treeSelectionModel);
		tree.setContextMenu(new TermMenu(eventBus, collection, label) {
			@Override
			public SelectedTerms getSelectedTerms() {
				return selectedTermsExtractor.getSelectedTerms(tree);
			}

			@Override
			public List<Term> getExplicitlySelectedTerms() {
				List<Term> result = new LinkedList<Term>();
				for(TermTreeNode node : tree.getSelectionModel().getSelectedItems()) 
					result.add(node.getTerm());
				return result;
			}
		});
		FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
		flowLayoutContainer.add(tree);
		flowLayoutContainer.setScrollMode(ScrollMode.AUTO);
		flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
		
		add(flowLayoutContainer);
		
		setupDnD();
		
		for(Term mainTerm : label.getMainTerms()) {
			addMainTerm(mainTerm);
			for(Term synonym : label.getSynonyms(mainTerm)) 
				this.addSynonymTerm(mainTerm, synonym);
		}
		
		bindEvents();
	}
	
	private void refreshToolTip() {
		if(!label.getDescription().trim().isEmpty()) {
			this.setToolTip(label.getName() + ":</br>" + label.getDescription());
			//this.setTitle(label.getName() + ":</br>" + label.getDescription());
		} else {
			this.setToolTip(null);
			//this.setTitle(null);
		}
	}

	public MainTermTreeNode addMainTerm(Term term) {
		if(!termTermTreeNodeMap.containsKey(term))  {
			MainTermTreeNode mainTermTreeNode = new MainTermTreeNode(term);
			portletStore.add(mainTermTreeNode);
			this.termTermTreeNodeMap.put(term, mainTermTreeNode);
			return mainTermTreeNode;
		}
		return null;
	}
	
	protected SynonymTermTreeNode addSynonymTerm(Term mainTerm, Term synonymTerm) {
		MainTermTreeNode mainTermTreeNode = null;
		TermTreeNode termTreeNode = termTermTreeNodeMap.get(mainTerm);
		if(termTreeNode != null) {
			if(termTreeNode instanceof MainTermTreeNode) {
				mainTermTreeNode = (MainTermTreeNode)termTreeNode;
				SynonymTermTreeNode synonymTermTreeNode = new SynonymTermTreeNode(synonymTerm);
				removeTerm(synonymTerm);
				termTermTreeNodeMap.put(synonymTerm, synonymTermTreeNode);
				portletStore.add(mainTermTreeNode, synonymTermTreeNode);
				this.termTermTreeNodeMap.put(synonymTerm, synonymTermTreeNode);
				return synonymTermTreeNode;
			} 	
		}
		return null;
	}
	
	protected void removeSynonymTerm(Term mainTerm, Term synonym) {
		TermTreeNode termTreeNode = termTermTreeNodeMap.remove(synonym);
		if(termTreeNode != null && termTreeNode instanceof SynonymTermTreeNode) {
			portletStore.remove(termTreeNode);
			this.addMainTerm(synonym);
		}
	}
		
	private void removeTerm(Term term) {
		if(termTermTreeNodeMap.containsKey(term)) {
			TermTreeNode termTreeNode = termTermTreeNodeMap.get(term);
			if(termTreeNode instanceof MainTermTreeNode) {
				MainTermTreeNode mainTermTreeNode = (MainTermTreeNode)termTreeNode;
				List<TermTreeNode> synonyms = portletStore.getChildren(mainTermTreeNode);
				for(TermTreeNode synonym : synonyms)
					this.removeSynonymTerm(term, synonym.getTerm());
			}
			portletStore.remove(termTermTreeNodeMap.remove(term));
		}
	}
	
	private void removeTerms(List<Term> terms) {
		for(Term term : terms)
			this.removeTerm(term);
	}

	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				Term term = event.getTerm();
				TermTreeNode termTreeNode = termTermTreeNodeMap.get(term);
				if(termTreeNode != null) {
					setTreeSelection(termTreeNode);
				}
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(SynonymRemovalEvent event) {
				List<Term> synonyms = event.getSynonyms();
				Term mainTerm = event.getMainTerm();
				Label label = event.getLabel();
				if(LabelPortlet.this.label.equals(label)) {
					for(Term synonym : synonyms) {
						LabelPortlet.this.removeSynonymTerm(mainTerm, synonym);
					}
				}
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(SynonymCreationEvent event) {
				Term mainTerm = event.getMainTerm();
				List<Term> synonymTerms = event.getSynonymTerm();
				Label label = event.getLabel();
				if(LabelPortlet.this.label.equals(label)) {
					for(Term synonymTerm : synonymTerms)
						LabelPortlet.this.addSynonymTerm(mainTerm, synonymTerm);
				}
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				Term term = event.getTerm();
				if(termTermTreeNodeMap.get(term) != null) {
					TermTreeNode termTreeNode = termTermTreeNodeMap.get(term);
					if(portletStore.getAll().contains(termTreeNode)) {
						portletStore.update(termTreeNode);
					}
					//setTreeSelection(termTreeNode);
				}				
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(CategorizeCopyTermEvent event) {
				SelectedTerms selectedTerms = event.getSelectedTerms();
				List<Label> targetCategories = event.getTargetCategories();
				for(Label targetCategory : targetCategories) {
					if(targetCategory.equals(label)) {
						createTerms(selectedTerms);
					}
				}
			}
		});
		eventBus.addHandler(LabelModifyEvent.TYPE, new LabelModifyEvent.ModifyLabelHandler()  {
			@Override
			public void onModify(LabelModifyEvent event) {
				Label label = event.getLabel();
				if(label.equals(LabelPortlet.this.label)) {
					LabelPortlet.this.setHeadingText(label.getName());
					refreshToolTip();
				}
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(CategorizeMoveTermEvent event) {
				Label targetLabel = event.getTargetCategory();
				Label sourceLabel = event.getSourceCategory();
				SelectedTerms selectedTerms = event.getSelectedTerms();
				if(targetLabel.equals(label)) {
					createTerms(selectedTerms);
				}
				if(sourceLabel.equals(label)) {
					for(Term term : selectedTerms.getTerms()) {
						removeTerm(term);
					}
					//LabelPortlet.this.expand();
				}
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				List<Label> labels = event.getLabels();
				List<Term> terms = event.getTerms();
				if(labels.contains(LabelPortlet.this.label)) {
					List<TermTreeNode> nodes = new LinkedList<TermTreeNode>();
					for(Term term : terms) {
						if(!termTermTreeNodeMap.containsKey(term)) {
							MainTermTreeNode node = addMainTerm(term);
							if(node != null)
								nodes.add(node);
						}
					}
					//setTreeSelection(nodes);
				}
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				List<Label> oldLabels = event.getOldLabels();
				List<Term> terms = event.getTerms();
				for(Label oldLabel : oldLabels) {
					if(LabelPortlet.this.label.equals(oldLabel)) {
						for(Term term : terms)
							LabelPortlet.this.removeTerm(term);
					}
					//LabelPortlet.this.expand();
				}
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(CategorizeCopyRemoveTermEvent event) {
				List<Label> labels = event.getLabels();
				List<Term> terms = event.getTerms();
				if(labels.contains(LabelPortlet.this.label)) {
					LabelPortlet.this.removeTerms(terms);
					//LabelPortlet.this.expand();
				}
			}
		});
		
		treeSelectionModel.addSelectionHandler(new SelectionHandler<TermTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TermTreeNode> event) {
				TermTreeNode termTreeNode = event.getSelectedItem();
				Term term = termTreeNode.getTerm();
				eventBus.fireEvent(new TermSelectEvent(term));
				eventBus.fireEvent(new LabelSelectEvent(label));
			}
		});
		toolButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				LabelMenu menu = new LabelMenu(eventBus, LabelPortlet.this, collection);
				menu.show(toolButton);
				eventBus.fireEvent(new LabelSelectEvent(label));
		}});
		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new LabelSelectEvent(label));
			}
		}, ClickEvent.getType());
		this.addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				eventBus.fireEvent(new ShowSingleLabelEvent(label));
			} 
		}, DoubleClickEvent.getType());
	}
	
	protected void createTerms(SelectedTerms selectedTerms) {
		List<TermTreeNode> nodes = new LinkedList<TermTreeNode>();
		for(MainTermSynonyms mainTermSynonyms : selectedTerms.getMainTermSynonyms()) {
			Term mainTerm = mainTermSynonyms.getMainTerm();
			TermTreeNode node = addMainTerm(mainTerm);
			if(node != null)
				nodes.add(node);
			for(Term synonymTerm : mainTermSynonyms.getSynonyms()) {
				TermTreeNode synonymNode = addSynonymTerm(mainTerm, synonymTerm);
				if(synonymNode != null)
					nodes.add(synonymNode);
			}
		}
		for(Term term : selectedTerms.getAdditionalTerms()) {
			TermTreeNode node = addMainTerm(term);
			if(node != null)
				nodes.add(node);
		}
		//setTreeSelection(nodes);
	}

	protected void setTreeSelection(TermTreeNode termTreeNode) {
		List<TermTreeNode> selection = new LinkedList<TermTreeNode>();
		selection.add(termTreeNode);
		this.setTreeSelection(selection);
	}

	protected void setTreeSelection(List<TermTreeNode> termTreeNodes) {
		List<TermTreeNode> selectionTree = new LinkedList<TermTreeNode>();
		selectionTree.addAll(termTreeNodes);
		
		//if this check not made infinite loop will be caused with selectionmodel and select event
		boolean allAlreadySelected = true;
		for(TermTreeNode node : termTreeNodes) {
			allAlreadySelected &= treeSelectionModel.isSelected(node);
		}
		if(!allAlreadySelected) 
			treeSelectionModel.setSelection(selectionTree, true);
		LabelPortlet.this.expand();
	}

	//collapse button will only be available on attach, when initTools is called  in ContentPanel
	@Override
	protected void initTools() {
		super.initTools();
		for(Widget tool : this.getHeader().getTools()) {
			tool.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					eventBus.fireEvent(new LabelSelectEvent(label));
				}
			}, ClickEvent.getType());
		}
	}
	
	private void setupDnD() {
		TreeDragSource<TermTreeNode> dragSource = new TreeDragSource<TermTreeNode>(tree) {
			@Override
			protected void onDragStart(DndDragStartEvent event) {
				super.onDragStart(event);

				MainTermSynonymsLabelDnd mainTermSynonymsLabelDnd = 
						new MainTermSynonymsLabelDnd(LabelPortlet.this, selectedTermsExtractor.getSelectedTerms(tree), label);
				List<Term> selection = mainTermSynonymsLabelDnd.getTerms();
				if (selection.isEmpty())
					event.setCancelled(true);
				else {
					setStatusText(selection.size() + " term(s) selected");
					event.getStatusProxy()
							.update(Format.substitute(getStatusText(),
									selection.size()));
				}
							
				event.setData(mainTermSynonymsLabelDnd);
			}
		};

		DropTarget dropTarget = new DropTarget(this) {
			private AutoScrollSupport scrollSupport;

			//scrollSupport can only work correctly when initialized once the element to be scrolled is already attached to the page
			protected void onDragEnter(DndDragEnterEvent event) {
				super.onDragEnter(event);
				//AutoScrollSupport scrollSupport = ((PortalLayoutContainer)LabelPortlet.this.getParentLayoutWidget()).getScrollSupport();
				if (scrollSupport == null) {
					scrollSupport = new AutoScrollSupport(labelPortletsView.getElement());
					scrollSupport.setScrollRegionHeight(50);
					scrollSupport.setScrollDelay(100);
					scrollSupport.setScrollRepeatDelay(100);
				}	
				scrollSupport.start();
			}
		};
		dropTarget.setAllowSelfAsSource(false);
		dropTarget.setOperation(Operation.COPY);
		
		LabelPortletDndHandler dndHandler = new LabelPortletDndHandler(eventBus, this);
		dropTarget.addDragEnterHandler(dndHandler);
		dropTarget.addDragLeaveHandler(dndHandler);
		dropTarget.addDropHandler(dndHandler);
		
		// let our events take care of tree/list store updates, hence own
		// implementation to take care of move/copy		
		final StoreTargetTreeDropTarget<TermTreeNode> treeDropTarget = new StoreTargetTreeDropTarget<TermTreeNode>(
				tree);
		treeDropTarget.setAllowDropOnLeaf(true);
		treeDropTarget.setAllowSelfAsSource(true);
		treeDropTarget.setOperation(Operation.COPY);
		treeDropTarget.addDropHandler(dndHandler);
		//treeDropTarget.addDragEnterHandler(dndHandler);
		//treeDropTarget.addDragLeaveHandler(dndHandler);
	}
	
	protected List<Term> getSelectedTerms() {
		List<Term> result = new LinkedList<Term>();
		List<TermTreeNode> nodeSelection = treeSelectionModel.getSelectedItems();
		for (TermTreeNode node : nodeSelection) {
			result.add(node.getTerm());
		}
		return result;
	}

	public Label getLabel() {
		return label;
	}
	
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public boolean containsMainTerm(Term term) {
		if(termTermTreeNodeMap.containsKey(term)) 
			return termTermTreeNodeMap.get(term) instanceof MainTermTreeNode;
		return false;
	}

	public Tree<TermTreeNode, String> getTree() {
		return tree;
	}

	public void collapseSynonyms() {
		tree.collapseAll();
	}

	public void expandSynonyms() {
		tree.expandAll();
	}	
	
	@Override
	public void setHeadingText(String text) {
		if(label instanceof HighlightLabel)
			setHeadingHtml("<div style='color: black'>" + text + "</div>");
		else if(label instanceof TrashLabel)
			setHeadingHtml("<div style='color: gray'>" 
					+ text + "</div>");
		else 
			setHeadingHtml("<div style='font-weight: normal'>" 
					+ text + "</div>");
	}

}
