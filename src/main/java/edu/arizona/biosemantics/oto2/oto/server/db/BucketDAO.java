package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class BucketDAO {
	
	private TermDAO termDAO;
	private LabelingDAO labelingDAO;
	
	protected BucketDAO() {} 
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public void setLabelingDAO(LabelingDAO labelingDAO) {
		this.labelingDAO = labelingDAO;
	}

	public Bucket get(int id) {
		Bucket bucket = null;
		try(Query query = new Query("SELECT * FROM oto_bucket WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				bucket = createBucket(result);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return bucket;
	}
	
	private Bucket createBucket(ResultSet result) throws SQLException  {
		int id = result.getInt(1);
		int collectionId = result.getInt(2);
		String name = result.getString(3);
		String description = result.getString(4);
		Bucket bucket = new Bucket(id, collectionId, name, description);
		List<Term> terms = termDAO.getTerms(bucket);
		bucket.setTerms(terms);
		return bucket;
	}

	public Bucket insert(Bucket bucket, int collectionId)  {
		if(!bucket.hasId()) {
			try (Query insert = new Query("INSERT INTO `oto_bucket` " +
					"(`collection`, `name`, `description`) VALUES (?, ?, ?)")) {
				insert.setParameter(1, collectionId);
				insert.setParameter(2, bucket.getName().trim());
				insert.setParameter(3, bucket.getDescription().trim());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				bucket.setId(id);
				
				for(Term term : bucket.getTerms())
					termDAO.insert(term, bucket.getId());
			} catch(Exception e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}
		}
		return bucket;
	}

	public void update(Bucket bucket)  {
		try(Query query = new Query("UPDATE oto_bucket SET name = ?, description = ? WHERE id = ?")) {
			query.setParameter(1, bucket.getName());
			query.setParameter(2, bucket.getDescription());
			query.setParameter(3, bucket.getId());
			
			Bucket oldBucket = this.get(bucket.getId());
			for(Term term : oldBucket.getTerms()) {
				termDAO.remove(term);
			}
			for(Term term : bucket.getTerms()) {
				termDAO.insert(term, bucket.getId());
			}
			query.execute();
		} catch (QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}

	public void remove(Bucket bucket)  {
		try(Query query = new Query("DELETE FROM oto_bucket WHERE id = ?")) {
			query.setParameter(1, bucket.getId());
			query.execute();
		} catch (QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		for(Term term :  bucket.getTerms())
			termDAO.remove(term);
	}
	
	public List<Bucket> getBuckets(Collection collection)  {
		List<Bucket> buckets = new LinkedList<Bucket>();
		try(Query query = new Query("SELECT * FROM oto_bucket WHERE collection = ?")) {
			query.setParameter(1, collection.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				buckets.add(createBucket(result));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return buckets;		
	}

	public void ensure(Collection collection)  {
		for(Bucket bucket : collection.getBuckets()) {
			for(Term term : bucket.getTerms()) {
				termDAO.update(term, bucket.getId());
			}
		}
	}

	public Bucket get(Term term) {
		Bucket bucket = null;
		try(Query query = new Query("SELECT b.* FROM oto_bucket b, oto_term t " +
				"WHERE t.id = ? AND t.bucket = b.id")) {
			query.setParameter(1, term.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				bucket = createBucket(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return bucket;
	}
	
}
