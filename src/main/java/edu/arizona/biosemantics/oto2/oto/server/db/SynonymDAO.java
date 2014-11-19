package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class SynonymDAO {

	private LabelingDAO labelingDAO;
	private TermDAO termDAO;
	
	protected SynonymDAO() { }
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}
	
	public void setLabelingDAO(LabelingDAO labelingDAO) {
		this.labelingDAO = labelingDAO;
	}

	public Map<Term, List<Term>> get(Label label)  {
		Map<Term, List<Term>> synonyms = new HashMap<Term, List<Term>>();
		try(Query query = new Query("SELECT * FROM oto_synonym WHERE label = ?")) {
			query.setParameter(1, label.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				int mainTermId = result.getInt(1);
				int synonymTermId = result.getInt(3);
				Term mainTerm = termDAO.get(mainTermId);
				Term synonymTerm = termDAO.get(synonymTermId);
				if(mainTerm != null && synonymTerm != null) {
					if(!synonyms.containsKey(mainTerm))
						synonyms.put(mainTerm, new LinkedList<Term>());
					synonyms.get(mainTerm).add(synonymTerm);
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
		return synonyms;
	}
	
	public void ensure(Label label, List<Term> mainTerms)  {
		String notInMainTerms = "";
		for(Term mainTerm : mainTerms) {
			notInMainTerms += mainTerm.getId() + ",";
		}
		notInMainTerms = notInMainTerms.isEmpty() ? "" : notInMainTerms.substring(0, notInMainTerms.length() - 1);
		String deleteOldSynonymsQuery = notInMainTerms.isEmpty() ? "DELETE FROM `oto_synonym` WHERE label = ?" : 
			 "DELETE FROM `oto_synonym` WHERE label = ? AND mainterm NOT IN (" + notInMainTerms + ")";
		try(Query deleteOldSynonyms = new Query(deleteOldSynonymsQuery)) {
			deleteOldSynonyms.setParameter(1, label.getId());
			deleteOldSynonyms.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
		
		for(Term mainTerm : mainTerms) {
			List<Term> synonymTerms = label.getSynonyms(mainTerm);
			
			String notInSynonyms = "";
			for(Term synonymTerm : synonymTerms) {
				notInSynonyms += synonymTerm.getId() + ",";
			}
			
			notInSynonyms = notInSynonyms.isEmpty() ? "" : notInSynonyms.substring(0, notInSynonyms.length() - 1);
			deleteOldSynonymsQuery = notInSynonyms.isEmpty() ? "DELETE FROM `oto_synonym` WHERE label = ? AND mainterm = ?" : 
				 "DELETE FROM `oto_synonym` WHERE label = ? AND mainterm = ? AND synonymterm NOT IN (" + notInSynonyms + ")";
			try(Query deleteOldSynonyms = new Query(deleteOldSynonymsQuery)) {
				deleteOldSynonyms.setParameter(1, label.getId());
				deleteOldSynonyms.setParameter(2, mainTerm.getId());
				deleteOldSynonyms.execute();
			} catch(QueryException e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}	
			
			for(Term synonymTerm : synonymTerms) {
				try(Query insert = new Query("INSERT IGNORE INTO `oto_synonym` " +
						"(`mainterm`, `label`, `synonymterm`) VALUES (?, ?, ?)")) {		
					insert.setParameter(1, mainTerm.getId());
					insert.setParameter(2, label.getId());
					insert.setParameter(3, synonymTerm.getId());
					insert.execute();
				} catch(QueryException e) {
					log(LogLevel.ERROR, "Query Exception", e);
				}	
			}
		}
	}
	

	public void insert(Label label, Term mainTerm, Term synonymTerm) {
		try(Query insert = new Query("INSERT IGNORE INTO `oto_synonym` " +
				"(`mainterm`, `label`, `synonymterm`) VALUES (?, ?, ?)")) {		
			insert.setParameter(1, mainTerm.getId());
			insert.setParameter(2, label.getId());
			insert.setParameter(3, synonymTerm.getId());
			insert.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
	}

	public void remove(Collection collection)  {
		try(Query query = new Query("SELECT s.mainterm FROM oto_synonym s, oto_term t, oto_bucket b WHERE b.collection = ? AND t.bucket = b.id AND s.mainterm = t.id ")) {
			query.setParameter(1, collection.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				int toDeleteSynonym = resultSet.getInt(1);
				try(Query deleteQuery = new Query("DELETE FROM oto_synonym WHERE mainterm = ?")) {
					deleteQuery.setParameter(1, toDeleteSynonym);
					deleteQuery.execute();
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}		
	}
		
}
