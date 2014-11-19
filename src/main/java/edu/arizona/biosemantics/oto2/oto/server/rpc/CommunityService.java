package edu.arizona.biosemantics.oto2.oto.server.rpc;

import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Categorization;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.CommunityCollection;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Synonymization;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICommunityService;

public class CommunityService extends RemoteServiceServlet implements ICommunityService {

	private DAOManager daoManager = new DAOManager();
	
	@Override
	public CommunityCollection get(String type) {
		Set<Categorization> categorizations = daoManager.getCommunityDAO().getCategorizations(type);
		Set<Synonymization> synonymizations = daoManager.getCommunityDAO().getSynoymizations(type);
		return new CommunityCollection(categorizations, synonymizations);
	}

}
