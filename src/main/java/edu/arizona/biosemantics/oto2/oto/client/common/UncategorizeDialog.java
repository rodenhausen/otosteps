package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.LinkedList;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class UncategorizeDialog extends MessageBox {
	
	private EventBus eventBus;
	private List<Label> sourceLabels;
	private Term term;
	private List<Label> labels;

	public UncategorizeDialog(final EventBus eventBus, final Label sourceLabel, final Term term, 
			final List<Label> labels) {
		super("Remove all categorizations of term?", "");
		this.eventBus = eventBus;
		this.sourceLabels = new LinkedList<Label>();
		this.sourceLabels.add(sourceLabel);
		this.term = term;
		this.labels = labels;
		
		createAndShow();
	}
	
	public UncategorizeDialog(final EventBus eventBus, final List<Label> sourceLabels, final Term term, 
			final List<Label> labels) {
		super("Remove all categorizations of term?", "");
		this.eventBus = eventBus;
		this.sourceLabels = sourceLabels;
		this.term = term;
		this.labels = labels;
		
		createAndShow();
	}

	private void createAndShow() {
		setPredefinedButtons(PredefinedButton.YES,
				PredefinedButton.NO, PredefinedButton.CANCEL);
		setIcon(MessageBox.ICONS.question());
		setMessage("You are uncategorizing a term with multiple categories. Would you like to "
				+ "remove <b>" + term.getTerm() + "</b> from from all its categories?");
		getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new TermUncategorizeEvent(term, labels));
			}
		});
		getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new CategorizeCopyRemoveTermEvent(term, sourceLabels));
			}
		});
		getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
		show();
	}
	
}
