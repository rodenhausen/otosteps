package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class CollectionDAO {
	
	private BucketDAO bucketDAO;
	private LabelDAO labelDAO;
	private LabelingDAO labelingDAO;
	private TermDAO termDAO;
	private SynonymDAO synonymDAO;
	
	protected CollectionDAO() {} 
	
	public void setBucketDAO(BucketDAO bucketDAO) {
		this.bucketDAO = bucketDAO;
	}

	public void setLabelDAO(LabelDAO labelDAO) {
		this.labelDAO = labelDAO;
	}
	
	public void setLabelingDAO(LabelingDAO labelingDAO) {
		this.labelingDAO = labelingDAO;
	}
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public void setSynonymDAO(SynonymDAO synonymDAO) {
		this.synonymDAO = synonymDAO;
	}

	public boolean isValidSecret(int id, String secret)  {
		try(Query query = new Query("SELECT secret FROM oto_collection WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				String validSecret = result.getString(1);
				return validSecret.equals(secret);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return false;
	}
	
	public Collection get(int id)  {
		Collection collection = null;
		try(Query query = new Query("SELECT * FROM oto_collection WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				collection = createCollection(result);
			}
			
			List<Bucket> buckets = bucketDAO.getBuckets(collection);
			collection.setBuckets(buckets);
			List<Label> labels = labelDAO.getLabels(collection);
			// ensure to return same term objects in bucket and labels, so operations are performed
			// on the same object, e.g. rename
			List<Term> termsToSend = new LinkedList<Term>();
			for(Bucket bucket : buckets)
				termsToSend.addAll(bucket.getTerms());
			for(Label label : labels) {
				List<Term> oldMainTerms = label.getMainTerms();
				Map<Term, List<Term>> oldSynonymTermsMap = label.getMainTermSynonymsMap();
				Map<Term, List<Term>> newSynonymTermsMap = label.getMainTermSynonymsMap();
				
				List<Term> newMainLabelTerms = new LinkedList<Term>();
				for(Term mainLabelTerm : label.getMainTerms()) {
					Term mainTermToSend = termsToSend.get(termsToSend.indexOf(mainLabelTerm));
					newMainLabelTerms.add(mainTermToSend);
					
					List<Term> newSynonymTerms = new LinkedList<Term>();
					for(Term synonymTerm : label.getSynonyms(mainLabelTerm)) {
						Term synonymTermToSend = termsToSend.get(termsToSend.indexOf(synonymTerm));
						newSynonymTerms.add(synonymTermToSend);
					}
					
					newSynonymTermsMap.put(mainTermToSend, newSynonymTerms);
				}
				label.setMainTerms(newMainLabelTerms);		
				label.setMainTermSynonymsMap(newSynonymTermsMap);
			}
			//
			collection.setLabels(labels);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		try(Query query = new Query("UPDATE oto_collection SET lastretrieved = ? WHERE id = ?")) {
			query.setParameter(2, id);
			Date date = new Date();
			query.setParameter(1, new Timestamp(date.getTime()));
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		return collection;
	}
	
	private Collection createCollection(ResultSet result) throws SQLException {
		int id = result.getInt(1);
		String name = result.getString(2);
		String type = result.getString(3);
		String secret = result.getString(4);
		return new Collection(id, name, type, secret);
	}

	public Collection insert(Collection collection)  {
		if(!collection.hasId()) {
			try(Query insert = new Query("INSERT INTO `oto_collection` (`name`, `type`, `secret`) VALUES(?, ?, ?)")) {
				insert.setParameter(1, collection.getName().trim());
				insert.setParameter(2, collection.getType());
				insert.setParameter(3, collection.getSecret());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				collection.setId(id);
				
				for(Bucket bucket : collection.getBuckets()) {
					bucketDAO.insert(bucket, collection.getId());
					bucket.setCollection(id);
				}
						
				for(Label label : collection.getLabels()) {
					labelDAO.insert(label, collection.getId());
					label.setCollection(id);
				}
			} catch(Exception e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}
		}
		return collection;
	}
	
	public void update(Collection collection)  {
		System.out.println("update collection");
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		try {
			System.out.println(writer.writeValueAsString(collection));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try(Query query = new Query("UPDATE oto_collection SET name = ?, type = ?, secret = ? WHERE id = ?")) {
			query.setParameter(1, collection.getName());
			query.setParameter(2, collection.getType());
			query.setParameter(3, collection.getSecret());
			query.setParameter(4, collection.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		bucketDAO.ensure(collection);
		labelDAO.ensure(collection);
	}
	
	public void remove(Collection collection)  {
		try(Query query = new Query("DELETE FROM oto_collection WHERE id = ?")) {
			query.setParameter(1, collection.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		for(Bucket bucket : collection.getBuckets())
			bucketDAO.remove(bucket);
		
		for(Label label : collection.getLabels())
			labelDAO.remove(label);
	}

	public Collection reset(Collection collection)  {
		labelingDAO.remove(collection);
		synonymDAO.remove(collection);
		termDAO.resetTerms(collection);
		return this.get(collection.getId());
	}

	public List<Collection> getCollections(String type) {
		List<Collection> collections = new LinkedList<Collection>();
		try(Query query = new Query("SELECT id FROM oto_collection WHERE type = ?")) {
			query.setParameter(1, type);
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				Collection collection = get(id);
				if(collection != null)
					collections.add(collection);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return collections;
	}	
}
