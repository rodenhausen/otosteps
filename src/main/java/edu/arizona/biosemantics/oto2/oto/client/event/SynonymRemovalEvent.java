package edu.arizona.biosemantics.oto2.oto.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent.SynonymRemovalHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class SynonymRemovalEvent extends GwtEvent<SynonymRemovalHandler> {

	public interface SynonymRemovalHandler extends EventHandler {
		void onSynonymRemoval(SynonymRemovalEvent event);
	}
	
    public static Type<SynonymRemovalHandler> TYPE = new Type<SynonymRemovalHandler>();
	private Label label;
	private Term mainTerm;
	private List<Term> synonyms = new LinkedList<Term>();
    
    
    public SynonymRemovalEvent(Label label, Term mainTerm, List<Term> synonyms) {
        this.label = label;
        this.mainTerm = mainTerm;
        this.synonyms = synonyms;
    }
	
	public SynonymRemovalEvent(Label label, Term mainTerm, Term synonym) {
		this.label = label;
        this.mainTerm = mainTerm;
        this.synonyms.add(synonym);
	}

	@Override
	public Type<SynonymRemovalHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SynonymRemovalHandler handler) {
		handler.onSynonymRemoval(this);
	}

	public Label getLabel() {
		return label;
	}

	public Term getMainTerm() {
		return mainTerm;
	}

	public List<Term> getSynonyms() {
		return synonyms;
	}

}
