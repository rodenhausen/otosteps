package edu.arizona.biosemantics.oto2.oto.client.info;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.state.client.GridStateHandler;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Location;
import edu.arizona.biosemantics.oto2.oto.shared.model.LocationProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;

public class LocationsView extends Composite {

	private static final LocationProperties locationProperties = GWT
			.create(LocationProperties.class);
	private ListStore<Location> store = new ListStore<Location>(locationProperties.key());
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private EventBus eventBus;
	private Grid<Location> grid;
	protected Term currentTerm;
	private AutoProgressMessageBox searchingBox;

	public LocationsView(EventBus eventBus) {
		this.eventBus = eventBus;
		store.setAutoCommit(true);
		RowNumberer<Location> numberer = new RowNumberer<Location>();
		ColumnConfig<Location, String> instanceColumn = new ColumnConfig<Location, String>(locationProperties.instance(), 50,SafeHtmlUtils.fromTrustedString("<b>Instance</b>"));
		ColumnConfig<Location, Label> categorizationColumn = new ColumnConfig<Location, Label>(locationProperties.categorization(), 100, SafeHtmlUtils.fromTrustedString("<b>Categorization</b>"));
		categorizationColumn.setCell(new AbstractCell<Label>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	Label value, SafeHtmlBuilder sb) {
				if(value != null)
					sb.appendHtmlConstant("<span qtitle='Description of " + value.getName() + "' " +
							"qtip='" + value.getDescription() + "'>" + value.getName() + "</span>");
				else
					sb.appendHtmlConstant("<span qtitle='Uncategorized' " +
							"qtip='No category assigned to this term.'>uncategorized</span>");
			}
		});
		instanceColumn.setToolTip(SafeHtmlUtils.fromTrustedString("The selected terml"));
		categorizationColumn.setToolTip(SafeHtmlUtils.fromTrustedString("The current categorization of the term"));
		categorizationColumn.setMenuDisabled(false);
		instanceColumn.setMenuDisabled(false);
		List<ColumnConfig<Location, ?>> columns = new ArrayList<ColumnConfig<Location, ?>>();
		columns.add(numberer);
		columns.add(instanceColumn);
		columns.add(categorizationColumn);
		ColumnModel<Location> columnModel = new ColumnModel<Location>(columns);
		grid = new Grid<Location>(store, columnModel);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		QuickTip quickTip = new QuickTip(grid);
		//instanceColumn.setWidth(200);
		grid.getView().setAutoExpandColumn(categorizationColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setForceFit(true);
		grid.setBorders(false);
		grid.setAllowTextSelection(true);
		grid.setColumnReordering(true);
		/*grid.setStateful(true);
		grid.setStateId("locationsGrid");
		GridStateHandler<Location> state = new GridStateHandler<Location>(grid);
		state.loadState();*/
		
		StringFilter<Location> instanceFilter = new StringFilter<Location>(locationProperties.instance());
		StringFilter<Location> categorizationFilter = new StringFilter<Location>(new ValueProvider<Location, String>() {
			@Override
			public String getValue(Location object) {
				return object.getCategorization().getName();
			}
			@Override
			public void setValue(Location object, String value) {
				object.getCategorization().setName(value);
			}
			@Override
			public String getPath() {
				// has to return same path as valueprovider used in columnmodel for filter to work; ideally use same valueprovider 
				// but not possible here
				return "categorization";
			}
		});
		GridFilters<Location> filters = new GridFilters<Location>();
	    filters.setLocal(true);
	    filters.addFilter(instanceFilter);
	    filters.addFilter(categorizationFilter);
	    filters.initPlugin(grid);
	    numberer.initPlugin(grid);
		
		this.initWidget(grid);
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				currentTerm = event.getTerm();
				refresh();
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				if(event.getTerms().contains(currentTerm)) {
					store.clear();
					for(Label label: event.getLabels())
						store.add(new Location(currentTerm.getTerm(), label));
				}
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				if(event.getTerms().contains(currentTerm)) {
					store.clear();
				}
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(CategorizeCopyRemoveTermEvent event) {
				if(event.getTerms().contains(currentTerm)) {
					for(Label label : event.getLabels()) {
						Location location = store.findModelWithKey(label.toString());
						if(location != null) 
							store.remove(location);
					}
				}
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(CategorizeCopyTermEvent event) {
				if(event.getSelectedTerms().getTerms().contains(currentTerm)) {
					for(Label label: event.getTargetCategories()) {
						Location toAdd = new Location(currentTerm.getTerm(), label);
						if(store.findModel(toAdd) == null)
							store.add(toAdd);
					}
				}
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(CategorizeMoveTermEvent event) {
				if(event.getSelectedTerms().getTerms().contains(currentTerm)) {
					Location location = store.findModelWithKey(event.getSourceCategory().toString());
					if(location != null) 
						store.remove(location);
					Location toAdd = new Location(currentTerm.getTerm(), event.getTargetCategory());
					if(store.findModel(toAdd) == null)
						store.add(toAdd);
				}
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				Location location = store.findModelWithKey(event.getLabel().toString());
				if(location != null) 
					store.remove(location);
			}
		});
		eventBus.addHandler(LabelModifyEvent.TYPE, new LabelModifyEvent.ModifyLabelHandler() {
			@Override
			public void onModify(LabelModifyEvent event) {
				Location location = store.findModelWithKey(event.getLabel().toString());
				if(location != null) 
					store.update(location);
			}
		});
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(LabelsMergeEvent event) {
				if(currentTerm != null) {
					boolean found = false;
					for(Label source : event.getSources()) {
						if(source.containsTerm(currentTerm)) {
							Location location = store.findModelWithKey(source.toString());
							if(location != null) 
								store.remove(location);
							found = true;
						}
					}
					Location newLocation = new Location(currentTerm.getTerm(), event.getDestination());
					if(found && store.findModel(newLocation) == null) 
						store.add(newLocation);
				}
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				if(currentTerm != null && currentTerm.equals(event.getTerm()))
					for(Location location : store.getAll()) {
						location.setInstance(currentTerm.getTerm());
						store.update(location);
					}
			}
		});
		//show would show the box not relative to this widget yet, not ready in 
		//final location yet
		this.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				showSearchingBox();
			}
		});
		this.addShowHandler(new ShowHandler() {
			@Override
			public void onShow(ShowEvent event) {
				showSearchingBox();
			}
		});
	}
	
	protected void refresh() {
		createSearchingBox();
		 if(this.isVisible()) {
        	showSearchingBox();
        }
		collectionService.getLocations(currentTerm, new AsyncCallback<List<Location>>() {
			@Override
			public void onSuccess(List<Location> locations) {
				setLocations(locations);
				destroySearchingBox();
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.getLocationsFailed(caught);
				destroySearchingBox();
			}
		});
	}

	public void setLocations(List<Location> locations) {
		store.clear();
		store.addAll(locations);
		
		//bug: http://www.sencha.com/forum/showthread.php?285982-Grid-ColumnHeader-Menu-missing
		grid.getView().refresh(true);
	}

	private void createSearchingBox() {
		if(searchingBox == null) {
			searchingBox = new AutoProgressMessageBox("Progress", 
					"Searching locations, please wait...");
			searchingBox.setProgressText("Searching...");
			searchingBox.auto();
			searchingBox.setClosable(true); // in case user figures search takes too long / some technical problem
			searchingBox.setModal(false);
		}
	}

	protected void destroySearchingBox() {
		if(searchingBox != null) {
			searchingBox.hide();
			searchingBox = null;
		}
	}

	private void showSearchingBox() {
		if(searchingBox != null) {
			searchingBox.getElement().alignTo(this.getElement(), 
	        		 new AnchorAlignment(Anchor.CENTER, Anchor.CENTER), 0, 0);
			searchingBox.show();
		}
	}
}
