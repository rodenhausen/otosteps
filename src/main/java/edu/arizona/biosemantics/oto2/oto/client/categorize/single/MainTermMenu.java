package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.LinkedList;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.categorize.TermMenu;
import edu.arizona.biosemantics.oto2.oto.client.common.SelectedTermsExtractor;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.MainTermSynonyms;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;

public class MainTermMenu extends TermMenu {

	private Term mainTerm;

	public MainTermMenu(EventBus eventBus, Collection collection, Label label, Term mainTerm) {
		super(eventBus, collection, label);
		this.mainTerm = mainTerm;
	}

	@Override
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

	@Override
	public SelectedTerms getSelectedTerms() {
		return new SelectedTerms(new MainTermSynonyms(mainTerm, label.getSynonyms(mainTerm)));
	}

	@Override
	public List<Term> getExplicitlySelectedTerms() {
		List<Term> result = new LinkedList<Term>();
		result.add(mainTerm);
		return result;
	}
	
}
