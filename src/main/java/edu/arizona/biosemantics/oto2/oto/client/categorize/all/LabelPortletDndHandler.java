package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent.DndDragLeaveHandler;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent.DndDragMoveHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;

import edu.arizona.biosemantics.oto2.oto.client.common.CopyMoveMenu;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.MainTermSynonymsLabelDnd;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.TermDnd;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.uncategorize.TermsView;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;

public class LabelPortletDndHandler implements DndDragEnterHandler, DndDragLeaveHandler, DndDropHandler {

	private EventBus eventBus;
	private LabelPortlet labelPortlet;
	private boolean portletExpandedOnEnter = false;
	private boolean dragInsidePortletOngoing = false;

	public LabelPortletDndHandler(EventBus eventBus, LabelPortlet labelPortlet) {
		this.eventBus = eventBus;
		this.labelPortlet = labelPortlet;
	}
	
	@Override
	public void onDrop(DndDropEvent event) {
		if(event.getDropTarget() instanceof TreeDropTarget) {
			doTreeDrop(event);
		} else {
			doPortletDrop(event);
		}
		restoreExpansionState();
	}

	private void doPortletDrop(DndDropEvent event) {
		Object data = event.getData();
		if(data instanceof TermDnd) {
			TermDnd termDnd = (TermDnd)data;
			if(termDnd.getSource().getClass().equals(TermsView.class)) {
				List<Term> terms = termDnd.getTerms();
				eventBus.fireEvent(new TermCategorizeEvent(terms, labelPortlet.getLabel()));
				//labelPortlet.expand();
			}
			if(termDnd.getSource().getClass().equals(LabelPortlet.class) && termDnd instanceof MainTermSynonymsLabelDnd) {
				final MainTermSynonymsLabelDnd mainTermSynonymsLabelDnd = (MainTermSynonymsLabelDnd)termDnd;
				final Label sourceLabel = mainTermSynonymsLabelDnd.getLabels().get(0);
				if(!sourceLabel.equals(labelPortlet.getLabel())) {
					Menu menu = new CopyMoveMenu(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {								
							eventBus.fireEvent(new CategorizeCopyTermEvent(mainTermSynonymsLabelDnd.getSelectedTerms(), 
									sourceLabel, labelPortlet.getLabel()));								
						}
					}, new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new CategorizeMoveTermEvent(mainTermSynonymsLabelDnd.getSelectedTerms(), 
									sourceLabel, labelPortlet.getLabel()));
						}
					});
					menu.show(labelPortlet);
					//labelPortlet.expand();
				} else {
					if(!mainTermSynonymsLabelDnd.getSelectedTerms().getAdditionalTerms().isEmpty()) {
						for(Term term : mainTermSynonymsLabelDnd.getSelectedTerms().getAdditionalTerms()) {
							if(labelPortlet.getLabel().isSynonym(term))
								eventBus.fireEvent(new SynonymRemovalEvent(labelPortlet.getLabel(), 
										labelPortlet.getLabel().getMainTermOfSynonym(term), term));	
						}
					}
				}
			}
		}
	}

	private void doTreeDrop(DndDropEvent event) {
		Object data = event.getData();
		if (data instanceof TermDnd) {
			TermDnd termDnd = (TermDnd) data;

			if (event.getDropTarget() instanceof StoreTargetTreeDropTarget) {
				StoreTargetTreeDropTarget<TermTreeNode> treeDropTarget = (StoreTargetTreeDropTarget<TermTreeNode>)event.getDropTarget();
				TermTreeNode target = treeDropTarget.getAndNullTarget();
				if (target != null) {
					final Term mainLabelTerm = target.getTerm();

					if (termDnd.getSource().getClass().equals(TermsView.class)) {
						eventBus.fireEvent(new TermCategorizeEvent(termDnd
								.getTerms(), labelPortlet.getLabel()));
						eventBus.fireEvent(new SynonymCreationEvent(labelPortlet.getLabel(),
								mainLabelTerm, termDnd.getTerms()));
					}
					if (termDnd.getSource().getClass()
							.equals(LabelPortlet.class)
							&& termDnd instanceof MainTermSynonymsLabelDnd) {
						final MainTermSynonymsLabelDnd mainTermSynonymsLabelDnd = (MainTermSynonymsLabelDnd) termDnd;
						final Label sourceLabel = mainTermSynonymsLabelDnd
								.getLabels().get(0);
						if (!sourceLabel.equals(labelPortlet.getLabel())) {
							Menu menu = new CopyMoveMenu(
									new SelectionHandler<Item>() {
										@Override
										public void onSelection(
												SelectionEvent<Item> event) {
											eventBus.fireEvent(new CategorizeCopyTermEvent(
													mainTermSynonymsLabelDnd
															.getSelectedTerms(),
													sourceLabel, labelPortlet.getLabel()));
											eventBus.fireEvent(new SynonymCreationEvent(
													labelPortlet.getLabel(), mainLabelTerm,
													mainTermSynonymsLabelDnd
															.getTerms()));
										}
									}, new SelectionHandler<Item>() {
										@Override
										public void onSelection(
												SelectionEvent<Item> event) {
											eventBus.fireEvent(new CategorizeMoveTermEvent(
													mainTermSynonymsLabelDnd
															.getSelectedTerms(),
													sourceLabel, labelPortlet.getLabel()));
											eventBus.fireEvent(new SynonymCreationEvent(
													labelPortlet.getLabel(), mainLabelTerm,
													mainTermSynonymsLabelDnd
															.getTerms()));
										}
									});
							menu.show(labelPortlet);
							//labelPortlet.expand();
						} else {
							eventBus.fireEvent(new SynonymCreationEvent(labelPortlet.getLabel(),
									mainLabelTerm, mainTermSynonymsLabelDnd
											.getTerms()));
						}
					}
				}
			}
		}
	}

	private void restoreExpansionState() {
		if(!portletExpandedOnEnter) {
			labelPortlet.collapse();
			//labelPortlet.collapseSynonyms();
		}
	}

	@Override
	public void onDragLeave(DndDragLeaveEvent event) {
		//would fire drag leave events from portlet, even though the user hasn't really left the portlet, but instead moved slowly
		//to the tree area
		
		//System.out.println("drag leave event");
		//System.out.println(event.getDragEnterEvent().getNativeEvent().getClientX());
		//System.out.println(event.getDragEnterEvent().getNativeEvent().getClientY());
		//System.out.println(event.getDragEnterEvent().getX());
		//System.out.println(event.getDragEnterEvent().getY());
		//System.out.println(labelPortlet.getElement().getBounds());
		//System.out.println(labelPortlet.getBody().getBounds());
		
		if(labelPortlet.getElement().getBounds().contains(event.getDragEnterEvent().getX(), event.getDragEnterEvent().getY())) {
			//System.out.println("didn't really leave");
		} else {
			restoreExpansionState();
			dragInsidePortletOngoing = false;
		}
	}

	@Override
	public void onDragEnter(DndDragEnterEvent event) {
		if(!dragInsidePortletOngoing)
			portletExpandedOnEnter = labelPortlet.isExpanded();
		if(!portletExpandedOnEnter) {
			labelPortlet.expand();	
			//labelPortlet.expandSynonyms();
		}
			
		dragInsidePortletOngoing = true; 
		//System.out.println("drag enter: " + dragInsidePortletOngoing + " " +  portletExpandedOnEnter);
	}

}
