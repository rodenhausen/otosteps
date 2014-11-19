package edu.arizona.biosemantics.oto2.oto.server.rpc;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IContextService;

public class ContextService extends RemoteServiceServlet implements IContextService {

	private DAOManager daoManager = new DAOManager();
	
	@Override
	public List<TypedContext> getContexts(Collection collection, Term term) {
		return daoManager.getContextDAO().get(collection, term);
	}
	
	@Override
	public List<Context> insert(int collectionId, String secret, List<Context> contexts) {
		if(daoManager.getCollectionDAO().isValidSecret(collectionId, secret)) {
			List<Context> result = new ArrayList<Context>(contexts.size());
			for(Context context : contexts) {
				context = daoManager.getContextDAO().insert(context);
				result.add(context);
			}
			return result;
		}
		return null;
	}

}
