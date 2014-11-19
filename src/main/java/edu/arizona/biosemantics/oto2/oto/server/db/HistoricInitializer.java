package edu.arizona.biosemantics.oto2.oto.server.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.common.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.common.model.TermCategory;
import edu.arizona.biosemantics.oto.common.model.TermSynonym;
import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.oto2.oto.server.db.CommunityDAO.LabelCount;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class HistoricInitializer {

	private DAOManager daoManager;

	public HistoricInitializer(DAOManager daoManager) {
		this.daoManager = daoManager;
	}
	
	public void initialize(Collection collection) {
		List<TermCategory> termCategories = new LinkedList<TermCategory>();
		List<TermSynonym> termSynonyms = new LinkedList<TermSynonym>();
		
		try(OTOClient client = new OTOClient(Configuration.otoClientUrl)) {
			client.open();
			Future<GlossaryDownload> futureGlosaryDownload = client.getGlossaryDownload(collection.getType());
			try {
				GlossaryDownload glossaryDownload = futureGlosaryDownload.get();
				termCategories = glossaryDownload.getTermCategories();
				termSynonyms = glossaryDownload.getTermSynonyms();
			} catch (InterruptedException | ExecutionException e) {
				log(LogLevel.ERROR, "Couldn't download OTO glossary to initialize collection", e);
			}
		}
		
		initializeTerm(collection);
		initializeLabeling(collection, termCategories);
		initializeSynonym(collection, termSynonyms);
	}
	
	public void initializeTerm(Collection collection) {
		TermDAO termDAO = daoManager.getTermDAO();
		BucketDAO bucketDAO = daoManager.getBucketDAO();
		termDAO.resetTerms(collection);
		try {
			List<Term> terms = termDAO.getTerms(collection);
			for(Term term : terms) {
				term.setTerm(daoManager.getCommunityDAO().getSpelling(collection, term));
				term.setUseless(daoManager.getCommunityDAO().getUseless(collection, term));
				Bucket bucket = bucketDAO.get(term);
				termDAO.update(term, bucket.getId());
			}	
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}

	public void initializeLabeling(Collection collection, List<TermCategory> termCategories) {
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		labelingDAO.remove(collection);
		
		Set<Term> initializedFromGlossary = initializeLabelingFromGlossary(collection, termCategories);
		
		Set<Term> structureTerms = new HashSet<Term>();
		for(Bucket bucket : collection.getBuckets())
			if(bucket.getName().equalsIgnoreCase("structures")) {
				structureTerms.addAll(bucket.getTerms());
			}
		Label structureLabel = null;
		for(Label label : collection.getLabels()) {
			if(label.getName().equalsIgnoreCase("structure")) {
				structureLabel = label;
			}
		}
		
		if(structureLabel != null) {
			initializeLabelingFromStructureTerms(collection, structureTerms, structureLabel);
			initializeLabelingRemainingFromCommunity(collection, initializedFromGlossary, structureTerms);
		} else 
			initializeLabelingRemainingFromCommunity(collection, initializedFromGlossary, new HashSet<Term>());
	}
	
	private Set<Term> initializeLabelingFromGlossary(Collection collection, List<TermCategory> termCategories) {
		Set<Term> result = new HashSet<Term>();
		
		TermDAO termDAO = daoManager.getTermDAO();
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
			
		Map<String, Set<String>> termCategoryMap = new HashMap<String, Set<String>>();
		for(TermCategory termCategory : termCategories) {
			if(!termCategoryMap.containsKey(termCategory.getTerm()) )
				termCategoryMap.put(termCategory.getTerm(), new HashSet<String>());
			termCategoryMap.get(termCategory.getTerm()).add(termCategory.getCategory());
		}
		
		try {
			Map<String, Label> labelNameMap = new HashMap<String, Label>();
			for(Label label : collection.getLabels())
				labelNameMap.put(label.getName(), label);
			
			List<Term> terms = termDAO.getTerms(collection);
			for(Term term : terms) {
				if(termCategoryMap.containsKey(term.getTerm())) {
					for(String category : termCategoryMap.get(term.getTerm())) {
						if(labelNameMap.containsKey(category)) {
							labelingDAO.insert(term, labelNameMap.get(category));
							result.add(term);
						}
					}
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return result;
	}


	private void initializeLabelingFromStructureTerms(Collection collection, Set<Term> structureTerms, Label structureLabel) {
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		for(Term structureTerm : structureTerms) 
			labelingDAO.insert(structureTerm, structureLabel);
	}

	private void initializeLabelingRemainingFromCommunity(Collection collection, Set<Term> initializedFromGlossary, Set<Term> structureTerms) {
		TermDAO termDAO = daoManager.getTermDAO();
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		try {
			Map<String, Label> labelNameMap = new HashMap<String, Label>();
			for(Label label : collection.getLabels())
				labelNameMap.put(label.getName(), label);
			
			List<Term> terms = termDAO.getTerms(collection);
			for(Term term : terms) {
				if(!initializedFromGlossary.contains(term) && !structureTerms.contains(term)) {
					List<LabelCount> labelCounts = daoManager.getCommunityDAO().getLabelCounts(collection, term);
					Set<String> labels = daoManager.getCommunityDAO().determineLabels(labelCounts);
					for(String label : labels) {
						if(labelNameMap.containsKey(label))
							labelingDAO.insert(term, labelNameMap.get(label));
					}
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}
	
	public void initializeSynonym(Collection collection, List<TermSynonym> termSynonyms) {
		SynonymDAO synonymDAO = daoManager.getSynonymDAO();
		synonymDAO.remove(collection);	
		initializeSynonyms(collection, termSynonyms);
	}
	
	// it is unclear from oto synonym downloads whether all the synonyms of a term use the same main term
	// in a TermSynonym object. Therefore choose one by voting of appearance as "term" in termSynonyms
	private Map<Label, Map<Term, Map<Term, Integer>>> getSynonymGroupsFromGlossary(Collection collection, List<TermSynonym> termSynonyms) {
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		
		Map<String, Label> labelNameMap = new HashMap<String, Label>();
		for(Label label : collection.getLabels())
			labelNameMap.put(label.getName(), label);
		
		Map<String, Term> termNameMap = new HashMap<String, Term>(); 
		for(Term term : collection.getTerms()) 
			termNameMap.put(term.getTerm(), term);
		
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroups = 
				new HashMap<Label, Map<Term, Map<Term, Integer>>>();
		
		for(TermSynonym termSynonym : termSynonyms) {
			String category = termSynonym.getCategory();
			if(labelNameMap.containsKey(category) && termNameMap.containsKey(termSynonym.getTerm()) && 
					termNameMap.containsKey(termSynonym.getSynonym())) {
				Label label = labelNameMap.get(category);
				Term term  = termNameMap.get(termSynonym.getTerm());
				Term synonym = termNameMap.get(termSynonym.getSynonym());
				
				List<Term> labelMainTerms = labelingDAO.getMainTerms(label);
				if(labelMainTerms.contains(term) && labelMainTerms.contains(synonym)) {
					if(!synonymGroups.containsKey(label))
						synonymGroups.put(label, new HashMap<Term, Map<Term, Integer>>());
					Map<Term, Map<Term, Integer>> labelsGroups = synonymGroups.get(label);
					Map<Term, Integer> termGroup = labelsGroups.get(term);
					Map<Term, Integer> otherTermGroup = labelsGroups.get(synonym);
					if(termGroup == null && otherTermGroup == null) {
						Map<Term, Integer> newGroup = new HashMap<Term, Integer>();
						newGroup.put(term, 1);
						newGroup.put(synonym, 0);
						labelsGroups.put(term, newGroup);
						labelsGroups.put(synonym, newGroup);
					}
					if(termGroup != null && otherTermGroup == null) {
						termGroup.put(synonym, 0);
						labelsGroups.put(synonym, termGroup);
					}
					if(termGroup == null && otherTermGroup != null) {
						otherTermGroup.put(term, 1);
						labelsGroups.put(term, otherTermGroup);
					}
					if(termGroup != null && otherTermGroup != null && 
							!termGroup.equals(otherTermGroup)) {
						termGroup.putAll(otherTermGroup);
						termGroup.put(term, termGroup.get(term) + 1);
						for(Term otherTermGroupTerm : otherTermGroup.keySet())
							labelsGroups.put(otherTermGroupTerm, termGroup);
					}
				}
			}
		}
		return synonymGroups;
	}

	private void initializeSynonyms(Collection collection, List<TermSynonym> termSynonyms) {
		SynonymDAO synonymDAO = daoManager.getSynonymDAO();
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroups = daoManager.getCommunityDAO().createSynonymGroupsFromHistory(collection);
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroupsGlossary = getSynonymGroupsFromGlossary(collection, termSynonyms);
		
		//merge
		for(Label label : synonymGroupsGlossary.keySet()) {
			if(!synonymGroups.containsKey(label)) 
				synonymGroups.put(label, synonymGroupsGlossary.get(label));
			else {
				Map<Term, Map<Term, Integer>> labelsGroups = synonymGroups.get(label);
				Map<Term, Map<Term, Integer>> labelsGroupsGlossary = synonymGroupsGlossary.get(label);
				for(Term term : labelsGroupsGlossary.keySet()) {
					if(!labelsGroups.containsKey(term)) 
						labelsGroups.put(term, labelsGroupsGlossary.get(term));
					else {
						Map<Term, Integer> termGroup = labelsGroups.get(term);
						Map<Term, Integer> termGroupGlossary = labelsGroupsGlossary.get(term);
						for(Term groupTerm : termGroupGlossary.keySet()) {
							if(!termGroup.containsKey(groupTerm)) 
								termGroup.put(groupTerm, termGroupGlossary.get(groupTerm));
						}
					}
				}
			}
		}
		
		//insert to db
		Set<Map<Term, Integer>> visitedTermGroups = new HashSet<Map<Term, Integer>>();
		for(Label label : synonymGroups.keySet()) {
			Map<Term, Map<Term, Integer>> labelsGroups = synonymGroups.get(label);
			for(Term term : labelsGroups.keySet()) {
				Map<Term, Integer> termGroup = labelsGroups.get(term);
				if(!visitedTermGroups.contains(termGroup)) {
					visitedTermGroups.add(termGroup);
					
					Term mainTerm = daoManager.getCommunityDAO().getHistoricMainTerm(termGroup);
					for(Term groupTerm : termGroup.keySet())
						if(!groupTerm.equals(mainTerm))
							synonymDAO.insert(label, mainTerm, groupTerm);
				}
			}
		}		
	}	
	
}
