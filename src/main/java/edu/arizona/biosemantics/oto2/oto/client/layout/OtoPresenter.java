package edu.arizona.biosemantics.oto2.oto.client.layout;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.SelectOntologiesDialog;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CommentEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LoadEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SaveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermMarkUselessEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyServiceAsync;

public class OtoPresenter {
	
	private final IOntologyServiceAsync ontologyService = GWT.create(IOntologyService.class);
	private final ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private Collection collection;
	private OtoView view;
	private EventBus eventBus;
	private ModelControler modelControler;
	
	public OtoPresenter(EventBus eventBus) {
		this.eventBus = eventBus;
		this.modelControler = new ModelControler(eventBus);
		bindEvents();
	}
	
	public void setView(OtoView view) {
		this.view = view;
	}

	private void bindEvents() {
		//save triggers
		eventBus.addHandler(CommentEvent.TYPE, new CommentEvent.CommentHandler() {
			@Override
			public void onComment(CommentEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermMarkUselessEvent.TYPE, new TermMarkUselessEvent.MarkUselessTermHandler() {
			@Override
			public void onMark(TermMarkUselessEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(LabelCreateEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(CategorizeCopyTermEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(CategorizeCopyRemoveTermEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(CategorizeMoveTermEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(SynonymCreationEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(SynonymRemovalEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(SaveEvent.TYPE, new SaveEvent.SaveHandler() {
			@Override
			public void onSave(SaveEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(LabelsMergeEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(LabelModifyEvent.TYPE, new LabelModifyEvent.ModifyLabelHandler() {
			@Override
			public void onModify(LabelModifyEvent event) {
				saveCollection();
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				saveCollection();
			}
		});
		
		eventBus.addHandler(LoadEvent.TYPE, new LoadEvent.LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				loadCollection(event.getCollection().getId(), 
						event.getCollection().getSecret(), event.isInitializeFromHistory());
			}
		});
	}

	private void saveCollection() {
		collectionService.update(collection, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {	}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.saveCollectionFailed(caught);
			}
		});
	}

	public void loadCollection(int collectionId, String secret, boolean initializeFromHistory) {		
		Collection collection = new Collection();
		collection.setId(collectionId);
		collection.setSecret(secret);
		final AutoProgressMessageBox box = new AutoProgressMessageBox("Progress", "Loading your data, please wait...");
        box.setProgressText("Loading...");
        box.auto();
        box.show();
        CollectionLoader loader = new CollectionLoader(box);
        if(initializeFromHistory) 
        	collectionService.initializeFromHistory(collection, loader);
        else
	        collectionService.get(collection, loader);
	}
	
	private class CollectionLoader implements AsyncCallback<Collection> {
		private AutoProgressMessageBox box;
		public CollectionLoader(AutoProgressMessageBox box) {
			this.box = box;
		}
		@Override
		public void onSuccess(Collection result) {
			modelControler.setCollection(result);
			OtoPresenter.this.collection = result;
			view.setCollection(result);
			
			//already store ontologies, otherwise delay when requested on button press
			ontologyService.getOntologies(new AsyncCallback<Set<Ontology>>() {
				@Override
				public void onSuccess(Set<Ontology> result) {
					SelectOntologiesDialog.setOntologies(result);
					box.hide();
				}
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					box.hide();
				}
			});
		}
		@Override
		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			box.hide();
			Alerter.alertFailedToLoadCollection();
		}
	}
}
