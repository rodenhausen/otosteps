package edu.arizona.biosemantics.oto2.oto.server.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Categorization;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Synonymization;

public class CommunityDAO {

	public static class LabelCount implements Comparable<LabelCount> {
		public String labelName;
		public int count;
		public LabelCount(String labelName, int count) {
			this.labelName = labelName;
			this.count = count;
		}
		@Override
		public int compareTo(LabelCount o) {
			return o.count - this.count;
		}
	}
	
	public static class HistoricSynonymCalculation {
		public Label label;
		public Term term;
		public Term otherTerm;
		public HistoricSynonymCalculation(Label label, Term term, Term otherTerm) {
			this.label = label;
			this.term = term;
			this.otherTerm = otherTerm;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result
					+ ((otherTerm == null) ? 0 : otherTerm.hashCode());
			result = prime * result + ((term == null) ? 0 : term.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HistoricSynonymCalculation other = (HistoricSynonymCalculation) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			if (otherTerm == null) {
				if (other.otherTerm != null)
					return false;
			} else if (!otherTerm.equals(other.otherTerm))
				return false;
			if (term == null) {
				if (other.term != null)
					return false;
			} else if (!term.equals(other.term))
				return false;
			return true;
		}
	}
	
	public static class HistoricSynonymResult {
		public boolean result = false;
		public Term mainTerm = null;
	}
	
	public static class RawHistoricSynonymCalculation {
		public String label;
		public String term;
		public String otherTerm;
		public RawHistoricSynonymCalculation(String label, String term, String otherTerm) {
			this.label = label;
			this.term = term;
			this.otherTerm = otherTerm;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result
					+ ((otherTerm == null) ? 0 : otherTerm.hashCode());
			result = prime * result + ((term == null) ? 0 : term.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RawHistoricSynonymCalculation other = (RawHistoricSynonymCalculation) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			if (otherTerm == null) {
				if (other.otherTerm != null)
					return false;
			} else if (!otherTerm.equals(other.otherTerm))
				return false;
			if (term == null) {
				if (other.term != null)
					return false;
			} else if (!term.equals(other.term))
				return false;
			return true;
		}
	}
	
	public static class RawHistoricSynonymResult {
		public boolean result = false;
		public String mainTerm = null;
	}
	
	private CollectionDAO collectionDAO;
	private TermDAO termDAO;
	private LabelDAO labelDAO;
	private LabelingDAO labelingDAO;

	protected CommunityDAO() { }
	
	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}
	
	public void setLabelingDAO(LabelingDAO labelingDAO) {
		this.labelingDAO = labelingDAO;
	}
	
	public void setLabelDAO(LabelDAO labelDAO) {
		this.labelDAO = labelDAO;
	}
	
	public Set<Categorization> getCategorizations(String type)  {
		Set<Categorization> result = new HashSet<Categorization>();
		
		Set<String> visitedTerms = new HashSet<String>();
		for(Collection collection : collectionDAO.getCollections(type)) {
			for(Term term : collection.getTerms()) {
				if(!visitedTerms.contains(term.getTerm())) {
					visitedTerms.add(term.getTerm());
					List<LabelCount> labelCounts = this.getLabelCounts(type, term);
					Set<String> labels = determineLabels(labelCounts);
					result.add(new Categorization(term.getTerm(), labels));
				}
			}
		}
		
		return result;
	}
	
	public Set<Synonymization> getSynoymizations(String type) {
		Set<Synonymization> result = new HashSet<Synonymization>();
		
		Set<Categorization> categorizations = getCategorizations(type);
		Map<String, Map<String, Map<String, Integer>>> synonymGroups = createSynonymGroupsFromHistory(categorizations);
		
		Set<String> visitedTerms = new HashSet<String>();
		for(String label : synonymGroups.keySet()) {
			Map<String, Map<String, Integer>> labelGroups = synonymGroups.get(label);
			for(String term : labelGroups.keySet()) {
				if(!visitedTerms.contains(term)) {
					Map<String, Integer> group = labelGroups.get(term);
					visitedTerms.addAll(group.keySet());
					String mainTerm = getHistoricMainTerm(group);
					Set<String> synonyms = group.keySet();
					synonyms.remove(mainTerm);
					result.add(new Synonymization(label, mainTerm, synonyms));
				}
			}
		}
		return result;
	}
	
	public Map<String, Map<String, Map<String, Integer>>> createSynonymGroupsFromHistory(Set<Categorization> categorizations) {
		Map<String, Map<String, Map<String, Integer>>> synonymGroups = 
				new HashMap<String, Map<String, Map<String, Integer>>>();
		
		Set<RawHistoricSynonymCalculation> calculations = new HashSet<RawHistoricSynonymCalculation>();
		for(Categorization categorization : categorizations) {
			String term = categorization.getTerm();
			for(String label : categorization.getCategories()) {
				for(String otherTerm : getAllTermsOfLabel(label)) {
					if(!term.equals(otherTerm)) {
						RawHistoricSynonymCalculation calculation = new RawHistoricSynonymCalculation(label, term, otherTerm);
						RawHistoricSynonymCalculation reverseCalculation = new RawHistoricSynonymCalculation(label, otherTerm, term);
						if(!calculations.contains(calculation) && !calculations.contains(reverseCalculation)) {
							calculations.add(calculation);
							calculations.add(reverseCalculation);
							RawHistoricSynonymResult historicSynonymResult = isHistoricSynonymRaw(calculation);
							if(historicSynonymResult.result) {
								if(!synonymGroups.containsKey(label))
									synonymGroups.put(label, new HashMap<String, Map<String, Integer>>());
								Map<String, Map<String, Integer>> labelsGroups = synonymGroups.get(label);
								Map<String, Integer> termGroup = labelsGroups.get(term);
								Map<String, Integer> otherTermGroup = labelsGroups.get(otherTerm);
								if(termGroup == null && otherTermGroup == null) {
									Map<String, Integer> newGroup = new HashMap<String, Integer>();
									newGroup.put(term, historicSynonymResult.mainTerm == term ? 1 : 0);
									newGroup.put(otherTerm, historicSynonymResult.mainTerm == otherTerm ? 1 : 0);
									labelsGroups.put(term, newGroup);
									labelsGroups.put(otherTerm, newGroup);
								}
								if(termGroup != null && otherTermGroup == null) {
									termGroup.put(otherTerm, historicSynonymResult.mainTerm == otherTerm ? 1 : 0);
									labelsGroups.put(otherTerm, termGroup);
								}
								if(termGroup == null && otherTermGroup != null) {
									otherTermGroup.put(term, historicSynonymResult.mainTerm == term ? 1 : 0);
									labelsGroups.put(term, otherTermGroup);
								}
								if(termGroup != null && otherTermGroup != null && 
										!termGroup.equals(otherTermGroup)) {
									termGroup.putAll(otherTermGroup);
									if(historicSynonymResult.mainTerm == term)
										termGroup.put(term, termGroup.get(term) + 1);
									if(historicSynonymResult.mainTerm == otherTerm)
										termGroup.put(otherTerm, termGroup.get(otherTerm) + 1);
									for(String otherTermGroupTerms : otherTermGroup.keySet())
										labelsGroups.put(otherTermGroupTerms, termGroup);
								}
							}
						}
					}
				}
			}
		}
		return synonymGroups;
	}
	
	private List<String> getAllTermsOfLabel(String label) {
		List<String> terms = new LinkedList<String>();
		try(Query query = new Query("SELECT t.term FROM oto_labeling x, oto_label l, oto_term t "
				+ "WHERE x.label = l.id AND l.name = ? AND t.id = x.term")) {
			query.setParameter(1, label);
			ResultSet result = query.execute();
			while(result.next()) {
				terms.add(result.getString(1));
			}
			try(Query synonymQuery = new Query("SELECT t.term FROM oto_synonym s, oto_label l, oto_term t "
					+ "WHERE s.label = l.id AND l.name = ? AND t.id = s.synonymterm")) {
				synonymQuery.setParameter(1, label);
				result = query.execute();
				while(result.next()) 
					terms.add(result.getString(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return terms;
	}

	public Set<String> determineLabels(List<LabelCount> labelCounts) {
		Collections.sort(labelCounts);
		Set<String> result = new HashSet<String>();
		int overallCount = 0;
		for(LabelCount labelCount : labelCounts)
			overallCount += labelCount.count;
		
		for(LabelCount labelCount : labelCounts)
			// is going to be valid for maximally 3 labels, which was requirement given
			if(labelCount.count >= 0.5 * (overallCount - labelCount.count))
				result.add(labelCount.labelName);
		
		if(result.isEmpty() && !labelCounts.isEmpty())
			result.add(labelCounts.get(0).labelName);
		return result;
	}

	private List<LabelCount> getLabelCounts(String type, Term term) {
		try(Query query = new Query("SELECT l.name FROM oto_labeling x, oto_label l, oto_collection c, oto_term t "
				+ "WHERE x.label = l.id AND l.collection = c.id AND c.type= ? AND x.term = t.id AND t.term = ? "
				+ "UNION "
				+ "SELECT l.name FROM oto_label l, oto_collection c, oto_term t, oto_synonym s "
				+ "WHERE s.label = l.id AND l.collection = c.id AND c.type = ? AND s.synonymterm = t.id AND t.term = ?")) {
			query.setParameter(1, type);
			query.setParameter(2, term.getTerm());
			query.setParameter(3, type);
			query.setParameter(4, term.getTerm());
			ResultSet result = query.execute();
			Map<String, Integer> labelingMap = new HashMap<String, Integer>();
			while(result.next()) {
				String labelName = result.getString(1);
				if(!labelingMap.containsKey(labelName))
					labelingMap.put(labelName, 0);
				labelingMap.put(labelName, labelingMap.get(labelName) + 1);
			}
			
			List<LabelCount> labelCounts = new ArrayList<LabelCount>();
			for(String labelName : labelingMap.keySet()) {
				labelCounts.add(new LabelCount(labelName, labelingMap.get(labelName)));
			}
			Collections.sort(labelCounts);
			return labelCounts;
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
			return new LinkedList<LabelCount>();
		}
	}


	public String getSpelling(Collection collection, Term term) {
		try(Query query = new Query("SELECT t.* FROM oto_term t, oto_bucket b, " +
				"oto_collection c WHERE t.bucket = b.id AND b.collection = c.id AND c.id != ? AND" +
				" c.type = ? AND t.original_term = ?")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, collection.getType());
			query.setParameter(3, term.getOriginalTerm());
			ResultSet result = query.execute();
			Map<String, Integer> spellingMap = new HashMap<String, Integer>();
			while(result.next()) {
				Term historyTerm = termDAO.createTerm(result);
				String spelling = historyTerm.getTerm();
				if(!spellingMap.containsKey(spelling))
					spellingMap.put(spelling, 0);
				spellingMap.put(spelling, spellingMap.get(spelling) + 1);
			}
			int maxVotes = 0;
			String useSpelling = term.getOriginalTerm();
			for(String spelling : spellingMap.keySet()) {
				int votes = spellingMap.get(spelling);
				if(votes > maxVotes) {
					maxVotes = votes;
					useSpelling = spelling;
				}
			}
			return useSpelling;
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
			return term.getTerm();
		}
	}

	public boolean getUseless(Collection collection, Term term) {
		try(Query query = new Query("SELECT t.* FROM oto_term t, oto_bucket b, " +
				"oto_collection c WHERE t.bucket = b.id AND b.collection = c.id AND c.id != ? AND" +
				" c.type = ? AND t.term = ?")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, collection.getType());
			query.setParameter(3, term.getTerm());
			ResultSet result = query.execute();
			Map<Boolean, Integer> uselessMap = new HashMap<Boolean, Integer>();
			while(result.next()) {
				Term historyTerm = termDAO.createTerm(result);
				Boolean useless = historyTerm.getUseless();
				if(!uselessMap.containsKey(useless))
					uselessMap.put(useless, 0);
				uselessMap.put(useless, uselessMap.get(useless) + 1);
			}
			int maxVotes = 0;
			Boolean useless = term.getUseless();
			for(Boolean value : uselessMap.keySet()) {
				int votes = uselessMap.get(value);
				if(votes > maxVotes) {
					maxVotes = votes;
					useless = value;
				}
			}
			return useless;
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
			return term.getUseless();
		}
	}

	public List<LabelCount> getLabelCounts(Collection collection, Term term) {
		try(Query query = new Query("SELECT l.name FROM oto_labeling x, oto_label l, oto_collection c, oto_term t "
				+ "WHERE x.label = l.id AND l.collection = c.id AND c.id != ? AND c.type= ? AND x.term = t.id AND t.term = ? "
				+ "UNION "
				+ "SELECT l.name FROM oto_label l, oto_collection c, oto_term t, oto_synonym s "
				+ "WHERE s.label = l.id AND l.collection = c.id AND c.id != ? AND c.type = ? AND s.synonymterm = t.id AND t.term = ?")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, collection.getType());
			query.setParameter(3, term.getTerm());
			query.setParameter(4, collection.getId());
			query.setParameter(5, collection.getType());
			query.setParameter(6, term.getTerm());
			ResultSet result = query.execute();
			Map<String, Integer> labelingMap = new HashMap<String, Integer>();
			while(result.next()) {
				String labelName = result.getString(1);
				if(!labelingMap.containsKey(labelName))
					labelingMap.put(labelName, 0);
				labelingMap.put(labelName, labelingMap.get(labelName) + 1);
			}
			
			List<LabelCount> labelCounts = new ArrayList<LabelCount>();
			for(String labelName : labelingMap.keySet()) {
				labelCounts.add(new LabelCount(labelName, labelingMap.get(labelName)));
			}
			Collections.sort(labelCounts);
			return labelCounts;
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
			return new LinkedList<LabelCount>();
		}
	}
	
	private RawHistoricSynonymResult isHistoricSynonymRaw(RawHistoricSynonymCalculation calculation) {
		HistoricSynonymCounts counts = isHistoricSynonym(null, calculation);		
		RawHistoricSynonymResult result = new RawHistoricSynonymResult();
		result.result = counts.synonymsCount > counts.bothMainTermsCount;
		result.mainTerm = counts.termMainTermCount >= counts.otherTermMainTermCount ? calculation.term : calculation.otherTerm;
		return result;
	}
	
	public HistoricSynonymResult isHistoricSynonymForCollection(Collection collection, HistoricSynonymCalculation calculation) {
		RawHistoricSynonymCalculation rawHistoricSynonymCalculation = new RawHistoricSynonymCalculation(calculation.label.getName(), 
				calculation.term.getTerm(), calculation.otherTerm.getTerm());
		
		HistoricSynonymCounts counts = isHistoricSynonym(collection.getId(), rawHistoricSynonymCalculation);		
		HistoricSynonymResult result = new HistoricSynonymResult();
		result.result = counts.synonymsCount > counts.bothMainTermsCount;
		result.mainTerm = counts.termMainTermCount >= counts.otherTermMainTermCount ? calculation.term : calculation.otherTerm;
		return result;
	}
	
	private class HistoricSynonymCounts {
		public int synonymsCount;
		public int bothMainTermsCount;
		public int termMainTermCount;
		public int otherTermMainTermCount;
		public HistoricSynonymCounts(int synonymsCount,
				int bothMainTermsCount, int termMainTermCount,
				int otherTermMainTermCount) {
			this.synonymsCount = synonymsCount;
			this.bothMainTermsCount = bothMainTermsCount;
			this.termMainTermCount = termMainTermCount;
			this.otherTermMainTermCount = otherTermMainTermCount;
		}
	}
	
	public HistoricSynonymCounts isHistoricSynonym(Integer notCollectionId, RawHistoricSynonymCalculation calculation) {		
		int bothMainTermsCount = 0;
		int synonymsCount = 0;
		int termMainTermCount = 0;
		int otherTermMainTermCount = 0;
		
		String sql =  "SELECT c.id, t.term FROM oto_collection c," +
				" oto_labeling x, oto_term t, oto_label l" +
				" WHERE " +
				" c.id != ? AND c.id = l.collection AND l.name = ? AND l.id = x.label AND t.id = x.term";
		if(notCollectionId == null) {
			sql =  "SELECT c.id, t.term FROM oto_collection c," +
					" oto_labeling x, oto_term t, oto_label l" +
					" WHERE " +
					" c.id = l.collection AND l.name = ? AND l.id = x.label AND t.id = x.term";
		}
		
		
		try(Query query = new Query(sql)) {
			if(notCollectionId == null) {
				query.setParameter(1, calculation.label);
			} else {
				query.setParameter(1, notCollectionId);
				query.setParameter(2, calculation.label);
			}
			ResultSet result = query.execute();
			HashMap<Integer, Set<String>> collectionLabeldTerms = new HashMap<Integer, Set<String>>();
			while(result.next()) {
				int collectionId = result.getInt(1);
				String labeledTerm = result.getString(2);
				if(!collectionLabeldTerms.containsKey(collectionId))
					collectionLabeldTerms.put(collectionId, new HashSet<String>());
				collectionLabeldTerms.get(collectionId).add(labeledTerm);
			}
			for(Integer collectionId : collectionLabeldTerms.keySet()) {
				Set<String> labeledTerms = collectionLabeldTerms.get(collectionId);
				if(labeledTerms.contains(calculation.term) && labeledTerms.contains(calculation.otherTerm))
					bothMainTermsCount++;
				else {
					try(Query synonymQuery = new Query("SELECT * FROM " +
							"oto_collection c, oto_synonym s, oto_label l, " +
							"oto_term t1, oto_term t2 WHERE " +
							"c.id = ? AND l.collection = c.id AND l.name = ? AND " +
							"s.label = l.id AND s.mainterm = t1.id AND s.synonymterm = t2.id " +
							"AND t1.term = ? AND t2.term = ?")) {
						synonymQuery.setParameter(1, collectionId);
						synonymQuery.setParameter(2, calculation.label);
						synonymQuery.setParameter(3, calculation.term);
						synonymQuery.setParameter(4, calculation.otherTerm);
						ResultSet synonymResult = synonymQuery.execute();
						while(synonymResult.next()) {
							synonymsCount++;
							termMainTermCount++;
						}
					}
					try(Query synonymQuery = new Query("SELECT * FROM " +
							"oto_collection c, oto_synonym s, oto_label l, " +
							"oto_term t1, oto_term t2 WHERE " +
							"c.id = ? AND l.collection = c.id AND l.name = ? AND " +
							"s.label = l.id AND s.mainterm = t1.id AND s.synonymterm = t2.id " +
							"AND t1.term = ? AND t2.term = ?")) {
						synonymQuery.setParameter(1, collectionId);
						synonymQuery.setParameter(2, calculation.label);
						synonymQuery.setParameter(3, calculation.otherTerm);
						synonymQuery.setParameter(4, calculation.term);
						ResultSet synonymResult = synonymQuery.execute();
						while(synonymResult.next()) {
							synonymsCount++;
							otherTermMainTermCount++;
						}
					}
				}	
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		
		HistoricSynonymCounts result = new HistoricSynonymCounts(synonymsCount, bothMainTermsCount, termMainTermCount, otherTermMainTermCount);
		return result;
	}
	
	public Map<Label, Map<Term, Map<Term, Integer>>> createSynonymGroupsFromHistory(Collection collection) {
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroups = 
				new HashMap<Label, Map<Term, Map<Term, Integer>>>();
		
		Set<HistoricSynonymCalculation> calculations = new HashSet<HistoricSynonymCalculation>();
		for(Term term : collection.getTerms()) {
			Set<Label> labels = labelingDAO.getLabels(term);
			for(Label label : labels) {
				for(Term otherTerm : labelingDAO.getAllTerms(label)) {
					if(!term.equals(otherTerm)) {
						HistoricSynonymCalculation calculation = new HistoricSynonymCalculation(label, term, otherTerm);
						HistoricSynonymCalculation reverseCalculation = new HistoricSynonymCalculation(label, otherTerm, term);
						if(!calculations.contains(calculation) && !calculations.contains(reverseCalculation)) {
							calculations.add(calculation);
							calculations.add(reverseCalculation);
							HistoricSynonymResult historicSynonymResult = isHistoricSynonymForCollection(collection, calculation);
							if(historicSynonymResult.result) {
								if(!synonymGroups.containsKey(label))
									synonymGroups.put(label, new HashMap<Term, Map<Term, Integer>>());
								Map<Term, Map<Term, Integer>> labelsGroups = synonymGroups.get(label);
								Map<Term, Integer> termGroup = labelsGroups.get(term);
								Map<Term, Integer> otherTermGroup = labelsGroups.get(otherTerm);
								if(termGroup == null && otherTermGroup == null) {
									Map<Term, Integer> newGroup = new HashMap<Term, Integer>();
									newGroup.put(term, historicSynonymResult.mainTerm == term ? 1 : 0);
									newGroup.put(otherTerm, historicSynonymResult.mainTerm == otherTerm ? 1 : 0);
									labelsGroups.put(term, newGroup);
									labelsGroups.put(otherTerm, newGroup);
								}
								if(termGroup != null && otherTermGroup == null) {
									termGroup.put(otherTerm, historicSynonymResult.mainTerm == otherTerm ? 1 : 0);
									labelsGroups.put(otherTerm, termGroup);
								}
								if(termGroup == null && otherTermGroup != null) {
									otherTermGroup.put(term, historicSynonymResult.mainTerm == term ? 1 : 0);
									labelsGroups.put(term, otherTermGroup);
								}
								if(termGroup != null && otherTermGroup != null && 
										!termGroup.equals(otherTermGroup)) {
									termGroup.putAll(otherTermGroup);
									if(historicSynonymResult.mainTerm == term)
										termGroup.put(term, termGroup.get(term) + 1);
									if(historicSynonymResult.mainTerm == otherTerm)
										termGroup.put(otherTerm, termGroup.get(otherTerm) + 1);
									for(Term otherTermGroupTerms : otherTermGroup.keySet())
										labelsGroups.put(otherTermGroupTerms, termGroup);
								}
							}
						}
					}
				}
			}
		}
		return synonymGroups;
	}

	public <T> T getHistoricMainTerm(Map<T, Integer> group) {
		T mainTerm = null;
		int maxVotes = -1;
		for(T term : group.keySet())
			if(group.get(term) > maxVotes) {
				mainTerm = term;
				maxVotes = group.get(term);
			}	
		return mainTerm;
	}
	
}
