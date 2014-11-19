package edu.arizona.biosemantics.oto2.oto.server.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.bioportal.model.Search;
import edu.arizona.biosemantics.bioportal.model.SearchResultPage;
import edu.arizona.biosemantics.bioportal.model.SearchResultPage.SearchResult;
import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class OntologyDAO {
	
	public static BioPortalClient bioportalClient;
	private Set<Ontology> lastRetrievedOntologies = new LinkedHashSet<Ontology>();
	private Map<String, Ontology> lastRetrievedOntologiesMap = new HashMap<String, Ontology>();
	
	protected OntologyDAO() { }

	public List<OntologyEntry> get(Term term) {
		return getOntologyEntries(createSearch(term));
	}
	
	private Search createSearch(Term term) {
		Search search = new Search();
		search.setQuery(term.getTerm());
		search.setExactMatch(true);
		search.setRequiresDefinition(true);
		search.setIncludeObsolete(false);
		return search;
	}

	private List<OntologyEntry> getOntologyEntries(Search search) {
		List<OntologyEntry> result = new LinkedList<OntologyEntry>();
		try {
			SearchResultPage searchResultPage = bioportalClient.searchClasses(search).get();
			addOntologyEntries(search, result, searchResultPage);
			int currentPage = 1;
			while(searchResultPage.getNextPage() != null && currentPage++ < searchResultPage.getPageCount()) {
				searchResultPage = bioportalClient.getSearchResultPage(searchResultPage.getNextPage()).get();
				addOntologyEntries(search, result, searchResultPage);
			}
		} catch(ExecutionException | InterruptedException e) {
			log(LogLevel.ERROR, "Exception", e);
		}
		
		highlightDefinitions(search.getQuery(), result);
		if(search.isExactMatch())
			return filterNonExactMatches(search.getQuery(), result);
		return result;
	}
	
	private void highlightDefinitions(String query, List<OntologyEntry> ontologyEntries) {
		for(OntologyEntry entry : ontologyEntries) {
			String definition = entry.getDefinition().replaceAll("(?i)" + query, "<b>" + query + "</b>");
			entry.setDefinition(definition);
		}
	}

	private List<OntologyEntry> filterNonExactMatches(String query, List<OntologyEntry> ontologyEntries) {
		List<OntologyEntry> result = new LinkedList<OntologyEntry>();
		for(OntologyEntry entry : ontologyEntries) {
			if(entry.getLabel().trim().equalsIgnoreCase(query)) {
				result.add(entry);
			}
		}
		return result;
	}

	private void addOntologyEntries(Search search, List<OntologyEntry> result, SearchResultPage searchResultPage) {
		for(SearchResult searchResult : searchResultPage.getSearchResults()) {
			if(searchResult.getDefinitions() == null || searchResult.getDefinitions().isEmpty()) {
				
				//make sure the label is exactly what was searched for and not a partial string
				//if(searchResult.getLabel().trim().equalsIgnoreCase(search.getQuery())) 
					
					//id's in result are not unique
					result.add(new OntologyEntry(searchResult.getId() + "-server-" + result.size(), 
							getOntologyAcronym(searchResult.getOntology()), getOntologyName(searchResult.getOntology()),
							searchResult.getLabel(), "", searchResult.getId()));
			} else {
				
				//make sure the label is exactly what was searched for and not a partial string
				//if(searchResult.getLabel().trim().equalsIgnoreCase(search.getQuery())) 
					for(String definition : searchResult.getDefinitions()) {
						
						//make sure the definition is not empty (bioportal service returns empty ones even though requiresDef is set to true)
						//if(!definition.trim().isEmpty()) {
						
							definition = definition.replaceAll("(?i)" + search.getQuery(), "<b>" + search.getQuery() + "</b>").replaceAll("\n", "</br>");
						
							//id's in result are not unique
							result.add(new OntologyEntry(searchResult.getId() + "-server-" + result.size(), 
									getOntologyAcronym(searchResult.getOntology()), getOntologyName(searchResult.getOntology()),
									searchResult.getLabel(), definition, searchResult.getId()));
						//}
					}
			}
		}
	}

	private String getOntologyName(String ontologyId) {
		if(this.lastRetrievedOntologiesMap.containsKey(ontologyId)) 
			return lastRetrievedOntologiesMap.get(ontologyId).getName();
		return ontologyId;
	}

	private String getOntologyAcronym(String ontologyId) {
		if(this.lastRetrievedOntologiesMap.containsKey(ontologyId)) 
			return lastRetrievedOntologiesMap.get(ontologyId).getAcronym();
		return ontologyId;
	}

	public List<OntologyEntry> get(Term term, List<Ontology> ontologies) {		
		Search search = createSearch(term);
		// no ontologies set will in bioportal automaticlaly search all of them
		// with this option one won't run into the problem of sending off a too long query URL when appending the string of all
		// the ontologies to be searched which causes it to fail
		if(lastRetrievedOntologies.size() != ontologies.size() || !lastRetrievedOntologies.containsAll(ontologies))
			search.setOntologies(createBioportalOntologies(ontologies));
		return getOntologyEntries(search);
	}

	private List<edu.arizona.biosemantics.bioportal.model.Ontology> createBioportalOntologies(List<Ontology> ontologies) {
		List<edu.arizona.biosemantics.bioportal.model.Ontology> result = new LinkedList<edu.arizona.biosemantics.bioportal.model.Ontology>();
		for(Ontology ontology : ontologies)
			result.add(new edu.arizona.biosemantics.bioportal.model.Ontology(ontology.getId(), ontology.getAcronym(), ontology.getName()));
		return result;
	}

	public Set<Ontology> getOntologies() {
		List<edu.arizona.biosemantics.bioportal.model.Ontology> ontologies = new LinkedList<edu.arizona.biosemantics.bioportal.model.Ontology>();
		try {
			ontologies = bioportalClient.getOntologies().get();
		} catch(ExecutionException | InterruptedException e) {
			log(LogLevel.ERROR, "Exception", e);
		}
		return createOntologies(ontologies);
	}

	private Set<Ontology> createOntologies(List<edu.arizona.biosemantics.bioportal.model.Ontology> ontologies) {
		Set<Ontology> result = new LinkedHashSet<Ontology>();
		Map<String, Ontology> map = new HashMap<String, Ontology>();
		for(edu.arizona.biosemantics.bioportal.model.Ontology ontology : ontologies) {
			Ontology newOntology = new Ontology(ontology.getId(), ontology.getAcronym(), ontology.getName());
			result.add(new Ontology(ontology.getId(), ontology.getAcronym(), ontology.getName()));
			map.put(ontology.getId(), newOntology);
		}

		this.lastRetrievedOntologiesMap = map;
		this.lastRetrievedOntologies = result;
		return result;
	}

}
