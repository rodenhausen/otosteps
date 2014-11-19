package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface IOntologyServiceAsync {

	public void getOntologyEntries(Term term, AsyncCallback<List<OntologyEntry>> callback);
	
	public void getOntologyEntries(Term term, List<Ontology> ontologies, AsyncCallback<List<OntologyEntry>> callback);
	
	public void getOntologies(AsyncCallback<Set<Ontology>> callback);
		
}
