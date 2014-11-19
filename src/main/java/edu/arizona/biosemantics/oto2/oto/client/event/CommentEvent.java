package edu.arizona.biosemantics.oto2.oto.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.CommentEvent.CommentHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Comment;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class CommentEvent extends GwtEvent<CommentHandler> {

	public interface CommentHandler extends EventHandler {
		void onComment(CommentEvent event);
	}
	
    public static Type<CommentHandler> TYPE = new Type<CommentHandler>();
	private Comment comment;
	private List<Term> terms = new LinkedList<Term>();
	
    public CommentEvent(Term term, Comment comment) {
    	this.terms.add(term);
    	this.comment = comment;
    }

    public CommentEvent(List<Term> terms, Comment comment) {
    	this.terms = terms;
    	this.comment = comment;
    }
    
	@Override
	public Type<CommentHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CommentHandler handler) {
		handler.onComment(this);
	}

	public Comment getComment() {
		return comment;
	}

	public List<Term> getTerms() {
		return terms;
	}

}
