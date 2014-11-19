package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.shared.model.MainTermSynonyms;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;

public class SelectedTermsExtractor {
	
	public SelectedTerms getSelectedTerms(Tree<TermTreeNode, ?> tree) {
		//List<Term> selection = getSelectedTerms(tree);
		Map<Term, LinkedHashSet<Term>> mainTermSelectedSynonymsMap = new HashMap<Term, LinkedHashSet<Term>>();
		Set<Term> selectedMainTerms = new HashSet<Term>();
		
		for(TermTreeNode selectedTerm : tree.getSelectionModel().getSelectedItems()) {
			TermTreeNode parentNode = tree.getStore().getParent(selectedTerm);
			if(parentNode == null) {
				selectedMainTerms.add(selectedTerm.getTerm());
				if(!mainTermSelectedSynonymsMap.containsKey(selectedTerm.getTerm()))
					mainTermSelectedSynonymsMap.put(selectedTerm.getTerm(), new LinkedHashSet<Term>());
				List<TermTreeNode> synonymNodes = tree.getStore().getChildren(selectedTerm);
				for(TermTreeNode synonymNode : synonymNodes)
					mainTermSelectedSynonymsMap.get(selectedTerm.getTerm()).add(synonymNode.getTerm());
			} else {
				Term parentTerm = parentNode.getTerm();
				if(!mainTermSelectedSynonymsMap.containsKey(parentTerm))
					mainTermSelectedSynonymsMap.put(parentTerm, new LinkedHashSet<Term>());
				mainTermSelectedSynonymsMap.get(parentTerm).add(selectedTerm.getTerm());
			}
		}
		
		List<MainTermSynonyms> mainTermSynonyms = new LinkedList<MainTermSynonyms>();
		List<Term> additionalTerms = new LinkedList<Term>();
		for(Term mainTerm : mainTermSelectedSynonymsMap.keySet()) {
			if(selectedMainTerms.contains(mainTerm)) {
				mainTermSynonyms.add(new MainTermSynonyms(mainTerm, new LinkedList<Term>(mainTermSelectedSynonymsMap.get(mainTerm))));
			} else {
				additionalTerms.addAll(mainTermSelectedSynonymsMap.get(mainTerm));
			}
		}
		
		return new SelectedTerms(mainTermSynonyms, additionalTerms);
	}
}
