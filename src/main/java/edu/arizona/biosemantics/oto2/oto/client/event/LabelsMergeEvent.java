package edu.arizona.biosemantics.oto2.oto.client.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent.MergeLabelsHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class LabelsMergeEvent extends GwtEvent<MergeLabelsHandler> {

	public interface MergeLabelsHandler extends EventHandler {
		void onMerge(LabelsMergeEvent event);
	}
	
    public static Type<MergeLabelsHandler> TYPE = new Type<MergeLabelsHandler>();
    
    private List<Label> sources;
    private Label destination;
    
    public LabelsMergeEvent(Label destination, List<Label> sources) {
        this.sources = sources;
        this.destination = destination;
    }
	
	@Override
	public Type<MergeLabelsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MergeLabelsHandler handler) {
		handler.onMerge(this);
	}

	public List<Label> getSources() {
		return sources;
	}

	public Label getDestination() {
		return destination;
	}
	

}

