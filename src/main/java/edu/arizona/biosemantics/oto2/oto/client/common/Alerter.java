package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.List;
import java.util.Map;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.BeforeSelectHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;

public class Alerter {

	public static class InfoMessageBox extends MessageBox {
		public InfoMessageBox(String title, String message) {
			super(title, message);
			setIcon(ICONS.info());
		}
	}
	
	public static void alertNotAddedTerms(List<Term> possibleMainTerms, Map<Term, AddResult> addResults) {
		for(Term possibleMainTerm : possibleMainTerms) {
			AddResult addResult = addResults.get(possibleMainTerm);
			if(!addResult.result) {
				if(addResult.parent != null) {
					InfoMessageBox alert = new InfoMessageBox("Term exists in label", 
							"The term <b>" + possibleMainTerm.getTerm() + "</b> already exists in this label as synonym of <b>" + 
									addResult.parent.getTerm() + "</b>. A term can only appear once inside a label.");
					alert.show();
				} else {
					InfoMessageBox alert = new InfoMessageBox("Term exists in label", 
							"The term <b>" + possibleMainTerm.getTerm() + "</b> already exists in this label" + 
									". A term can only appear once inside a label.");
					alert.show();
				}
			}
		}
	}
	
	public static void alertNotAddedTerms(Map<Term, AddResult> addResults, Label targetLabel) {
		StringBuilder resultBuilder = new StringBuilder();
		for(Term term : addResults.keySet()) {
			AddResult addResult = addResults.get(term);
			if(!addResult.result) {
				if(addResult.parent != null) {
					resultBuilder.append("- <b>" + term.getTerm() + "</b> already exists as synonym of " +
							"<b>" + addResult.parent.getTerm() + "</b>.</br>");
				} else {
					resultBuilder.append("- <b>" + term.getTerm() + "</b> already exists.</br>");
				}
			}
		}
		
		String text = resultBuilder.toString();
		if(!text.isEmpty()) {
			InfoMessageBox alert = new InfoMessageBox(targetLabel.getName() + ": Term(s) exist(s) in category", 
					"A term can only appear <b>once</b> inside category " + targetLabel.getName() + ": </br>" + text);
			alert.show();
		}
	}

	
	public static void alertNoOntoloygySelected() {
		InfoMessageBox alert = new InfoMessageBox("Ontology Selection", "Before you can use this feature" +
				" you have to select a set " +
				"of ontologies to search.");
		alert.show();
	}

	public static void alertFailedToLoadCollection() {
		InfoMessageBox alert = new InfoMessageBox("Load Collection Failed", "Failed to load the collection. Please come back later.");
		alert.show();
	}

	public static void alertTermWithNameExists(String newName) {
		InfoMessageBox alert = new InfoMessageBox("Term with name exists", "Failed to rename term. " +
				"Another term with the same spelling <b>" + newName + "</b> exists already.");
		alert.show();
	}
	
	public static void mergeWarning(final EventBus eventBus, final LabelsMergeEvent labelsMergeEvent) {
		String labelsString = "";
		for(Label label : labelsMergeEvent.getSources()) {
			labelsString += "<b>" + label.getName() + "</b>, ";
		}
		final ConfirmMessageBox alert = new ConfirmMessageBox("Merge categories", "The merge will remove the categories " +
				labelsString.substring(0, labelsString.length() - 2) + " and move their terms into <b>" + labelsMergeEvent.getDestination().getName() + 
				"</b>. Do you want to continue?");
		alert.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(labelsMergeEvent);
			}
		});
		alert.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				alert.hide();
			}
		});
		alert.show();
	}

	public static void dialogRename(final EventBus eventBus, final Term term, final Collection collection) {
		final PromptMessageBox box = new PromptMessageBox(
				"Correct Spelling", "Please input new spelling");
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
		box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				String newName = box.getValue();
				eventBus.fireEvent(new TermRenameEvent(term, term.getTerm(), newName, collection));
			}
		});
		box.show();
	}

	public static void addCommentFailed(Throwable caught) {
		 AlertMessageBox box = new AlertMessageBox("Add Comment", "Adding of comment failed.");
		 box.show();
		 caught.printStackTrace();
	}

	public static void addLabelFailed(Throwable caught) {
		AlertMessageBox box = new AlertMessageBox("Add Label", "Adding of label failed.");
		box.show();
	}

	public static void getContextsFailed(Throwable caught) {
		AlertMessageBox box = new AlertMessageBox("Get Context", "Getting of contexts failed.");
		box.show();
		caught.printStackTrace();
	}

	public static void getOntologiesFailed(Throwable caught) {
		AlertMessageBox box = new AlertMessageBox("Get Ontologies", "Getting of ontologies failed.");
		box.show();
		caught.printStackTrace();
	}

	public static void getLocationsFailed(Throwable caught) {
		AlertMessageBox box = new AlertMessageBox("Get Locations", "Getting of locations failed.");
		box.show();
		caught.printStackTrace();
	}

	public static void getOntologyEntriesFailed(Throwable caught) {
		AlertMessageBox box = new AlertMessageBox("Get Ontology Entries", "Getting of ontology entries failed.");
		box.show();
		caught.printStackTrace();
	}

	public static void saveCollectionFailed(Throwable caught) {
		AlertMessageBox box = new AlertMessageBox("Save Collection", "Save collection failed.");
		box.show();
		caught.printStackTrace();
	}

	public static void resetFailed(Throwable caught) {
		AlertMessageBox box = new AlertMessageBox("Reset", "Reset failed.");
		box.show();
		caught.printStackTrace();
	}

	public static void alertCouldNotBeLoaded(Throwable caught, int collectionId, String secret) {
		AlertMessageBox box = new AlertMessageBox("Load failed", "Could not load collection with id " + 
				collectionId + " and secret " + secret);
		box.show();
		caught.printStackTrace();
	}

}
