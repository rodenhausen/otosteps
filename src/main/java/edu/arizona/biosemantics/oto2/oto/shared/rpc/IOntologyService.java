package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;


/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("ontology")
public interface IOntologyService extends RemoteService {
	
	public List<OntologyEntry> getOntologyEntries(Term term);
	
	public List<OntologyEntry> getOntologyEntries(Term term, List<Ontology> ontologies);
	
	public Set<Ontology> getOntologies();
	
}
