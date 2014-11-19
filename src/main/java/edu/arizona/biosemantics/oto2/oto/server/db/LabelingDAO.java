package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class LabelingDAO {
	
	private TermDAO termDAO;
	private LabelDAO labelDAO;
	
	protected LabelingDAO() { }

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}
	
	public void setLabelDAO(LabelDAO labelDAO) {
		this.labelDAO = labelDAO;
	}
	
	public Set<Label> getLabels(Term term)  {
		Set<Label> labels = new HashSet<Label>();
		try(Query query = new Query("SELECT * FROM oto_labeling WHERE term = ?")) {
			query.setParameter(1, term.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				int labelId = result.getInt(2);
				Label label = labelDAO.get(labelId);
				if(label != null)
					labels.add(label);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		try(Query query = new Query("SELECT * FROM oto_synonym WHERE synonymterm = ?")) {
			query.setParameter(1, term.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				int labelId = result.getInt(2);
				Label label = labelDAO.get(labelId);
				if(label != null)
					labels.add(label);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return labels;
	}
	
	public List<Term> getAllTerms(Label label)  {
		List<Term> terms = new LinkedList<Term>();
		try(Query query = new Query("SELECT * FROM oto_labeling WHERE label = ?")) {
			query.setParameter(1, label.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				int termId = result.getInt(1);
				Term term = termDAO.get(termId);
				if(term != null)
					terms.add(term);
			}
			try(Query synonymQuery = new Query("SELECT * FROM oto_synonym WHERE label = ?")) {
				synonymQuery.setParameter(1, label.getId());
				result = query.execute();
				while(result.next()) {
					int termId = result.getInt(3);
					Term term = termDAO.get(termId);
					if(term != null)
						terms.add(term);
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return terms;
	}
	
	public List<Term> getMainTerms(Label label)  {
		List<Term> terms = new LinkedList<Term>();
		try(Query query = new Query("SELECT * FROM oto_labeling WHERE label = ? AND term NOT IN (SELECT synonymterm FROM oto_synonym WHERE label = ?)")) {
			query.setParameter(1, label.getId());
			query.setParameter(2, label.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				int termId = result.getInt(1);
				Term term = termDAO.get(termId);
				if(term != null)
					terms.add(term);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}		
		return terms;
	}

	public void insert(Term term, Label label)  {
		try(Query query = new Query("INSERT INTO `oto_labeling` " +
				"(`term`, `label`) VALUES (?, ?)")) {
			query.setParameter(1, term.getId());
			query.setParameter(2, label.getId());
			ResultSet result = query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
	}
	
	public void ensure(Term term, Label label)  {
		try(Query query = new Query("SELECT * FROM `oto_labeling` WHERE term = ? AND label = ?")) {
			query.setParameter(1, term.getId());
			query.setParameter(2, label.getId());
			ResultSet result = query.execute();
			if(!result.next()) 
				insert(term, label);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
	}
	
	public void ensure(Label label, List<Term> terms)  {
		try(Query deleteOldLabelings = new Query("DELETE FROM `oto_labeling` WHERE label NOT IN "
				+ "(SELECT id FROM `oto_label`)")) {
			deleteOldLabelings.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
		
		String queryString = "DELETE FROM `oto_labeling` WHERE label = ?"; 
		for(Term term : terms)
			queryString += " AND term != " + term.getId();
		try(Query query = new Query(queryString)) {
			query.setParameter(1, label.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
		
		for(Term term : terms)
			ensure(term, label);
	}
	
	
	public void remove(Term term, Label label)  {
		try(Query query = new Query("DELETE FROM `oto_labeling` WHERE term = ? AND label = ?")) {
			query.setParameter(1, term.getId());
			query.setParameter(2, label.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
	}

	public void remove(Collection collection)  {
		try(Query query = new Query("SELECT l.term FROM oto_labeling l, oto_term t, oto_bucket b WHERE b.collection = ? AND t.bucket = b.id AND l.term = t.id")) {
			query.setParameter(1, collection.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				int toDeleteTerm = resultSet.getInt(1);
				try(Query deleteQuery = new Query("DELETE FROM oto_labeling WHERE term = ?")) {
					deleteQuery.setParameter(1, toDeleteTerm);
					deleteQuery.execute();
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
	}


}