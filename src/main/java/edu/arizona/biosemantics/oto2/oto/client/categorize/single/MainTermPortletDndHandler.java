package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent.DndDragLeaveHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;

import edu.arizona.biosemantics.oto2.oto.client.common.dnd.TermDnd;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.uncategorize.TermsView;

public class MainTermPortletDndHandler implements DndDropHandler { /* DndDragEnterHandler, , DndDragLeaveHandler { */

	private EventBus eventBus;
	private MainTermPortlet mainTermPortlet;
	//private boolean portletExpandedOnEnter = false;
	//private boolean dragInsidePortletOngoing = false;

	public MainTermPortletDndHandler(EventBus eventBus, MainTermPortlet mainTermPortlet) {
		this.eventBus = eventBus;
		this.mainTermPortlet = mainTermPortlet;
	}

	@Override
	public void onDrop(DndDropEvent event) {
		Object data = event.getData();
		if (data instanceof TermDnd) {
			TermDnd termDnd = (TermDnd) data;
			if (termDnd.getSource().getClass().equals(TermsView.class)) {
				eventBus.fireEvent(new TermCategorizeEvent(termDnd.getTerms(),
						mainTermPortlet.getLabel()));
				eventBus.fireEvent(new SynonymCreationEvent(mainTermPortlet.getLabel(), mainTermPortlet.getMainTerm(),
						termDnd.getTerms()));
			}
			if (termDnd.getSource().getClass().equals(MainTermPortlet.class)) {
				eventBus.fireEvent(new SynonymCreationEvent(mainTermPortlet.getLabel(), mainTermPortlet.getMainTerm(),
						termDnd.getTerms()));
			}
		}
		//restoreExpansionState();
	}
	
	/*private void restoreExpansionState() {
		if(!portletExpandedOnEnter) {
			mainTermPortlet.collapse();
		}
	}*/
	
	/*@Override
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
		
		if(mainTermPortlet.getElement().getBounds().contains(event.getDragEnterEvent().getX(), event.getDragEnterEvent().getY())) {
			//System.out.println("didn't really leave");
		} else {
			restoreExpansionState();
			dragInsidePortletOngoing = false;
		}
	}

	@Override
	public void onDragEnter(DndDragEnterEvent event) {
		if(!dragInsidePortletOngoing)
			portletExpandedOnEnter = mainTermPortlet.isExpanded();
		if(!portletExpandedOnEnter) {
			mainTermPortlet.expand();	
		}
			
		dragInsidePortletOngoing = true; 
		//System.out.println("drag enter: " + dragInsidePortletOngoing + " " +  portletExpandedOnEnter);
	} */

}
