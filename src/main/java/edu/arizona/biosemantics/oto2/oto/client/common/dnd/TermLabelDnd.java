package edu.arizona.biosemantics.oto2.oto.client.common.dnd;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermLabelDnd extends TermDnd {

	protected List<Label> labels = new LinkedList<Label>();

	public TermLabelDnd(Widget source) {
		super(source);
	}
	
	public TermLabelDnd(Widget source, List<Term> terms, Label label) {
		super(source, terms);
		labels = new LinkedList<Label>();
		this.labels.add(label);
	}
	
	public TermLabelDnd(Widget source, List<Term> terms, List<Label> labels) {
		super(source, terms);
		this.labels = labels;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}
	
}
