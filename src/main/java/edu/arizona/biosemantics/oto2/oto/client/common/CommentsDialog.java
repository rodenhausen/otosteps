package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.StartEditEvent;
import com.sencha.gxt.widget.core.client.event.StartEditEvent.StartEditHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.Oto;
import edu.arizona.biosemantics.oto2.oto.client.event.CommentEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Comment;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;

public class CommentsDialog extends Dialog {

	private static class CommentEntry {
		private Bucket bucket;
		private List<Label> labels;
		private Term term;
		private Comment comment;

		public CommentEntry(Bucket bucket, List<Label> labels, Term term, Comment comment) {
			this.bucket = bucket;
			this.labels = new ArrayList<Label>(labels);
			Collections.sort(this.labels);
			this.term = term;
			this.comment = comment;
		}
		public Bucket getBucket() {
			return bucket;
		}
		public List<Label> getLabels() {
			return labels;
		}
		public Term getTerm() {
			return term;
		}
		public Comment getComment() {
			return comment;
		}
	}
	
	private static class CommentEntryProperties implements PropertyAccess<CommentEntry> {
		@Path("id")
		ModelKeyProvider<CommentEntry> key() {
			return new ModelKeyProvider<CommentEntry>() {
				@Override
				public String getKey(CommentEntry item) {
					return String.valueOf(item.getComment().getId() + "-" + item.getBucket().getId() + "-" + 
							item.getTerm().getId());
				}
			};
		}

		@Path("user")
		ValueProvider<CommentEntry, String> user() {
			return new ValueProvider<CommentEntry, String>() {
				@Override
				public String getValue(CommentEntry object) {
					return object.getComment().getUser();
				}
				@Override
				public void setValue(CommentEntry object, String value) {	}
				@Override
				public String getPath() {
					return "user";
				}
			};
		}

		@Path("comment")
		ValueProvider<CommentEntry, String> text() {
			return new ValueProvider<CommentEntry, String>() {
				@Override
				public String getValue(CommentEntry object) {
					return object.getComment().getComment();
				}
				@Override
				public void setValue(CommentEntry object, String value) {	
					object.getComment().setComment(value);
				}
				@Override
				public String getPath() {
					return "comment";
				}
			};
		}
		
		@Path("bucket")
		ValueProvider<CommentEntry, String> bucket() {
			return new ValueProvider<CommentEntry, String>() {
				@Override
				public String getValue(CommentEntry object) {
					return object.getBucket().getName();
				}
				@Override
				public void setValue(CommentEntry object, String value) {	}
				@Override
				public String getPath() {
					return "bucket";
				}
			};
		}

		@Path("label")
		ValueProvider<CommentEntry, String> label() {
			return new ValueProvider<CommentEntry, String>() {
				@Override
				public String getValue(CommentEntry object) {
					String labelString = "";
					for(Label label : object.getLabels()) 
						labelString += label.getName() + ", ";
					if(labelString.length() < 2)
						return labelString;
					return labelString.substring(0, labelString.length() - 2);
				}
				@Override
				public void setValue(CommentEntry object, String value) { }
				@Override
				public String getPath() {
					return "label";
				}
			};
		}
		@Path("term")
		ValueProvider<CommentEntry, String> term() {
			return new ValueProvider<CommentEntry, String>() {
				@Override
				public String getValue(CommentEntry object) {
					return object.getTerm().getTerm();
				}
				@Override
				public void setValue(CommentEntry object, String value) { }
				@Override
				public String getPath() {
					return "term";
				}
			};
		}
	}
	
	private EventBus eventBus;
	private Collection collection;
	private ListStore<CommentEntry> store;
	private CommentEntryProperties commentEntryProperties = new CommentEntryProperties();
	private Grid<CommentEntry> grid;
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);

	public CommentsDialog(final EventBus eventBus, Collection collection) {
		this.eventBus = eventBus;
		this.collection = collection;
				
		IdentityValueProvider<CommentEntry> identity = new IdentityValueProvider<CommentEntry>();
		final CheckBoxSelectionModel<CommentEntry> checkBoxSelectionModel = new CheckBoxSelectionModel<CommentEntry>(
				identity);

		checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);

		ColumnConfig<CommentEntry, String> termCol = new ColumnConfig<CommentEntry, String>(
				commentEntryProperties.term(), 100, "Term");
		ColumnConfig<CommentEntry, String> labelCol = new ColumnConfig<CommentEntry, String>(
				commentEntryProperties.label(), 100, "Categories");
		ColumnConfig<CommentEntry, String> bucketCol = new ColumnConfig<CommentEntry, String>(
				commentEntryProperties.bucket(), 100, "Bucket");
		ColumnConfig<CommentEntry, String> userCol = new ColumnConfig<CommentEntry, String>(
				commentEntryProperties.user(), 100, "User");
		final ColumnConfig<CommentEntry, String> textCol = new ColumnConfig<CommentEntry, String>(
				commentEntryProperties.text(), 400, "Comment");

		List<ColumnConfig<CommentEntry, ?>> columns = new ArrayList<ColumnConfig<CommentEntry, ?>>();
		columns.add(checkBoxSelectionModel.getColumn());
		columns.add(userCol);
		columns.add(termCol);
		columns.add(labelCol);
		columns.add(bucketCol);
		columns.add(textCol);
		ColumnModel<CommentEntry> cm = new ColumnModel<CommentEntry>(columns);

		store = new ListStore<CommentEntry>(commentEntryProperties.key());
		store.setAutoCommit(true);
		
		List<CommentEntry> comments = createComments();
		for (CommentEntry comment : comments)
			store.add(comment);

		final GroupingView<CommentEntry> groupingView = new GroupingView<CommentEntry>();
		groupingView.setShowGroupedColumn(false);
		groupingView.setForceFit(true);
		groupingView.groupBy(userCol);

		grid = new Grid<CommentEntry>(store, cm);
		grid.setView(groupingView);
		grid.setContextMenu(createContextMenu());
		grid.setSelectionModel(checkBoxSelectionModel);
		grid.getView().setAutoExpandColumn(textCol);
		grid.setBorders(false);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		
		StringFilter<CommentEntry> termFilter = new StringFilter<CommentEntry>(
				commentEntryProperties.term());
		StringFilter<CommentEntry> labelFilter = new StringFilter<CommentEntry>(
				commentEntryProperties.label());
		//StringFilter<CommentEntry> bucketFilter = new StringFilter<CommentEntry>(
		//		bucketLabelTermCommentProperties.bucket());
		StringFilter<CommentEntry> userFilter = new StringFilter<CommentEntry>(
				commentEntryProperties.user());
		StringFilter<CommentEntry> commentFilter = new StringFilter<CommentEntry>(
				commentEntryProperties.text());

		/*ListStore<String> termFilterStore = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				});
		for(Label label : collection.getLabels())
			termFilterStore.add(label.getName());
		ListFilter<CommentEntry, String> termFilter = new ListFilter<CommentEntry, String>(
				bucketLabelTermCommentProperties.term(), termFilterStore);
		
		ListStore<String> labelFilterStore = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				});
		for(Label label : collection.getLabels())
			labelFilterStore.add(label.getName());
		ListFilter<CommentEntry, String> labelFilter = new ListFilter<CommentEntry, String>(
				bucketLabelTermCommentProperties.label(), labelFilterStore);*/
		
		ListStore<String> bucketFilterStore = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				});
		for(Bucket bucket : collection.getBuckets())
			bucketFilterStore.add(bucket.getName());
		ListFilter<CommentEntry, String> bucketFilter = new ListFilter<CommentEntry, String>(
				commentEntryProperties.bucket(), bucketFilterStore);

		GridFilters<CommentEntry> filters = new GridFilters<CommentEntry>();
		filters.initPlugin(grid);
		filters.setLocal(true);

		filters.addFilter(termFilter);
		filters.addFilter(labelFilter);
		filters.addFilter(bucketFilter);
		filters.addFilter(userFilter);
		filters.addFilter(commentFilter);

		GridInlineEditing<CommentEntry> editing = new GridInlineEditing<CommentEntry>(grid) {
			@Override
			public void startEditing(final GridCell cell) {
				CommentEntry comment = store.get(cell.getRow());
				if(comment.getComment().getUser().equals(Oto.user))
					super.startEditing(cell);
			}
		};
		final TextField editor = new TextField();
		editing.addEditor(textCol, editor);
		//final SetValueValidator setValueValidator = new SetValueValidator(model);	
		editing.addCompleteEditHandler(new CompleteEditHandler<CommentEntry>() {
			@Override
			public void onCompleteEdit(CompleteEditEvent<CommentEntry> event) {			
				GridCell cell = event.getEditCell();
				final CommentEntry comment = grid.getStore().get(cell.getRow());
				ColumnConfig<CommentEntry, String> config = grid.getColumnModel().getColumn(cell.getCol());
				/*if(config.equals(textCol)) {
					switch(comment.getType()) {
						case taxonCharacterValueType:
							Value oldValue = (Value)comment.getObject();
							Character character = model.getTaxonMatrix().getCharacter(oldValue);
							Taxon taxon = model.getTaxonMatrix().getTaxon(oldValue);
							String value = config.getValueProvider().getValue(comment);
							
							ValidationResult validationResult = setValueValidator.validValue(value, character);
							if(validationResult.isValid()) {
								Value newValue = new Value(value);
								comment.setObject(newValue);
								eventBus.fireEvent(new SetValueEvent(taxon, character, oldValue, newValue));
								subModelBus.fireEvent(new SetValueEvent(taxon, character, oldValue, newValue));
							} else {
								AlertMessageBox alert = new AlertMessageBox("Set value failed", "Can't set value " +
										value + " for " + character.getName() + " of " +  taxon.getFullName() + ". Control mode " + 
										model.getControlMode(character).toString().toLowerCase() + " was selected for " + character.getName());
								alert.show();
							}
							
							break;
						default:
							break;
					}
				}*/
				if(config.equals(textCol)) {
					Comment newComment = new Comment(Oto.user, editor.getValue());
					collectionService.addComment(newComment, comment.getTerm().getId(), new AsyncCallback<Comment>() {
						@Override
						public void onSuccess(Comment result) {
							eventBus.fireEvent(new CommentEvent(comment.getTerm(), result));
							String comment = Format.ellipse(editor.getValue(), 80);
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

		setBodyBorder(false);
		setHeadingText("Comments");
		setWidth(800);
		setHeight(600);
		setHideOnButtonClick(true);
		setModal(true);
		setMaximizable(true);

		ContentPanel panel = new ContentPanel();
		panel.add(grid);
		this.add(panel);
	}

	private Menu createContextMenu() {
		Menu menu = new Menu();
		MenuItem removeItem = new MenuItem("Remove");
		menu.add(removeItem);
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (CommentEntry comment : grid.getSelectionModel()
						.getSelectedItems()) {
					store.remove(comment);
					eventBus.fireEvent(new CommentEvent(comment.getTerm(), new Comment(Oto.user, null)));
				}
			}
		});
		return menu;
	}

	private List<CommentEntry> createComments() {
		List<CommentEntry> comments = new LinkedList<CommentEntry>();
		for(Bucket bucket : collection.getBuckets()) {
			for(Term term : bucket.getTerms()) {
				for(Comment comment : term.getComments()) {
					comments.add(new CommentEntry(bucket, collection.getLabels(term), term, comment));
				}
			}
		}
		
		return comments;
	}

}
