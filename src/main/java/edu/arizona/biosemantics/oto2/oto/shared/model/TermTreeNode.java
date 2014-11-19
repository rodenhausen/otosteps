package edu.arizona.biosemantics.oto2.oto.shared.model;
public class TermTreeNode extends TextTreeNode {

	private Term term;

	public TermTreeNode(Term term) {
		this.term = term;
	}
	
	@Override
	public String getText() {
		return term.getTerm();
	}

	public Term getTerm() {
		return term;
	}

	@Override
	public String getId() {
		return "term-" + term.getId();
	}
	
}