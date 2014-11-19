package edu.arizona.biosemantics.oto2.oto.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.XTemplate;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class LearnTermCategorization extends SimpleContainer {

	public interface CodeSnippetHtml extends XTemplates {
		@XTemplate(source = "LearnTermCategorization.html")
		SafeHtml getTemplate();
	}

	public LearnTermCategorization() {
		CodeSnippetHtml html = GWT.create(CodeSnippetHtml.class);
		HtmlLayoutContainer c = new HtmlLayoutContainer(html.getTemplate());
		FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
		flowLayoutContainer.setScrollMode(ScrollMode.AUTO);
		flowLayoutContainer.add(c);
		this.add(flowLayoutContainer);
	}

}
