package edu.arizona.biosemantics.oto2.oto.server.db;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.HighlightLabel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TrashLabel;

public class FilllSample {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException  {
		ConnectionPool connectionPool = new ConnectionPool();
		Query.connectionPool = connectionPool;
		
		try(Query query = new Query("TRUNCATE TABLE  `oto_collection`")) {
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
		try(Query query = new Query("TRUNCATE TABLE  `oto_bucket`")) {
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
		try(Query query = new Query("TRUNCATE TABLE  `oto_label`")) {
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
		try(Query query = new Query("TRUNCATE TABLE  `oto_labeling`")) {
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
		try(Query query = new Query("TRUNCATE TABLE  `oto_synonym`")) {
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
		try(Query query = new Query("TRUNCATE TABLE  `oto_context`")) {
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
		try(Query query = new Query("TRUNCATE TABLE  `oto_term`")) {
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
		
		List<Bucket> buckets = new LinkedList<Bucket>();
		Bucket b = new Bucket();
		Bucket b2 = new Bucket();
		Bucket b3 = new Bucket();
		Term t1 = new Term();
		t1.setTerm("leaf");
		Term t2 = new Term();
		t2.setTerm("stem");
		Term t3 = new Term();
		t3.setTerm("apex");
		Term t4 = new Term();
		t4.setTerm("root");
		Term t5 = new Term();
		t5.setTerm("sepal");
		b.addTerm(t1);
		b.addTerm(t2);
		b.addTerm(t3);
		b.addTerm(t4);
		b.addTerm(t5);
		//for(int i=0; i<100; i++) {
		//	Term term = new Term("term " + i);
		//	b.addTerm(term);
		//}
		buckets.add(b);
		b.setName("structures");
		Term c1 = new Term("length");
		Term c2 = new Term("color");
		b2.addTerm(c1);
		b2.addTerm(c2);
		b2.setName("characters");
		b3.setName("others");
		Term o1 = new Term("asdfg");
		b3.addTerm(o1);
		buckets.add(b2);
		buckets.add(b3);
		
		Collection collection = new Collection();
		collection.setName("My test");
		collection.setBuckets(buckets);
		collection.setType("Plant");
		
		List<Label> labels = new LinkedList<Label>();
		Label l0 = new HighlightLabel();
		l0.setName("structure");
		
		Label l1 = new Label();
		l1.setName("arrangement");
		
		Label l2 = new Label();
		l2.setName("architecture");
		
		Label l3 = new Label();
		l3.setName("coloration");	
		
		labels.add(l0);
		labels.add(l1);
		labels.add(l2);
		labels.add(l3);
		
		/*for(int i=0; i<50; i++) {
			Label l = new Label();
			l.setName("label " + i);	
			labels.add(l);
		}*/
		
		//Label trashLabel = new TrashLabel("Useless", "This category can be uesd to label terms as uselss");
		//labels.add(trashLabel);
		collection.setLabels(labels);
		
		collection.setSecret("my secret");
		DAOManager daoManager = new DAOManager();
		collection = daoManager.getCollectionDAO().insert(collection);
			
		Context context = new Context(collection.getId(), "rubus", "text with label 1 and label 2 and apex and leaf");
		Context context2 = new Context(collection.getId(), "rubus", "text2");
		Context context3 = new Context(collection.getId(), "rubus argetest", "asdf");
		
		daoManager.getContextDAO().insert(context);
		daoManager.getContextDAO().insert(context2);
		daoManager.getContextDAO().insert(context3);
		
	}

}