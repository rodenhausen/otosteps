package edu.arizona.biosemantics.oto2.oto.client.categorize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.Oto;
import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.LabelAddDialog;
import edu.arizona.biosemantics.oto2.oto.client.common.UncategorizeDialog;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CommentEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SetUserEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Comment;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.LabelProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;

public abstract class TermMenu extends Menu implements BeforeShowHandler {
	
	private static final LabelProperties labelProperties = GWT.create(LabelProperties.class);
	
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	protected EventBus eventBus;
	protected Collection collection;
	protected Label label;
	
	public TermMenu(EventBus eventBus, Collection collection, Label label) {
		this.eventBus = eventBus;
		this.collection = collection;
		this.label = label;
		this.addBeforeShowHandler(this);
		this.setWidth(140);
	}

	@Override
	public void onBeforeShow(BeforeShowEvent event) {
		this.clear();
		
		final SelectedTerms selectedTerms = getSelectedTerms();
		List<Term> explicitSelection = getExplicitlySelectedTerms();
		if(explicitSelection == null || explicitSelection.isEmpty()) {
			event.setCancelled(true);
			this.hide();
		} else {
			buildMenu(explicitSelection, selectedTerms);
		}
			
		if(this.getWidgetCount() == 0)
			event.setCancelled(true);
	}

	public void buildMenu(List<Term> explicitSelection, SelectedTerms selectedTerms) {
		this.add(new HeaderMenuItem("Categorization"));
		createMoveTo(explicitSelection, selectedTerms);
		createCopy(explicitSelection, selectedTerms);
		createRemove(explicitSelection, selectedTerms);
		this.add(new HeaderMenuItem("Synonymization"));
		createAddSynonom(explicitSelection, selectedTerms);
		createRemoveSynonym(explicitSelection, selectedTerms);
		createRemoveAllSynonyms(explicitSelection, selectedTerms);
		this.add(new HeaderMenuItem("Term"));
		createRename(explicitSelection, selectedTerms);
		createComment(explicitSelection, selectedTerms);

	}

	protected void createComment(final List<Term> explicitSelection, SelectedTerms selectedTerms) {
		if(explicitSelection.size() >= 1) {
			MenuItem comment = new MenuItem("Comment");
			final Term term = explicitSelection.get(0);
			comment.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
					box.getTextArea().setValue(getUsersComment(term));
					box.addHideHandler(new HideHandler() {
						@Override
						public void onHide(HideEvent event) {
							Comment newComment = new Comment(Oto.user, box.getValue());
							for(final Term term : explicitSelection) {
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
	}

	protected String getUsersComment(Term term) {
		for(Comment comment : term.getComments())
			if(comment.getUser().equals(Oto.user))
				return comment.getComment();
		return "";
	}

	protected void createRemoveAllSynonyms(final List<Term> explicitSelection, final SelectedTerms selectedTerms) {
		boolean showRemoveAllSynonyms = false;
		for(Term term : explicitSelection) {
			if(!label.getSynonyms(term).isEmpty()) {
				showRemoveAllSynonyms = true;
			}
		}
		if(showRemoveAllSynonyms) {
			MenuItem removeAllSynonyms = new MenuItem("Remove all Synonyms");
			this.add(removeAllSynonyms);
			removeAllSynonyms.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					
					for(Term term : explicitSelection) {
						if(label.isMainTerm(term)) {
							List<Term> oldSynonyms = label.getSynonyms(term);
							eventBus.fireEvent(new SynonymRemovalEvent(label, term, oldSynonyms));
						}
					}
				}
			});
		}
	}

	protected void createRemoveSynonym(final List<Term> explicitSelection, final SelectedTerms selectedTerms) {
		if(explicitSelection.size() == 1 && label.isMainTerm(explicitSelection.get(0))) {
			final Term term = explicitSelection.get(0);
			if(!label.getSynonyms(term).isEmpty()) {
				Menu synonymMenu = new Menu();
				VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
				final List<Term> toRemove = new LinkedList<Term>();
				final TextButton synonymRemoveButton = new TextButton("Remove");
				synonymRemoveButton.setEnabled(false);
				
				FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
				VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
				flowLayoutContainer.add(checkBoxPanel);
				flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
				flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
				for(final Term synonymTerm : label.getSynonyms(term)) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(synonymTerm.getTerm());
					checkBox.setValue(false);
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue())
								toRemove.add(synonymTerm);
							else
								toRemove.remove(synonymTerm);
							synonymRemoveButton.setEnabled(!toRemove.isEmpty());
						}
					});
					checkBoxPanel.add(checkBox);
				}
				verticalLayoutContainer.add(flowLayoutContainer);
				if(verticalLayoutContainer.getWidgetCount() > 0) {
					synonymRemoveButton.addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							eventBus.fireEvent(new SynonymRemovalEvent(label, term, toRemove));
							TermMenu.this.hide();
						}
					});
					verticalLayoutContainer.add(synonymRemoveButton);
					synonymMenu.add(verticalLayoutContainer);
					MenuItem removeSynonym = new MenuItem("Remove Synonym");
					removeSynonym.setSubMenu(synonymMenu);
					this.add(removeSynonym);
				}	
			}
		}
	}

	protected void createAddSynonom(final List<Term> explicitSelection, final SelectedTerms selectedTerms) {
		if(explicitSelection.size() == 0 && label.getMainTerms().size() > 1 && 
				label.isMainTerm(explicitSelection.get(0))) {
			final Term term = explicitSelection.get(0);
			Menu synonymMenu = new Menu();
			
			VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
			final List<Term> synonymTerms = new LinkedList<Term>();
			final TextButton synonymButton = new TextButton("Synonomize");
			synonymButton.setEnabled(false);
			
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
			flowLayoutContainer.add(checkBoxPanel);
			flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
			flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
			for(final Term synonymTerm : label.getMainTerms()) {
				if(!synonymTerm.equals(term)) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(synonymTerm.getTerm());
					checkBox.setValue(false);
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue())
								synonymTerms.add(synonymTerm);
							else
								synonymTerms.remove(synonymTerm);
							synonymButton.setEnabled(!synonymTerms.isEmpty());
						}
					});
					checkBoxPanel.add(checkBox);
				}
			}
			verticalLayoutContainer.add(flowLayoutContainer);
			if(verticalLayoutContainer.getWidgetCount() > 0) {
				synonymButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						eventBus.fireEvent(new SynonymCreationEvent(label, term, synonymTerms));
						TermMenu.this.hide();
					}
				});
				verticalLayoutContainer.add(synonymButton);
				synonymMenu.add(verticalLayoutContainer);
				MenuItem addSynonym = new MenuItem("Add Synonym");
				addSynonym.setSubMenu(synonymMenu);
				this.add(addSynonym);
			}					
		}
	}

	protected void createRemove(final List<Term> explicitSelection, final SelectedTerms selectedTerms) {
		MenuItem remove = new MenuItem("Remove");
		remove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for(Term term : selectedTerms.getTerms()) {
					List<Label> labels = collection.getLabels(term);
					if(labels.size() > 1) {
						UncategorizeDialog dialog = new UncategorizeDialog(eventBus, label, 
								term, labels);
					} else {
						eventBus.fireEvent(new TermUncategorizeEvent(term, label));
					}
				}
			}
		});
		this.add(remove);
	}

	protected void createRename(final List<Term> explicitSelection, final SelectedTerms selectedTerms) {
		if(explicitSelection.size() == 1) {
			MenuItem rename = new MenuItem("Correct Spelling");
			final Term term = explicitSelection.get(0);
			rename.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					Alerter.dialogRename(eventBus, term, collection);
				}
			});
			this.add(rename);
		}
	}

	protected void createCopy(final List<Term> explicitSelection, final SelectedTerms selectedTerms) {
		if(collection.getLabels().size() > 1) {
			Menu copyMenu = new Menu();
			VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
			final List<Label> copyLabels = new LinkedList<Label>();
			final TextButton copyButton = new TextButton("Copy");
			copyButton.setEnabled(false);
			
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
			flowLayoutContainer.add(checkBoxPanel);
			flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
			flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
			for(final Label collectionLabel : collection.getLabels()) {
				if(!label.equals(collectionLabel) && !collectionLabel.getMainTerms().containsAll(selectedTerms.getTerms())) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(collectionLabel.getName());
					checkBox.setValue(false);
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue())
								copyLabels.add(collectionLabel);
							else
								copyLabels.remove(collectionLabel);
							copyButton.setEnabled(!copyLabels.isEmpty());
						}
					});
					checkBoxPanel.add(checkBox);
				}
			}
			verticalLayoutContainer.add(flowLayoutContainer);
			if(verticalLayoutContainer.getWidgetCount() > 0) {
				copyButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						eventBus.fireEvent(new CategorizeCopyTermEvent(selectedTerms, label, copyLabels));
						TermMenu.this.hide();
					}
				});
				verticalLayoutContainer.add(copyButton);
				copyMenu.add(verticalLayoutContainer);
				MenuItem copy = new MenuItem("Copy to");
				copy.setSubMenu(copyMenu);
				this.add(copy);
			}
		}
	}

	protected void createMoveTo(final List<Term> explicitSelection, final SelectedTerms selectedTerms) {
		if(collection.getLabels().size() > 1) {
			Menu moveMenu = new Menu();
			
			ListStore<Label> labelStore = new ListStore<Label>(labelProperties.key());
			final ComboBox<Label> moveComboBox = new ComboBox<Label>(labelStore, labelProperties.nameLabel());
			for(final Label collectionLabel : collection.getLabels())
				if(!label.equals(collectionLabel) && !collectionLabel.getMainTerms().containsAll(selectedTerms.getTerms())) {
					labelStore.add(collectionLabel);
				}
			
			TextButton moveButton = new TextButton("Move");
			moveButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					eventBus.fireEvent(new CategorizeMoveTermEvent(selectedTerms, label, moveComboBox.getValue()));
					TermMenu.this.hide();
				}
			});
			moveMenu.add(moveComboBox);
			moveMenu.add(moveButton);
			if(labelStore.size() > 0) {
				MenuItem move = new MenuItem("Move to");
				move.setSubMenu(moveMenu);
				this.add(move);
			}
		}
	}

	public abstract SelectedTerms getSelectedTerms();
	

	public abstract List<Term> getExplicitlySelectedTerms();
	
}