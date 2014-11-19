package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.oto.shared.LabelNameNormalizer;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.HighlightLabel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TrashLabel;

public class LabelDAO {
	
	private LabelingDAO labelingDAO;
	private SynonymDAO synonymDAO;
	
	protected LabelDAO() {} 
	
	public void setLabelingDAO(LabelingDAO labelingDAO) {
		this.labelingDAO = labelingDAO;
	}
	
	public void setSynonymDAO(SynonymDAO synonymDAO) {
		this.synonymDAO = synonymDAO;
	}

	public Label get(int id)  {
		Label label = null;
		try(Query query = new Query("SELECT * FROM oto_label WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				label = createLabel(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return label;
	}
	
	private Label createLabel(ResultSet result) throws SQLException  {
		int id = result.getInt(1);
		int collectionId = result.getInt(2);
		String type = result.getString(3);
		String name = result.getString(4);
		String description = result.getString(5);
		Label label;
		switch(type) {
		case "TrashLabel":
			label = new TrashLabel(id, collectionId, name, description);
			break;
		case "HighlightLabel":
			label = new HighlightLabel(id, collectionId, name, description);
			break;
		default:
			label = new Label(id, collectionId, name, description);
		}
		
		label.setMainTerms(labelingDAO.getMainTerms(label));
		label.setMainTermSynonymsMap(synonymDAO.get(label));
		return label;
	}

	public Label insert(Label label, int collectionId)  {
		label.setName(LabelNameNormalizer.normalize(label.getName()));
		if(!label.hasId()) {
			Label result = null;
			try(Query insert = new Query("INSERT INTO `oto_label` " +
					"(`collection`, `type`, `name`, `description`) VALUES (?, ?, ?, ?)")) {
				insert.setParameter(1, collectionId);
				insert.setParameter(2, label.getClass().getSimpleName());
				insert.setParameter(3, label.getName().trim());
				insert.setParameter(4, label.getDescription().trim());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				label.setId(id);
			} catch(Exception e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}
		}
		return label;
	}

	public void update(Label label)  {
		try(Query query = new Query("UPDATE oto_label SET name = ?, description = ? WHERE id = ?")) {
			query.setParameter(1, label.getName());
			query.setParameter(2, label.getDescription());
			query.setParameter(3, label.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}

	public void remove(Label label)  {
		try (Query query = new Query("DELETE FROM oto_label WHERE id = ?")) {
			query.setParameter(1, label.getId());
			query.execute();
		}catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}

	public List<Label> getLabels(Collection collection)  {
		List<Label> labels = new LinkedList<Label>();
		try(Query query = new Query("SELECT * FROM oto_label WHERE collection = ?")) {
			query.setParameter(1, collection.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				labels.add(get(id));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return labels;		
	}

	public void ensure(Collection collection)  {
		String ids = "";
		for(Label label : collection.getLabels()) {
			if(!label.hasId()) {
				Label newLabel = insert(label, collection.getId());
				label.setId(newLabel.getId());
				ids += newLabel.getId() + ",";
			}
			else {
				ids += label.getId() + ",";
				update(label);
			}
		}
		ids = (ids.isEmpty() ? ids : ids.substring(0, ids.length() - 1));
		
		String selectOldLabelsQuery = ids.isEmpty() ? "SELECT id FROM oto_label WHERE collection = ?" : 
			"SELECT id FROM oto_label WHERE collection = ? AND id NOT IN (" + ids + ")";
		try(Query selectOldLabels = new Query(selectOldLabelsQuery)) {
			selectOldLabels.setParameter(1, collection.getId());
			ResultSet resultSet = selectOldLabels.execute();
			while(resultSet.next()) {
				int idToDelete = resultSet.getInt(1);
				
				try(Query removeOldLabels = new Query("DELETE FROM oto_label WHERE id = ?")) {
					removeOldLabels.setParameter(1, idToDelete);
					removeOldLabels.execute();
				}
				
				try(Query removeOldSynonyms = new Query("DELETE FROM oto_synonym WHERE label = ?")) {
					removeOldSynonyms.setParameter(1, idToDelete);
					removeOldSynonyms.execute();
				}
			
				try(Query removeOldLabeling = new Query("DELETE FROM oto_labeling WHERE label = ?")) {
					removeOldLabeling.setParameter(1, idToDelete);
					removeOldLabeling.execute();
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
			
		for(Label label : collection.getLabels()) {
			labelingDAO.ensure(label, label.getMainTerms());
			synonymDAO.ensure(label, label.getMainTerms());
		}
	}
}

