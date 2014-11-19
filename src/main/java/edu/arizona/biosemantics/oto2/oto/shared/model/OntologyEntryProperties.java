package edu.arizona.biosemantics.oto2.oto.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface OntologyEntryProperties extends PropertyAccess<OntologyEntry> {
	
  @Path("id")
  ModelKeyProvider<OntologyEntry> key();
   
  @Path("label")
  LabelProvider<OntologyEntry> namelabel();
 
  ValueProvider<OntologyEntry, String> label();
   
  ValueProvider<OntologyEntry, String> definition();
  
  ValueProvider<OntologyEntry, String> url();
  
  ValueProvider<OntologyEntry, String> ontologyAcronym();
  
  ValueProvider<OntologyEntry, String> ontologyName();
}