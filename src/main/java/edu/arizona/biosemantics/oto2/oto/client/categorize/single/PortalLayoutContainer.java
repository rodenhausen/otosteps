package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.DragEndEvent;
import com.sencha.gxt.fx.client.DragHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer.PortalLayoutAppearance;
import com.sencha.gxt.widget.core.client.event.PortalDropEvent;
import com.sencha.gxt.widget.core.client.event.PortalDropEvent.HasPortalDropHandlers;
import com.sencha.gxt.widget.core.client.event.PortalDropEvent.PortalDropHandler;
import com.sencha.gxt.widget.core.client.event.PortalValidateDropEvent;
import com.sencha.gxt.widget.core.client.event.PortalValidateDropEvent.HasPortalValidateDropHandlers;
import com.sencha.gxt.widget.core.client.event.PortalValidateDropEvent.PortalValidateDropHandler;

/**
 * A layout container that lays out its children in in multiple columns, each
 * containing zero or more {@link Portlet}s. Supports drag and drop of child
 * <code>Portlets</code> between columns as well as modifying the order within
 * columns.
 * 
 * <p />
 * PortalLayoutContainer is not a container itself, but a Composite that wraps
 * an internal container.
 * 
 * <p />
 * PortalLayoutContainer internally creates a
 * <code>CssFloatLayoutContainer</code> for each column. The add, insert, remove
 * methods work against these internal containers, {@link #getWidget(int)}
 * returns the internal containers. The portlets are children of the internal
 * containers, not the portlet container itself.
 * 
 * <p/>
 * Code Snippet:
 * 
 * <pre>
  public void onModuleLoad() {
    PortalLayoutContainer c = new PortalLayoutContainer(3);
    c.add(createPortlet("Portlet 1", "Content 1"), 0);
    c.add(createPortlet("Portlet 2", "Content 2"), 1);
    c.add(createPortlet("Portlet 3", "Content 3"), 2);
    Viewport v = new Viewport();
    v.add(c);
    RootPanel.get().add(v);
  }
  
  private Portlet createPortlet(String h, String l) {
    Portlet p = new Portlet();
    p.setHeadingText(h);
    p.setWidget(new Label(l));
    p.setPixelSize(200, 200);
    return p;
  }
 * </pre>
 */
public class PortalLayoutContainer extends Composite implements HasPortalValidateDropHandlers, HasPortalDropHandlers {

  private int numColumns;
  protected boolean autoScroll = true;
  private int spacing = 10;
  protected Portlet active;
  protected int insertCol = -1;
  protected int insertRow = -1;
  protected Widget dropOnWidget;
  protected boolean dropOnWidgetExpandedOnEnter = false;
  protected int startCol;
  protected int startRow;
  private List<Integer> startColumns;
  protected XElement dummy;
  private AutoScrollSupport scrollSupport;

  private Map<String, HandlerRegistration> handlerRegistrations = new HashMap<String, HandlerRegistration>();
  private DragHandler handler;
  private final PortalLayoutAppearance appearance;

  private CssFloatLayoutContainer container;
  protected XElement dragProxyEl;


  /**
   * Creates a portal layout container with the default appearance and the
   * specified number of columns.
   * 
   * @param numColumns the number of columns managed by this portal
   */
  @UiConstructor
  public PortalLayoutContainer(int numColumns) {
    this(GWT.<PortalLayoutAppearance> create(PortalLayoutAppearance.class), numColumns);
  }

  /**
   * Creates a portal layout container with the specified appearance and number
   * of columns.
   * 
   * @param appearance the portal layout appearance
   * @param numColumns the number of columns managed by this portal
   */
  public PortalLayoutContainer(PortalLayoutAppearance appearance, int numColumns) {
    this.appearance = appearance;
    this.numColumns = numColumns;

    handler = createHandler();

    container = new CssFloatLayoutContainer();
    container.setAdjustForScroll(true);

    initWidget(container);

    getElement().getStyle().setOverflow(Overflow.AUTO);

    for (int i = 0; i < numColumns; i++) {
      CssFloatLayoutContainer con = new CssFloatLayoutContainer();
      con.getElement().getStyle().setProperty("padding", spacing + "px 0 0 " + spacing + "px");
      container.insert(con, container.getWidgetCount());
    }
  }

  /**
   * Adds a portlet to the portal.
   * 
   * @param w the portlet
   * @param column the column to add to
   */
  public void add(IsWidget w, int column) {
    insert((Portlet) asWidgetOrNull(w), getWidget(column).getWidgetCount(), column);
  }

  /**
   * Adds a portlet to the portal.
   * 
   * @param portlet the portlet to add
   * @param column the column to add to
   */
  @UiChild(tagname = "portlet")
  public void add(Portlet portlet, int column) {
    insert(portlet, getWidget(column).getWidgetCount(), column);
  }

  @Override
  public HandlerRegistration addDropHandler(PortalDropHandler handler) {
    return addHandler(handler, PortalDropEvent.getType());
  }

  @Override
  public HandlerRegistration addValidateDropHandler(PortalValidateDropHandler handler) {
    return addHandler(handler, PortalValidateDropEvent.getType());
  }

  /**
   * Removes all portlets from all columns.
   */
  public void clear() {
    for (int i = 0; i < container.getWidgetCount(); i++) {
      getWidget(i).clear();
    }
  }

  public PortalLayoutAppearance getAppearance() {
    return appearance;
  }

  /**
   * Returns the scroll support instance.
   * 
   * @return the scroll support
   */
  public AutoScrollSupport getAutoScrollSupport() {
    if (scrollSupport == null) {
      scrollSupport = new AutoScrollSupport(getElement());
    }
    return scrollSupport;
  }

  /**
   * Returns the number of columns.
   * 
   * @return the number of columns
   */
  public int getColumnCount() {
    return numColumns;
  }

  /**
   * Returns the internal container managed by this class that contains the
   * child CssFloatLayoutContainer's for each column.
   * 
   * @return the container
   */
  public Container getContainer() {
    return container;
  }

  /**
   * Returns the column of the given porlet.
   * 
   * @param portlet the portlet
   * @return the column or -1 if not found
   */
  public int getPortletColumn(Portlet portlet) {
    Widget c = portlet.getParent();
    if (c != null && c instanceof CssFloatLayoutContainer) {
      return container.getWidgetIndex(c);
    }
    return -1;
  }

  /**
   * Returns the index of the column for the given portlet.
   * 
   * @param portlet the portlet
   * @return the index or -1 if not found
   */
  public int getPortletIndex(Portlet portlet) {
    Widget c = portlet.getParent();
    if (c != null && c instanceof CssFloatLayoutContainer) {
      return ((CssFloatLayoutContainer) c).getWidgetIndex(portlet);
    }
    return -1;
  }

  /**
   * Returns the spacing between portlets.
   * 
   * @return the spacing the spacing in pixels
   */
  public int getSpacing() {
    return spacing;
  }

  /**
   * Inserts a portlet.
   * 
   * @param w the portlet to added
   * @param index the insert index
   * @param column the column to insert into
   */
  public void insert(IsWidget w, int index, int column) {
    insert((Portlet) asWidgetOrNull(w), index, column);
  }

  /**
   * Inserts a portlet.
   * 
   * @param portlet the portlet to add
   * @param index the insert index
   * @param column the column to insert into
   */
  public void insert(Portlet portlet, int index, int column) {
    Draggable d = portlet.getData("gxt.draggable");
    if (d == null) {
      d = new Draggable(portlet, portlet.getHeader()) {
    	  @Override
    	  protected void startDrag(Event event) {
    		  super.startDrag(event);
    		  PortalLayoutContainer.this.dragProxyEl = this.proxyEl;
    	  }
      };
      d.setConstrainClient(false);
      portlet.setData("gxt.draggable", d);
    }
    d.setUseProxy(true);

    HandlerRegistration reg = handlerRegistrations.get(portlet.getId());
    if (reg != null) {
      reg.removeHandler();
    }
    d.addDragHandler(handler);

    d.setMoveAfterProxyDrag(false);
    d.setSizeProxyToSource(true);
    d.setEnabled(!portlet.isPinned());


    getWidget(column).insert(portlet, index, new CssFloatData(1, new Margins(0, 0, 10, 0)));
    getWidget(column).forceLayout();
  }

  /**
   * Returns true if auto scroll is enabled (defaults to true).
   * 
   * @return true if auto scroll enabled
   */
  public boolean isAutoScroll() {
    return autoScroll;
  }

  /**
   * Removes a portlet from the portal.
   * 
   * @param w the width
   * @param column the column
   */
  public void remove(IsWidget w, int column) {
    remove((Portlet) asWidgetOrNull(w), column);
  }

  /**
   * Removes a portlet from the portal.
   * 
   * @param portlet the portlet to remove
   * @param column the column
   */
  public void remove(Portlet portlet, int column) {
    Draggable d = portlet.getData("gxt.draggable");
    if (d != null) {
      d.release();
    }
    portlet.setData("gxt.draggable", null);

    getWidget(column).remove(portlet);
  }

  /**
   * True to automatically scroll the portal container when the user hovers over
   * the top and bottom of the container (defaults to true).
   * 
   * @see AutoScrollSupport
   * 
   * @param autoScroll true to enable auto scroll
   */
  public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
  }

  /**
   * Sets the column's width.
   * 
   * @param colIndex the column index
   * @param width the column width
   */
  public void setColumnWidth(int colIndex, double width) {
    CssFloatData layoutData = (CssFloatData) getWidget(colIndex).getLayoutData();
    if (layoutData == null) {
      layoutData = new CssFloatData();
      getWidget(colIndex).setLayoutData(layoutData);
    }
    layoutData.setSize(width);
  }

  /**
   * Sets the spacing between portlets (defaults to 10).
   * 
   * @param spacing the spacing in pixels
   */
  public void setSpacing(int spacing) {
    this.spacing = spacing;
    for (int i = 0; i < container.getWidgetCount(); i++) {
      CssFloatLayoutContainer con = getWidget(i);
      con.getElement().getStyle().setProperty("padding", spacing + "px 0 0 " + spacing + "px");
    }
  }

  protected DragHandler createHandler() {
    return new DragHandler() {

      @Override
      public void onDragCancel(DragCancelEvent event) {
        onPortletDragCancel(event);
      }

      @Override
      public void onDragEnd(DragEndEvent event) {
        onPortletDragEnd(event);
      }

      @Override
      public void onDragMove(DragMoveEvent event) {
        onPortletDragMove(event);
      }

      @Override
      public void onDragStart(DragStartEvent event) {
        onPortletDragStart(event);
      }
    };

  }

  /**
   * Returns the column container for the given column.
   *
   * @param index the column index
   * @return the column container
   */
  protected CssFloatLayoutContainer getWidget(int index) {
    return (CssFloatLayoutContainer) container.getWidget(index);
  }

  protected void onPortletDragCancel(DragCancelEvent event) {
    active.setVisible(true);
    active = null;
    insertCol = -1;
    insertRow = -1;
    dummy.removeFromParent();
    if (autoScroll) {
      getAutoScrollSupport().stop();
    }
  }

  protected void onPortletDragEnd(DragEndEvent de) {
    dummy.removeFromParent();

    if (insertCol != -1 && insertRow != -1) {
      if (startCol == insertCol && insertRow > startRow) {
        insertRow--;
      }
      active.setVisible(true);
      active.removeFromParent();
      
      //fix bug in 3.1.0 implementation, where one would drag and drop a panel outside of this container. Due to the already removed "active" from the panel
      //the size is smaller than calculated by insertRow. Can simply be reproduced with a single cell in a column and dragging and dropping it out of this container.
      if(insertRow < 0 || insertRow > getWidget(insertCol).getWidgetCount()) {
    	  insertRow = 0;
      }
      //end fix
      
      getWidget(insertCol).insert(active, insertRow);
      getWidget(insertCol).forceLayout();

      fireEvent(new PortalDropEvent(active, startCol, startRow, insertCol, insertRow));
    }
    active.setVisible(true);
    active = null;
    insertCol = -1;
    insertRow = -1;
    if (autoScroll) {
      getAutoScrollSupport().stop();
    }
  }

  //this method is never used, in general DragLeaveEvents seem to be depreceated
  //maybe due to incompatibilities between browsers or other issues like leaks?
  protected void onPortletDragLeave(DragLeaveEvent de) {
    if (autoScroll) {
      getAutoScrollSupport().stop();
    }
  }

  protected void onPortletDragMove(DragMoveEvent de) {
	  
	  //remove proxy drag icon to make drop outside of this panel work correctly
	  //mouse hovering over the proxy icon would return wrong "target" to drop on
	  //and make drag drop fail
	if(outsideOfThis(de)) {
		//Draggable d = active.getData("gxt.draggable");
	   //d.setUseProxy(false);
	    dragProxyEl.setVisibility(false);        
    //coming back round trip
	} else {
		//Draggable d = active.getData("gxt.draggable");
	    dragProxyEl.setVisibility(true);
	}
    int col = getColumn(de.getNativeEvent().getClientX());
    
    int row = getRowPosition(col, de.getNativeEvent().getClientY());
    int adjustRow = row;
    if (startCol == col && row > startRow) {
      adjustRow--;
    }
    if (col != insertCol || row != insertRow) {
      if (fireCancellableEvent(new PortalValidateDropEvent(active, startCol, startRow, col, adjustRow))) {
        //if(dropOnWidget == null) {
     	//  System.out.println("add insert");
        	addInsert(col, row);
        //} else {
        // 	dummy.removeFromParent();
        //}
      } else {
        insertCol = startCol;
        insertRow = startRow;
        //if(dropOnWidget != null) {
        //	dummy.removeFromParent();
        //}
      }
    //} else {
    //	 if(dropOnWidget != null) {
    //    	dummy.removeFromParent();
    //    }
    }
  }

  private boolean outsideOfThis(DragMoveEvent de) {
	  int mouseX = de.getNativeEvent().getClientX();
	  int mouseY = de.getNativeEvent().getClientY();
	  if(mouseX < this.getAbsoluteLeft() || 
			  mouseY <  this.getAbsoluteTop() || 
			  mouseX > this.getAbsoluteLeft() + this.getOffsetWidth() || 
			  mouseY > this.getAbsoluteTop() + this.getOffsetHeight()) {
		  return true;
	  }
	return false;
}

protected void onPortletDragStart(DragStartEvent de) {
    active = (Portlet) de.getTarget();

    if (dummy == null) {
      SafeHtmlBuilder sb = new SafeHtmlBuilder();
      appearance.renderInsert(sb);
      dummy = XDOM.create(sb.toSafeHtml()).cast();
    }

    dummy.getStyle().setProperty("padding", active.getElement().getStyle().getPadding());

    int h = active.getElement().getOffsetHeight() - active.getElement().getFrameWidth(Side.TOP, Side.BOTTOM);
    dummy.getFirstChildElement().<XElement> cast().setHeight(h);

    startColumns = new ArrayList<Integer>();
    for (int i = 0; i < numColumns; i++) {
      CssFloatLayoutContainer con = getWidget(i);
      int x = con.getAbsoluteLeft();
      startColumns.add(x);
    }
    startCol = getColumn(de.getX());
    startRow = getRow(startCol, de.getY());
    active.setVisible(false);
    addInsert(startCol, startRow);

    if (autoScroll) {
      getAutoScrollSupport().start();
    }
    
    //Draggable d = active.getData("gxt.draggable");
    //d.setUseProxy(false);
  }

  private void addInsert(int col, int row) {
    insertCol = col;
    insertRow = row;

    CssFloatLayoutContainer lc = getWidget(insertCol);
    dummy.getStyle().setFloat(lc.getStyleFloat()); // use float style of column

    dummy.removeFromParent();
    lc.getElement().getFirstChildElement().<XElement>cast().insertChild(dummy, row);
  }

  private int getColumn(int x) {
    x += XDOM.getBodyScrollLeft();
    for (int i = startColumns.size() - 1; i >= 0; i--) {
      if (x > startColumns.get(i)) {
        return i;
      }
    }
    return 0;
  }

  private int getRow(int col, int y) {
    y += XDOM.getBodyScrollTop();
    CssFloatLayoutContainer con = getWidget(col);
    int count = con.getWidgetCount();

    for (int i = 0; i < count; i++) {
      Widget c = con.getWidget(i);
      int b = c.getAbsoluteTop();
      int t = b + c.getOffsetHeight();

      if (y < t) {
        return i;
      }
    }

    return 0;
  }

  private int getRowPosition(int col, int y) {
	 // System.out.println("start get row position "+ y);
    y += XDOM.getBodyScrollTop();
    CssFloatLayoutContainer con = getWidget(col);
    List<Widget> list = new ArrayList<Widget>();
    for (int i = 0; i < con.getWidgetCount(); i++) {
      list.add(con.getWidget(i));
    }
    int count = list.size();

    for (int i = 0; i < count; i++) {
      Widget c = list.get(i);

      int b = c.getAbsoluteTop();
      int t = b + c.getOffsetHeight();
      int m = b + (c.getOffsetHeight() / 2);
      //System.out.println("absolute top (b) " + b);
      //System.out.println("offset height (t) " + t);
      //System.out.println("(m) " + m);
      if(y < t && y > b) {
    	  //System.out.println("drop on widget");
    	  boolean onGoingDropOnWidget = false;
    	  if(dropOnWidget != null && dropOnWidget.equals(c))
    		  onGoingDropOnWidget = true;
    	  //System.out.println("onGoingDropOnWidget: " + onGoingDropOnWidget);
    	  dropOnWidget = c;
    	  //System.out.println("dropOnWidget: " + dropOnWidget);
    	  
    	  if(dropOnWidget != null && dropOnWidget instanceof MainTermPortlet) {
    		  MainTermPortlet mainTermPortlet = (MainTermPortlet)dropOnWidget;
    		  //System.out.println("mainTermPortlet: " + mainTermPortlet.getLabel().getName() + " " + mainTermPortlet.getMainTerm().getTerm());
    		  if(!onGoingDropOnWidget)
    			  dropOnWidgetExpandedOnEnter = mainTermPortlet.isExpanded();
    		  mainTermPortlet.expand();
    		  //System.out.println("dropOnWidgetExpandedOnEnter: " + dropOnWidgetExpandedOnEnter);
    	  }
      } else {
    	  //System.out.println("not drop on widget");
    	  if(dropOnWidget != null && dropOnWidget instanceof MainTermPortlet) {
    		  MainTermPortlet mainTermPortlet = (MainTermPortlet)dropOnWidget;
    		  //System.out.println("mainTermPortlet: " + mainTermPortlet.getLabel().getName() + " " + mainTermPortlet.getMainTerm().getTerm());
    		  //System.out.println("dropOnWidgetExpandedOnEnter: " + dropOnWidgetExpandedOnEnter);
    		  if(!dropOnWidgetExpandedOnEnter)
    			  mainTermPortlet.collapse();
    	  }
    	  dropOnWidget = null;
    	  
      }
      
      if (y < t) {
        if (y < m) {
          return i;
        } else {
          return i + 1;
        }
      }
    }
    //System.out.println("end get row pos");
    return count;
  }
}
