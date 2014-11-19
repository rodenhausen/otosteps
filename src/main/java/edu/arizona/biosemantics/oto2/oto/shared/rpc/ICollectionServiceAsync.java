package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.bioportal.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Comment;
import edu.arizona.biosemantics.oto2.oto.shared.model.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Location;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ICollectionServiceAsync {
	
	public void get(Collection collection, AsyncCallback<Collection> callback);
	
	public void get(int id, String secret, AsyncCallback<Collection> callback);

	public void update(Collection collection, AsyncCallback<Void> callback);
	
	public void insert(Collection collection, AsyncCallback<Collection> callback);
	
	public void addTerm(Term term, int bucketId, AsyncCallback<Term> callback);
	
	public void addLabel(Label label, int collectionId, AsyncCallback<Label> callback);
	
	public void addComment(Comment comment, int termId, AsyncCallback<Comment> callback);
	
	public void getLocations(Term term, AsyncCallback<List<Location>> callback);

	public void reset(Collection collection, AsyncCallback<Collection> callback);
	
	public void initializeFromHistory(Collection collection, AsyncCallback<Collection> callback);
		
}
