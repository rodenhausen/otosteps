package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class StoreTargetTreeDropTarget<M> extends
		edu.arizona.biosemantics.oto2.oto.client.categorize.all.TreeDropTarget<M> {

	public StoreTargetTreeDropTarget(Tree<M, ?> tree) {
		super(tree);
	}

	@Override
	protected void onDragDrop(DndDropEvent event) {
		if (activeItem != null && status == -1) {
			clearStyle(activeItem);
			if (event.getData() != null) {
			}
		} else if (activeItem != null && status != -1) {
			if (event.getData() != null) {
			}
		} else if (activeItem == null && status == -1) {
			if (event.getData() != null) {
			}
		} else {
			// event.setCancelled(true);
		}
		
		/*getWidget().setTrackMouseOver(restoreTrackMouse);
		status = -1;
		activeItem = null;
		appendItem = null;
		*/

		if (autoScroll) {
			scrollSupport.stop();
		}
	}
	
	public M getAndNullTarget() {
		if(activeItem != null) {
			M result = activeItem.getModel();
			
			getWidget().setTrackMouseOver(restoreTrackMouse);
			status = -1;
			activeItem = null;
			appendItem = null;

			return result;
		}
		return null;
	}

}
