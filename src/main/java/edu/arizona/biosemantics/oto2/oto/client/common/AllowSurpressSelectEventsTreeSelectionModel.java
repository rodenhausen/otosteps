package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.List;

import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

public class AllowSurpressSelectEventsTreeSelectionModel<M> extends
		TreeSelectionModel<M> {
	public void setSelection(List<M> selection, boolean surpressEvents) {
		doSelect(selection, false, surpressEvents);
	}
}