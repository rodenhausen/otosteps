package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.Validator;

import edu.arizona.biosemantics.oto2.oto.client.event.OntologiesSelectEvent;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyServiceAsync;

public class SelectOntologiesDialog extends Dialog {

	private static Set<String> preselectedOntologies = new HashSet<String>(Arrays.asList(
			new String[]{"PO", "HAO", "PORO", "PATO"}));
	private final OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private final IOntologyServiceAsync ontologyService = GWT.create(IOntologyService.class);
	private ListStore<Ontology> unselectedListStore = new ListStore<Ontology>(ontologyProperties.key());
	private ListStore<Ontology> selectedListStore = new ListStore<Ontology>(ontologyProperties.key());
	private EventBus eventBus;
	private static Set<Ontology> ontologies = new HashSet<Ontology>();	
	
	public SelectOntologiesDialog(final EventBus eventBus) {
		this.eventBus = eventBus;
		setHeadingText("Select Ontologies to Search");
		setPredefinedButtons(PredefinedButton.OK);
		setBodyStyleName("pad-text");
		getBody().addClassName("pad-text");
		setHideOnButtonClick(false);
		setWidth(500);
		setHeight(500);
		
		selectedListStore.addSortInfo(new StoreSortInfo<Ontology>(ontologyProperties.acronym(), SortDir.ASC));
		unselectedListStore.addSortInfo(new StoreSortInfo<Ontology>(ontologyProperties.acronym(), SortDir.ASC));
		unselectedListStore.addAll(ontologies);
		for(Ontology ontology : ontologies) {
			if(preselectedOntologies.contains(ontology.getAcronym())) {
				selectedListStore.add(ontology);
				unselectedListStore.remove(ontology);
			}
		}
		
		final DualListField<Ontology, String> dialListField = new DualListField<Ontology, String>(
				unselectedListStore, selectedListStore,
				ontologyProperties.acronym(), new TextCell());
		dialListField.setMode(Mode.INSERT);
		dialListField.addValidator(new Validator<List<Ontology>>() {
			@Override
			public List<EditorError> validate(Editor<List<Ontology>> editor,
					List<Ontology> value) {
				if (value.size() <= 10) // || value.containsAll(ontologies) ||)
					return null;
				else {
					List<EditorError> errors = new ArrayList<EditorError>();
					// errors.add(new DefaultEditorError(editor,
					// "You have to select either all, or <= 10 ontologies.",
					// ""));
					errors.add(new DefaultEditorError(editor,
							"You can't select more than 10 ontologies.", ""));
					return errors;
				}
			}
		});
		dialListField.setEnableDnd(true);
		add(dialListField);

		this.getButton(PredefinedButton.OK).addSelectHandler(
				new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						if (dialListField.validate()) {
							List<Ontology> selectedOntologies = selectedListStore
									.getAll();
							Set<Ontology> ontologies = new LinkedHashSet<Ontology>(
									selectedOntologies.size());
							ontologies.addAll(selectedOntologies);
							eventBus.fireEvent(new OntologiesSelectEvent(
									ontologies));
							hide();
						}
					}
				});
	}
	
	
	private void refreshOntologies() {
		//already store ontologies, otherwise delay when requested on button press
		ontologyService.getOntologies(new AsyncCallback<Set<Ontology>>() {
			@Override
			public void onSuccess(Set<Ontology> result) {
				ontologies = result;
				unselectedListStore.clear();
				unselectedListStore.addAll(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.getOntologiesFailed(caught);
			}
		});
	}
	
	public static void setOntologies(Set<Ontology> ontologies) {
		SelectOntologiesDialog.ontologies = ontologies;
	}

}