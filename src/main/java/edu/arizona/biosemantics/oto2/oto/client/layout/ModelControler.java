package edu.arizona.biosemantics.oto2.oto.client.layout;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
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
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Comment;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.MainTermSynonyms;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;

public class ModelControler {

	private Collection collection;
	private EventBus eventBus;
	
	public ModelControler(EventBus eventBus) { 
		this.eventBus = eventBus;
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(CommentEvent.TYPE, new CommentEvent.CommentHandler() {
			@Override
			public void onComment(CommentEvent event) {
				setComment(event.getTerms(), event.getComment());
			}
		});
		eventBus.addHandler(TermMarkUselessEvent.TYPE, new TermMarkUselessEvent.MarkUselessTermHandler() {
			@Override
			public void onMark(TermMarkUselessEvent event) {
				markUseless(event.getTerms(), event.isNewUseless());
			}
		});
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(LabelCreateEvent event) {
				addLabel(event.getLabel());
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(CategorizeCopyTermEvent event) {
				SelectedTerms selectedTerms = event.getSelectedTerms();
				categorizeTerms(selectedTerms.getTerms(), event.getTargetCategories());
				for(MainTermSynonyms mainTermSynonyms : selectedTerms.getMainTermSynonyms())
					for(Label targetCategory : event.getTargetCategories()) 
						createSynonym(targetCategory, mainTermSynonyms.getMainTerm(), mainTermSynonyms.getSynonyms());
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(CategorizeCopyRemoveTermEvent event) {
				List<Label> labels = new LinkedList<Label>();
				labels.addAll(event.getLabels());
				uncategorizeTerms(event.getTerms(), labels);
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(CategorizeMoveTermEvent event) {
				SelectedTerms selectedTerms = event.getSelectedTerms();
				moveTerms(selectedTerms.getTerms(), event.getSourceCategory(), event.getTargetCategory());
				for(MainTermSynonyms mainTermSynonyms : selectedTerms.getMainTermSynonyms())
					createSynonym(event.getTargetCategory(), mainTermSynonyms.getMainTerm(), mainTermSynonyms.getSynonyms());
				
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(SynonymCreationEvent event) {
				createSynonym(event.getLabel(), event.getMainTerm(), event.getSynonymTerm());
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(SynonymRemovalEvent event) {
				removeSynonym(event.getLabel(), event.getMainTerm(), event.getSynonyms());
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				categorizeTerms(event.getTerms(), event.getLabels());
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				uncategorizeTerms(event.getTerms(), event.getOldLabels());
			}
		});
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(LabelsMergeEvent event) {
				mergeLabels(event.getSources(), event.getDestination());
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				removeLabel(event.getLabel());
			}
		});
		eventBus.addHandler(LabelModifyEvent.TYPE, new LabelModifyEvent.ModifyLabelHandler() {
			@Override
			public void onModify(LabelModifyEvent event) {
				modifyLabel(event.getLabel(), event.getNewName(), event.getNewDescription());
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				renameTerm(event.getTerm(), event.getNewName());
			}
		});
	}

	protected void setComment(List<Term> terms, Comment comment) {
		for(Term term : terms)
			term.setComment(comment);
	}

	protected void markUseless(List<Term> terms, boolean newUseless) {
		for(Term term : terms)
			term.setUseless(newUseless);
	}

	protected void renameTerm(Term term, String newName) {
		term.setTerm(newName);
	}

	protected void removeSynonym(Label label, Term mainTerm, List<Term> synonyms) {
		label.removeSynonymy(mainTerm, synonyms);
	}

	protected void createSynonym(Label label, Term mainTerm, List<Term> synonymTerms) {
		label.addSynonymy(mainTerm, synonymTerms, true);
	}

	protected void mergeLabels(List<Label> sources, Label destination) {
		collection.removeLabels(sources);
		for(Label mergeLabel : sources) {
			Map<Term, AddResult> addResults = new HashMap<Term, AddResult>();
			// it may just be sufficient to add everything as main term, because synonymy should always be coupled between <term, label> anyway
			Map<Term, AddResult> addResult = destination.addMainTerms(mergeLabel.getMainTerms());
			//Alerter.alertNotAddedTerms(mergeLabel.getMainTerms(), addResult);
			addResults.putAll(addResult);
			for(Term term : mergeLabel.getMainTerms()) {
				if(destination.isMainTerm(term)) {
					addResult = destination.addSynonymy(term, mergeLabel.getSynonyms(term), false);
					//Alerter.alertNotAddedTerms(mergeLabel.getSynonyms(term), addResult);
					addResults.putAll(addResult);
				} else if(destination.isSynonym(term)) {
					Term termsMainTerm = destination.getMainTermOfSynonym(term);
					addResult = destination.addSynonymy(termsMainTerm, mergeLabel.getSynonyms(term), false);
					//Alerter.alertNotAddedTerms(mergeLabel.getSynonyms(term), addResult);
					addResults.putAll(addResult);
				} else {
					//make sure it ends up in uncategorized// not necessary in model controler
				}
			}
			
			Alerter.alertNotAddedTerms(addResults, destination);
			// in case any of mergeLabel.gerMainTerms() is already a synonym in label, 
			// mainTermParents will contain a reference to the synonym parent and will not have been added as main term in label, 
			// otherwise null is contained and it will have been added as main term in label.
			/*List<Term> mainTermParents = label.addMainTerms(mergeLabel.getMainTerms());
			for(int i=0; i<mergeLabel.getMainTerms().size(); i++) {
				// check whether main term was added or synonym conflict
				Term mainTerm = mergeLabel.getMainTerms().get(i);
				Term parent = mainTermParents.get(i);
				// synonym conflict: add as synonym
				if(parent != null) {
					label.addSynonymy(parent, mergeLabel.getSynonyms(mainTerm));
				} else {
					//check if mergeLabel.getSynonyms(mainTerm)) contain terms that are already in label; if there are, dno't add them as syns
					label.addSynonymy(mainTerm, mergeLabel.getSynonyms(mainTerm));
				}
			}*/
		}
	}

	protected void removeLabel(Label label) {
		collection.removeLabel(label);
	}

	protected void modifyLabel(Label label, String newName, String newDescription) {
		label.setName(newName);
		label.setDescription(newDescription);
	}

	protected void addLabel(Label label) {
		collection.addLabel(label);
	}

	protected void moveTerms(List<Term> terms, Label sourceLabel, Label targetLabel) {
		Map<Term, AddResult> addResult = targetLabel.addMainTerms(terms);
		Alerter.alertNotAddedTerms(addResult, targetLabel);
		sourceLabel.uncategorizeTerm(terms);
	}

	protected void categorizeTerms(List<Term> terms, List<Label> targetLabels) {	
		Map<Term, AddResult> addResults = new HashMap<Term, AddResult>();
		for(Label target : targetLabels) {
			Map<Term, AddResult> addResult = target.addMainTerms(terms);
			Alerter.alertNotAddedTerms(addResult, target);
			addResults.putAll(addResult);
		}
	}

	protected void uncategorizeTerms(List<Term> terms, List<Label> labels) {
		for(Label label : labels)
			for(Term term : terms) 
				label.uncategorizeTerm(term);
		
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

}
