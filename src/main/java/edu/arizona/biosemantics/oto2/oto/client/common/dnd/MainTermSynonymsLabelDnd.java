package edu.arizona.biosemantics.oto2.oto.client.common.dnd;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.MainTermSynonyms;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class MainTermSynonymsLabelDnd extends TermLabelDnd {
	
	protected SelectedTerms selectedTerms;
		
	public MainTermSynonymsLabelDnd(Widget source, SelectedTerms selectedTerms, Label label) {
		super(source);
		this.setTerms(selectedTerms.getTerms());
		this.selectedTerms = selectedTerms;
		List<Label> labels = new LinkedList<Label>();
		labels.add(label);
		this.setLabels(labels);
	}

	public SelectedTerms getSelectedTerms() {
		return selectedTerms;
	}
	
}
