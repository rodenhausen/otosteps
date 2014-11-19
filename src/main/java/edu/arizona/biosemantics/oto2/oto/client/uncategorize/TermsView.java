package edu.arizona.biosemantics.oto2.oto.client.uncategorize;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.Oto;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet;
import edu.arizona.biosemantics.oto2.oto.client.categorize.single.MainTermPortlet;
import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.AllowSurpressSelectEventsListViewSelectionModel;
import edu.arizona.biosemantics.oto2.oto.client.common.AllowSurpressSelectEventsTreeSelectionModel;
import edu.arizona.biosemantics.oto2.oto.client.common.UncategorizeDialog;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.MainTermSynonymsLabelDnd;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.TermDnd;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.TermLabelDnd;
import edu.arizona.biosemantics.oto2.oto.client.event.CommentEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermMarkUselessEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Comment;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;

public class TermsView extends TabPanel {
	
	private class TermMenu extends Menu implements BeforeShowHandler {
		
		public TermMenu() {
			this.addBeforeShowHandler(this);
			this.setWidth(140);
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
			
			List<Term> viewSelected = new LinkedList<Term>();
			if(TermsView.this.getActiveWidget().equals(TermsView.this.termTree)) {
				List<TextTreeNode> nodes = termTreeSelectionModel.getSelectedItems();	
				for(TextTreeNode node : nodes)
					if(node instanceof TermTreeNode) {
						viewSelected.add(((TermTreeNode)node).getTerm());
					} else if(node instanceof BucketTreeNode) {
						viewSelected.addAll(((BucketTreeNode)node).getBucket().getTerms());
					}
			} else if(TermsView.this.getActiveWidget().equals(TermsView.this.listView)) {
				viewSelected = listViewSelectionModel.getSelectedItems();
			}
			
			final List<Term> selected = viewSelected;
			
			if(selected == null || selected.isEmpty()) {
				event.setCancelled(true);
				this.hide();
			} else {				
				final List<Term> terms = new LinkedList<Term>(selected);
				
				if(!collection.getLabels().isEmpty()) {
					Menu categorizeMenu = new Menu();
					VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
					final List<Label> categorizeLabels = new LinkedList<Label>();
					final TextButton categorizeButton = new TextButton("Categorize");
					categorizeButton.setEnabled(false);
					
					FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
					VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
					flowLayoutContainer.add(checkBoxPanel);
					flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
					flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
					for(final Label collectionLabel : collection.getLabels()) {
						CheckBox checkBox = new CheckBox();
						checkBox.setBoxLabel(collectionLabel.getName());
						checkBox.setValue(false);
						checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(ValueChangeEvent<Boolean> event) {
								if(event.getValue())
									categorizeLabels.add(collectionLabel);
								else
									categorizeLabels.remove(collectionLabel);
								categorizeButton.setEnabled(!categorizeLabels.isEmpty());
							}
						});
						checkBoxPanel.add(checkBox);
					}
					verticalLayoutContainer.add(flowLayoutContainer);
					categorizeButton.addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							eventBus.fireEvent(new TermCategorizeEvent(terms, categorizeLabels));
							TermMenu.this.hide();
						}
					});
					verticalLayoutContainer.add(categorizeButton);
					categorizeMenu.add(verticalLayoutContainer);
					MenuItem categorize = new MenuItem("Categorize to");
					categorize.setSubMenu(categorizeMenu);
					this.add(new HeaderMenuItem("Categorize"));
					this.add(categorize);
					
					this.add(new HeaderMenuItem("Term"));
					MenuItem markUseless = new MenuItem("Mark");
					Menu subMenu = new Menu();
					markUseless.setSubMenu(subMenu);
					MenuItem useless = new MenuItem("Useless");
					MenuItem useful = new MenuItem("Useful");
					subMenu.add(useless);
					subMenu.add(useful);
					useless.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new TermMarkUselessEvent(terms, true));
						}
					});
					useful.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new TermMarkUselessEvent(terms, false));
						}
					});
					this.add(markUseless);
				}
				
				if(selected.size() == 1) {
					final Term term = selected.get(0);
					MenuItem rename = new MenuItem("Correct Spelling");
					rename.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							Alerter.dialogRename(eventBus, term, collection);
						}
					});
					this.add(rename);
					/*MenuItem split = new MenuItem("Split Term");
					split.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							final PromptMessageBox box = new PromptMessageBox(
									"Split Term", "Please input splitted terms' separated by space.");
							box.getButton(PredefinedButton.OK).addBeforeSelectHandler(new BeforeSelectHandler() {
								@Override
								public void onBeforeSelect(BeforeSelectEvent event) {
									if(box.getTextField().getValue().trim().isEmpty()) {
										event.setCancelled(true);
										AlertMessageBox alert = new AlertMessageBox("Empty", "Empty not allowed");
										alert.show();
									}
								}
							});
							box.getTextField().setValue(term.getTerm());
							box.getTextField().setAllowBlank(false);
							box.addHideHandler(new HideHandler() {
								@Override
								public void onHide(HideEvent event) {
									String newName = box.getValue();
									eventBus.fireEvent(new TermSplitEvent(term, newName));
								}
							});
							box.show();
						}
					});
					this.add(split);*/
				}
								
				MenuItem comment = new MenuItem("Comment");
				final Term term = selected.get(0);
				comment.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
						box.getTextArea().setValue(getUsersComment(term));
						box.addHideHandler(new HideHandler() {
							@Override
							public void onHide(HideEvent event) {
								Comment newComment = new Comment(Oto.user, box.getValue());
								for(final Term term : selected) {
									collectionService.addComment(newComment, term.getId(), new AsyncCallback<Comment>() {
										@Override
										public void onSuccess(Comment result) {
											eventBus.fireEvent(new CommentEvent(term, result));
											String comment = Format.ellipse(box.getValue(), 80);
											String message = Format.substitute("'{0}' saved", new Params(comment));
											Info.display("Comment", message);
										}
										@Override
										public void onFailure(Throwable caught) {
											Alerter.addCommentFailed(caught);
										}
									});
								}
							}
						});
						box.show();
					}
				});
				this.add(comment);
			}
			
			if(this.getWidgetCount() == 0)
				event.setCancelled(true);
		}
	}
	
	protected String getUsersComment(Term term) {
		for(Comment comment : term.getComments())
			if(comment.getUser().equals(Oto.user))
				return comment.getComment();
		return "";
	}
	
	public static class BucketTreeNode extends TextTreeNode {
		
		private Bucket bucket;

		public BucketTreeNode(Bucket bucket) {
			this.bucket = bucket;
		}

		@Override
		public String getText() {
			return bucket.getName();
		}
		
		public Bucket getBucket() {
			return bucket;
		}

		@Override
		public String getId() {
			return "bucket-" + bucket.getId();
		}
		
	}
		
	private static final TermProperties termProperties = GWT.create(TermProperties.class);
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	
	private TreeStore<TextTreeNode> treeStore;
	private ListStore<Term> listStore;
	
	private Map<Term, TermTreeNode> termTermTreeNodeMap;
	private Map<Bucket, BucketTreeNode> bucketBucketTreeNodeMap;
	private ListView<Term, String> listView;
	private Tree<TextTreeNode, TextTreeNode> termTree;
	private EventBus eventBus;
	private Map<Term, Bucket> termBucketMap;
	private Collection collection;
	private AllowSurpressSelectEventsTreeSelectionModel<TextTreeNode> termTreeSelectionModel = 
			new AllowSurpressSelectEventsTreeSelectionModel<TextTreeNode>();
	private AllowSurpressSelectEventsListViewSelectionModel<Term> listViewSelectionModel = 
			new AllowSurpressSelectEventsListViewSelectionModel<Term>();
	
	public TermsView(EventBus eventBus) {
		super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		this.eventBus = eventBus;
		treeStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
		treeStore.setAutoCommit(true);
		treeStore.addSortInfo(new StoreSortInfo<TextTreeNode>(new Comparator<TextTreeNode>() {
			@Override
			public int compare(TextTreeNode o1, TextTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		listStore = new ListStore<Term>(termProperties.key());
		listStore.setAutoCommit(true);
		listStore.addSortInfo(new StoreSortInfo<Term>(new Term.TermComparator(), SortDir.ASC));
		listView = new ListView<Term, String>(listStore, termProperties.term());
		listView.setSelectionModel(listViewSelectionModel);
		listView.getElement().setAttribute("source", "termsview");
		listView.setContextMenu(new TermMenu());
		termTree = new Tree<TextTreeNode, TextTreeNode>(treeStore, new IdentityValueProvider<TextTreeNode>());
		termTree.setCell(new AbstractCell<TextTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	TextTreeNode textTreeNode, SafeHtmlBuilder sb) {
					String colorHex = "";
					if(textTreeNode instanceof TermTreeNode) {
						TermTreeNode termTreeNode = (TermTreeNode)textTreeNode;
						Term term = termTreeNode.getTerm();
						if(term.getUseless()) {
							sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px; background-color:gray; " +
									"color:white'>" + 
									textTreeNode.getText() + "</div>"));
						} else {
							sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px'>" + textTreeNode.getText() +
									"</div>"));
						}
					} else {
						sb.append(SafeHtmlUtils.fromTrustedString("<div style=''>" + textTreeNode.getText() +
								"</div>"));
					}
			}
		});
		
		termTree.setSelectionModel(termTreeSelectionModel);
		termTree.getElement().setAttribute("source", "termsview");
		termTree.setContextMenu(new TermMenu());
		add(termTree, "Term Groups");
		//add(listView, "list");
		
		bindEvents();
		setupDnD();
	}
	
	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				Term term = event.getTerm();
				if(!listViewSelectionModel.isSelected(term)) {
					List<Term> selection = new LinkedList<Term>();
					selection.add(term);
					listViewSelectionModel.setSelection(selection, true);
				}
				
				TermTreeNode termTreeNode = termTermTreeNodeMap.get(term);
				if(termTreeNode != null && treeStore.findModel(termTreeNode) != null && !termTreeSelectionModel.isSelected(termTreeNode)) {
					List<TextTreeNode> selectionTree = new LinkedList<TextTreeNode>();
					selectionTree.add(termTreeNode);
					termTreeSelectionModel.setSelection(selectionTree, true);
				}
				
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				Label label = event.getLabel();
				for(Term term : label.getMainTerms()) {
					List<Label> labels = collection.getLabels(term);
					if(labels.isEmpty()) {
						addTerm(term);
					}
				}
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				List<Term> terms = event.getTerms();
				for(Term term : terms) {					
					addTerm(term);
				}
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				List<Term> terms = event.getTerms();
				for(Term term : terms) {
					removeTerm(term);
				}
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				Term term = event.getTerm();
				if(listStore.getAll().contains(term)) {
					listStore.update(term);
				}
				if(treeStore.getAll().contains(termTermTreeNodeMap.get(term))) {
					treeStore.update(termTermTreeNodeMap.get(term));
				}
			}
		});
		eventBus.addHandler(TermMarkUselessEvent.TYPE, new TermMarkUselessEvent.MarkUselessTermHandler() {
			@Override
			public void onMark(TermMarkUselessEvent event) {
				List<Term> listStoreContent = listStore.getAll();
				List<TextTreeNode> treeStoreContent = treeStore.getAll();
				for(Term term : event.getTerms()) {
					if(listStoreContent.contains(term))
						listStore.update(term);
					if(termTermTreeNodeMap.get(term) != null && treeStoreContent.contains(termTermTreeNodeMap.get(term))) 
						treeStore.update(termTermTreeNodeMap.get(term));
				}
			}
		});
		
		listViewSelectionModel.addSelectionHandler(new SelectionHandler<Term>() {
			@Override
			public void onSelection(SelectionEvent<Term> event) {
				eventBus.fireEvent(new TermSelectEvent(event.getSelectedItem()));
			}
		});
		termTreeSelectionModel.addSelectionHandler(new SelectionHandler<TextTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TextTreeNode> event) {
				TextTreeNode node = event.getSelectedItem();
				if(node instanceof TermTreeNode) {
					TermTreeNode termTreeNode = (TermTreeNode)node;
					eventBus.fireEvent(new TermSelectEvent(termTreeNode.getTerm()));
				}
			}
		});
	}
	
	protected void removeTerm(Term term) {
		BucketTreeNode bucketTreeNode = bucketBucketTreeNodeMap.get(termBucketMap.get(term));
		TermTreeNode node = termTermTreeNodeMap.get(term);
		treeStore.remove(node);
		if(treeStore.getChildCount(bucketTreeNode) == 0) {
			treeStore.remove(bucketTreeNode);
		}
		listStore.remove(term);
	}

	protected void addTerm(Term term) {
		TermTreeNode node = termTermTreeNodeMap.get(term);
		BucketTreeNode bucketTreeNode = bucketBucketTreeNodeMap.get(termBucketMap.get(term));
		if(treeStore.findModel(bucketTreeNode) == null) 
			treeStore.add(bucketTreeNode);
		treeStore.add(bucketTreeNode, node);
		listStore.add(term);
	}

	private void setupDnD() {		
		ListViewDragSource<Term> dragSource = new ListViewDragSource<Term>(listView) {
			@Override
			 protected void onDragStart(DndDragStartEvent event) {
				 super.onDragStart(event);
				 List<Term> selection = listViewSelectionModel.getSelectedItems();
				 if(selection.isEmpty())
					 event.setCancelled(true);
				 event.setData(new TermDnd(TermsView.this, selection));
			 }
		};
		TreeDragSource<TextTreeNode> treeDragSource = new TreeDragSource<TextTreeNode>(termTree) {
			 @Override
			 protected void onDragStart(DndDragStartEvent event) {
				 super.onDragStart(event);
				 List<TextTreeNode> nodeSelection = termTreeSelectionModel.getSelectedItems();
				 List<Term> selection = new LinkedList<Term>();
				 for(TextTreeNode node : nodeSelection) {
					 if(node instanceof BucketTreeNode) {
						 BucketTreeNode bucketTreeNode = (BucketTreeNode) node;
						 selection.addAll(bucketTreeNode.getBucket().getUncategorizedTerms(collection));
					 }
					 if(node instanceof TermTreeNode) {
						 TermTreeNode termTreeNode = (TermTreeNode) node;
						 selection.add(termTreeNode.getTerm());
					 }
				 }
				 if(selection.isEmpty())
					 event.setCancelled(true);
				 else {
					 setStatusText(selection.size() + " term(s) selected");
					 event.getStatusProxy().update(Format.substitute(getStatusText(), selection.size()));
				 }
				 event.setData(new TermDnd(TermsView.this, selection));
			 }
		};
		
		DropTarget dropTarget = new DropTarget(this) {
			//scrollSupport can only work correctly when initialized once the element to be scrolled is already attached to the page
			private AutoScrollSupport treeScrollSupport;
			private AutoScrollSupport listScrollSupport;
			protected void onDragEnter(DndDragEnterEvent event) {
				super.onDragEnter(event);
				if (treeScrollSupport == null) {
					treeScrollSupport = new AutoScrollSupport(termTree.getElement());
					treeScrollSupport.setScrollRegionHeight(50);
					treeScrollSupport.setScrollDelay(100);
					treeScrollSupport.setScrollRepeatDelay(100);
				}
				if (listScrollSupport == null) {
					listScrollSupport = new AutoScrollSupport(termTree.getElement());
					listScrollSupport.setScrollRegionHeight(50);
					listScrollSupport.setScrollDelay(100);
					listScrollSupport.setScrollRepeatDelay(100);
				}	
				treeScrollSupport.start();
			}		
		};
		dropTarget.setAllowSelfAsSource(false);
		// actual drop action is taken care of by events
		dropTarget.setOperation(Operation.COPY);
		dropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				event.getData();
				if(event.getData() instanceof MainTermSynonymsLabelDnd) {
					MainTermSynonymsLabelDnd mainTermSynonymsLabelDnd = (MainTermSynonymsLabelDnd)event.getData();
					if(mainTermSynonymsLabelDnd.getSource().getClass().equals(MainTermPortlet.class) || 
							mainTermSynonymsLabelDnd.getSource().getClass().equals(LabelPortlet.class)) {
						uncategorize(mainTermSynonymsLabelDnd.getTerms(), mainTermSynonymsLabelDnd.getLabels());
					}
				}
				else if(event.getData() instanceof TermLabelDnd) {
					TermLabelDnd termLabelDnd = (TermLabelDnd)event.getData();
					uncategorize(termLabelDnd.getTerms(), termLabelDnd.getLabels());
				}
			}

			private void uncategorize(List<Term> terms, List<Label> labels) {
				for(Term term : terms) {
					List<Label> currentLabels = collection.getLabels(term);
					if(!labels.containsAll(currentLabels)) {
						UncategorizeDialog dialog = new UncategorizeDialog(eventBus, labels, 
								term, currentLabels);
					} else {
						eventBus.fireEvent(new TermUncategorizeEvent(term, labels));
					}
				}
			}
		});
	}
	
	public void setCollection(Collection collection) {
		this.collection = collection;
		
		termBucketMap = new HashMap<Term, Bucket>();
		bucketBucketTreeNodeMap = new HashMap<Bucket, BucketTreeNode>();
		termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
		treeStore.clear();
		listStore.clear();
					
		for(Bucket bucket : collection.getBuckets()) {
			BucketTreeNode bucketTreeNode = new BucketTreeNode(bucket);
			bucketBucketTreeNodeMap.put(bucket, bucketTreeNode);
			for(Term term : bucket.getTerms()) {
				TermTreeNode termTreeNode = new TermTreeNode(term);
				termBucketMap.put(term, bucket);
				termTermTreeNodeMap.put(term, termTreeNode);
				if(collection.getLabels(term).isEmpty()) {
					addTerm(term);
				}
			}
		}
	}

}