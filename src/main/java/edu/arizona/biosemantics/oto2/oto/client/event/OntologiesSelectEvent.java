package edu.arizona.biosemantics.oto2.oto.client.event;

import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.client.event.OntologiesSelectEvent.OntologiesSelectHandler;

public class OntologiesSelectEvent extends GwtEvent<OntologiesSelectHandler> {

	public interface OntologiesSelectHandler extends EventHandler {
		void onSelect(OntologiesSelectEvent event);
	}
	
    public static Type<OntologiesSelectHandler> TYPE = new Type<OntologiesSelectHandler>();
	private Set<Ontology> ontologies;

    public OntologiesSelectEvent(Set<Ontology> ontologies) {
    	this.ontologies = ontologies;
    }
    
	@Override
	public Type<OntologiesSelectHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OntologiesSelectHandler handler) {
		handler.onSelect(this);
	}

	public Set<Ontology> getOntologies() {
		return ontologies;
	}
	
}
