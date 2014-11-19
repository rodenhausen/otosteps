package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.bioportal.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Location;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface IContextServiceAsync {	

	public void getContexts(Collection collection, Term term, AsyncCallback<List<TypedContext>> callback);
	
	public void insert(int collectionId, String secret, List<Context> contexts, AsyncCallback<List<Context>> callback);
		
}
