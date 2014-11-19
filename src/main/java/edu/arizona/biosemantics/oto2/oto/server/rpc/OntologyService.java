package edu.arizona.biosemantics.oto2.oto.server.rpc;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyService;

public class OntologyService extends RemoteServiceServlet implements IOntologyService {

	private DAOManager daoManager = new DAOManager();
	
	@Override
	public List<OntologyEntry> getOntologyEntries(Term term) {
		return daoManager.getOntologyDAO().get(term);
	}
	
	@Override
	public List<OntologyEntry> getOntologyEntries(Term term, List<Ontology> ontologies) {
		return daoManager.getOntologyDAO().get(term, ontologies);
	}
	
	@Override
	public Set<Ontology> getOntologies() {
		return daoManager.getOntologyDAO().getOntologies();
	}

}
