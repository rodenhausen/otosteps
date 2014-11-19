package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.blue.client.panel.BlueHeaderFramedAppearance.BlueFramedHeaderResources;
import com.sencha.gxt.theme.blue.client.panel.BlueHeaderFramedAppearance.BlueHeaderFramedStyle;

  public interface OtoHeaderResources extends BlueFramedHeaderResources {

    @Source({"com/sencha/gxt/theme/base/client/widget/Header.css", "OtoHeader.css"})
    BlueHeaderFramedStyle style();
    
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource headerBackground();
  }