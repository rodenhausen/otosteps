package edu.arizona.biosemantics.oto2.oto.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Comment;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class CommentDAO {
	
	protected CommentDAO() { }
		
	public Comment get(int id)  {
		Comment term = null;
		try(Query query = new Query("SELECT * FROM oto_term_comment WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				term = createComment(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
		return term;
	}
	
	private Comment createComment(ResultSet result) throws SQLException  {
		int id = result.getInt(1);
		//int termId = result.getInt(2);
		String user = result.getString(3);
		String comment = result.getString(4);
		return new Comment(id, user, comment);
	}

	public Comment insert(Comment comment, int termId)  {
		removeUserComments(comment.getUser(), termId);
		if(!comment.hasId()) {
			try(Query insert = new Query("INSERT INTO `oto_term_comment` " +
					"(`term`, `user`, `comment`) VALUES (?, ?, ?)")) {
				insert.setParameter(1, termId);
				insert.setParameter(2, comment.getUser().trim());
				insert.setParameter(3, comment.getComment().trim());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				comment.setId(id);
			} catch(Exception e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}	
			
		}
		return comment;
	}

	public void removeUserComments(String user, int termId) {
		try(Query query = new Query("DELETE FROM oto_term_comment WHERE user = ? and term = ?")) {
			query.setParameter(1, user);
			query.setParameter(2, termId);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
	}

	public void update(Comment comment, int termId) {
		try(Query query = new Query("UPDATE oto_term_comment SET term = ?, user = ?, comment = ? WHERE id = ?")) {
			query.setParameter(1, termId);
			query.setParameter(2, comment.getUser());
			query.setParameter(3, comment.getComment());
			query.setParameter(4, comment.getId());	
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
	}

	public void remove(Comment comment)  {
		try(Query query = new Query("DELETE FROM oto_term_comment WHERE id = ?")) {
			query.setParameter(1, comment.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
	}
	
	public List<Comment> getComments(Term term)  {
		List<Comment> comments = new LinkedList<Comment>();
		try(Query query = new Query("SELECT * FROM oto_term_comment WHERE term = ?")) {
			query.setParameter(1, term.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				comments.add(get(id));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}	
		return comments;
	}

	public void ensure(List<Comment> comments, int termId) {
		String notInComments = "";
		for(Comment comment : comments) 
			notInComments += comment.getId() + ",";
		notInComments = notInComments.isEmpty() ? "" : notInComments.substring(0, notInComments.length() - 1);
		String deleteOldCommentsQuery = notInComments.isEmpty() ? "DELETE FROM `oto_term_comment` WHERE term = ?" : 
			 "DELETE FROM `oto_term_comment` WHERE term = ? AND id NOT IN (" + notInComments + ")";
		try(Query deleteOldComments = new Query(deleteOldCommentsQuery)) {
			deleteOldComments.setParameter(1, termId);
			deleteOldComments.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		for(Comment comment : comments)
			if(!comment.hasId())
				insert(comment, termId);
			else
				update(comment, termId);
	}
	
}
