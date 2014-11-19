package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

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
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("collection")
public interface ICollectionService extends RemoteService {
	
	public Collection get(Collection collection);
	
	public Collection get(int id, String secret);
	
	public void update(Collection collection); 
	
	public Collection insert(Collection collection);
	
	public Term addTerm(Term term, int bucketId);
	
	public Label addLabel(Label label, int collectionId);
	
	public Comment addComment(Comment comment, int termId);
	
	public List<Location> getLocations(Term term);
	
	public Collection reset(Collection collection);
	
	public Collection initializeFromHistory(Collection collection);

	
}
