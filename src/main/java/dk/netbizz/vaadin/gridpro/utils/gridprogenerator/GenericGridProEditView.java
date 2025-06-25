package dk.netbizz.vaadin.gridpro.utils.gridprogenerator;

/*
 * A Vaadin GridPro tool that converts annotations on your POJO entity class into a GridPro grid for updating
 *
 * This is given to the public domain and without any warranty
 *
 * Original idea of using annotations for generating the Vaadin UI by Andreas Lange
 * https://github.com/andrlange/vaadin-grid-form-entities
 *
 * See the README for more information
 *
 * This is a GridPro extension of the idea of using annotations on your POJO to simplify setting up GridPro.
 * Besides the most common field types, there is a special option for editing variable length array fields,
 * which is actually why I started to make this, since it avoids having a lot of repetitive application code.
 *
 * Since a user can move freely around in the grid you can never know when editing is finished, hence there is instant persistence.
 * Every edit of a single cell will cause a call to save the entity of the row.
 *
 * The application domain view must extend this class and replace the generic placeholder with the domain class for the grid.
 *
 * Further improvements should probably be about overall code structure and utilize interfaces more etc.
 *
 * Git Repo
 * https://github.com/MikaelFiil/vaadin-gridpro-generator
 *
 *  * Ideas
 * -----
 *
 *
 * Author mikael.fiil@netbizz.dk
 */

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.richtexteditor.RichTextEditorVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import dk.netbizz.vaadin.gridpro.utils.components.ConfirmationDialog;
import dk.netbizz.vaadin.gridpro.utils.components.GridUtils;
import dk.netbizz.vaadin.gridpro.utils.components.PopoverMessage;
import dk.netbizz.vaadin.gridpro.utils.components.TrafficLight;
import dk.netbizz.vaadin.gridpro.utils.inputcreators.DateTimePickerCreator;
import dk.netbizz.vaadin.gridpro.utils.inputcreators.InputFieldCreator;
import dk.netbizz.vaadin.gridpro.utils.themes.RadioButtonTheme;
import org.springframework.data.annotation.Transient;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.round;


@SuppressWarnings("Unused")
public abstract class GenericGridProEditView<T extends BaseEntity> extends VerticalLayout {

    // Keep compiler & Lint happy
    private static final String HEADER = ".header";
    private static final String ARRAY_END_INDEX = ".arrayEndIdx";
    private static final String COLOR = "color";

    public static final String CURRENCY_FORMAT = "###,###,### ¤";
    public static final String CURRENCY_FORMAT_DECIMALS = "###,###,###.## ¤";
    public static final String THOUSAND_FORMAT = "###,###,###";
    public static final String PERCENT_FORMAT = "###,###,### '%'";
    public static final String PERCENT_FORMAT_DECIMALS_1 = "###,###,###.# '%'";

    protected final GridPro<T> genericGrid;
    private final Class<T> entityClass;
    @Transient
    private T selectedItem;
    private final Button btnAdd = new Button();


    protected GenericGridProEditView(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.genericGrid = new GridPro<>(entityClass);
        this.genericGrid.setSingleCellEdit(false);      // Default
        this.genericGrid.setEditOnClick(true);
        genericGrid.setEmptyStateText("No rows found.");

        setupLayout(); // Further setup initialization done via subclass
    }

    private void setupLayout() {
        setSizeFull();
        add(genericGrid);
    }

    // Abstract methods to be implemented by specific domain views
    protected abstract void setValidationError(T entity, String columName, String msg);             // Let domain view handle UI messages
    protected abstract void setSystemError(String className, String columName, Exception e);        // Let domain view handle UI messages
    protected abstract void saveEntity(T entity);
    protected abstract void addNew();
    protected abstract void loadEntities();
    protected abstract void clearEntities();
    protected abstract void deleteEntity(T entity);
    protected abstract void selectEntity(T entity);                                                 // Seldom used, GridPro makes row selection obsolete - I think.
    protected abstract <S> List<S> getItemsForSelect(String colName);
    protected abstract String getFixedCalculatedText(T entity, String colName);
    protected abstract boolean validUpdate(T entity, String colName, Object newColValue);
    protected abstract boolean isEditableEntity(T entity);                                          // Should be based on user ownership
    protected abstract boolean canAddEntity();                                                      // Should be based on user profile rights
    protected abstract boolean canDeleteEntities();                                                 // Should be based on user profile rights

    protected void setupGridEventHandlers() {
        genericGrid.setSelectionMode(Grid.SelectionMode.SINGLE);                                    // Set this to be able to select a row
        GridUtils.setDeselectAllowed(genericGrid,false);

        // A selectionListener is a bit detached in GridPro you can easily have one selected row while editing another row, so we select the row being edited
/*
        genericGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(item -> {
            System.out.println("addSelectionListener in " + entityClass.getSimpleName());
            if (selectedItem == null || !selectedItem.equals(item)) {
                selectEntity(item);
            }
            selectedItem = item;
        }));
*/
        genericGrid.addItemClickListener(evt-> {
            if (selectedItem == null || !selectedItem.equals(evt.getItem())) {
                genericGrid.select(evt.getItem());
                selectEntity(evt.getItem());
            }
            selectedItem = evt.getItem();
        });

        // Also select item when tab to the row for editing
        genericGrid.addCellEditStartedListener(evt -> {
            if (selectedItem == null || !selectedItem.equals(evt.getItem())) {
                genericGrid.select(evt.getItem());
                selectEntity(evt.getItem());
            }
            selectedItem = evt.getItem();
        });

    }

    protected TextField createSearchField(String labelText, HeaderRow.HeaderCell cell) {
        TextField textField = new TextField();
        textField.setPlaceholder("Filter by " + labelText + "...");
        textField.setValueChangeMode(ValueChangeMode.LAZY);
        textField.setClearButtonVisible(true);
        textField.addClassName("search-field");
        textField.setWidthFull();
        textField.addValueChangeListener(evt -> refreshGrid());
        cell.setComponent(textField);
        return textField;
    }

    protected Select<String> createSelectSearchField(String labelText, HeaderRow.HeaderCell cell, List<String> options) {
        Select<String> select = new Select<>();
        select.setPlaceholder("Filter by " + labelText + "...");
        select.setItems(options);
        select.addValueChangeListener(item -> refreshGrid());
        cell.setComponent(select);
        return select;
    }


    // Indirection to ensure that we deselect all before reloading
    protected void refreshGrid() {
        selectedItem = null;
        genericGrid.deselectAll();
        loadEntities();
    }

    // Approximate height by rows
    protected void setMaxGridHeight(int rows) {
        genericGrid.getElement().getStyle().set("max-height", "calc(" + rows + " * var(--lumo-size-m)");
    }

    protected void setupGrid(Map<String, String> dynamicParameters) {
        genericGrid.removeAllColumns();
        List<GridColumnInfo> gridColumns = new ArrayList<>();
        addFieldColumns(gridColumns);   // Using side effects - sorry
        addMethodColumns(gridColumns);

        List<Integer> indexesOfAlternatingCols = new ArrayList<>();
        int colIdx = 0;     // Global column index when adding to grid, needed for alternating columns

        // Sort columns by order
        gridColumns = gridColumns.stream()
                .filter(item -> (dynamicParameters.get(item.propertyName+".hidden") == null))       // Some columns may be hidden in a specific view
                .sorted(Comparator.comparingInt(GridColumnInfo::order))
                .toList();

        // and add them to grid
        for (GridColumnInfo columnInfo : gridColumns) {

            String camelName = columnInfo.propertyName().substring(0, 1).toUpperCase() + columnInfo.propertyName().substring(1);

            if (columnInfo.method() != null) {      // For method-based columns
                switch (columnInfo.editorClass.getSimpleName()) {                                 // using switch is future-proof

                    case "ArrayCalculator" -> {         // Array method
                        if (columnInfo.alternatingCol) {
                            indexesOfAlternatingCols.add(colIdx);
                        }    // is it a new alternating column type
                        int lastIdx = (dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX) != null ? Math.min(columnInfo.arrayEndIdx, Integer.parseInt(dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX))) : columnInfo.arrayEndIdx);
                        for (int idx = 0; idx <= lastIdx; idx++) {
                            setStandardColumnProperties(makeArrayCalculatorColumns(columnInfo, idx, camelName), columnInfo, dynamicParameters.get(columnInfo.propertyName + HEADER + idx));
                        }
                    }

                    default ->                                 // Simple Method
                        setStandardColumnProperties(
                            genericGrid.addColumn(item -> {
                                try {
                                    Object value = columnInfo.method().invoke(item);
                                    return formatValue(value, columnInfo);
                                } catch (Exception e) {
                                    setSystemError(item.getClass().getName().getClass().getName(), columnInfo.propertyName(), e);
                                    return null;
                                }
                            })
                            , columnInfo, null);
                }
                colIdx++;
            } else {  // Field based columns
                boolean readonly = "true".equalsIgnoreCase(dynamicParameters.get(columnInfo.propertyName()+".readonly")) || dynamicParameters.get("readonly") != null;      // Is the field or the whole table readonly?

                switch (columnInfo.editorClass.getSimpleName()) {
                    case "TextField"  -> {
                        if (columnInfo.fieldLength <= 15) {
                            setStandardColumnProperties(makeShortTextFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        } else {
                            setStandardColumnProperties(makeStandardTextFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        }
                        colIdx++;
                    }
                    case "TextArea" -> {
                        setStandardColumnProperties(makeStandardTextAreaFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        colIdx++;
                    }
                    case "IntegerField" -> {
                        setStandardColumnProperties(makeIntegerFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        colIdx++;
                    }
                    case "BigDecimalField" -> {
                        setStandardColumnProperties(makeBigDecimalFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        colIdx++;
                    }
                    case "NumberField" -> {
                        setStandardColumnProperties(makeNumericFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        colIdx++;
                    }
                    case "Checkbox" -> {
                        setStandardColumnProperties(makeBooleanFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        colIdx++;
                    }
                    case "Select" -> {
                        setStandardColumnProperties(makeSelectFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        colIdx++;
                    }
                    case "DatePicker" -> {
                        setStandardColumnProperties(makeDatePickerFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        colIdx++;
                    }
                    case "TrafficLight" -> {
                        setStandardColumnProperties(makeTrafficlightFieldColumn(columnInfo, camelName, readonly), columnInfo, null);
                        colIdx++;
                    }
                    case "RichTextEditor" -> {
                        makeRichTextFieldColumn(columnInfo, camelName, readonly);
                        colIdx++;
                    }
                    case "FixedCalculatedText" -> {
                        setStandardColumnProperties(makeFixedCalculatedTextColumn(columnInfo, camelName), columnInfo, null);
                        colIdx++;
                    }
                    case "ArrayIntegerEditor" -> {
                        if (columnInfo.alternatingCol) {                // is it a new alternating column type
                            indexesOfAlternatingCols.add(colIdx);
                        }
                        int lastIdx = (dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX) != null ? Math.min(columnInfo.arrayEndIdx, Integer.parseInt(dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX))) : columnInfo.arrayEndIdx);
                        for (int idx = 0; idx <= lastIdx; idx++) {
                            setStandardColumnPropertiesArray(makeArrayIntegerFieldColumns(columnInfo, idx, camelName, readonly), columnInfo, dynamicParameters.get(columnInfo.propertyName + HEADER + idx), "[" + (idx+1) + "]");     // Database array is 1 based
                            colIdx++;
                        }
                    }
                    case "ArrayBigDecimalEditor" -> {
                        if (columnInfo.alternatingCol) {                // is it a new alternating column type
                            indexesOfAlternatingCols.add(colIdx);
                        }
                        int lastIdx = (dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX) != null ? Math.min(columnInfo.arrayEndIdx, Integer.parseInt(dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX))) : columnInfo.arrayEndIdx);
                        for (int idx = 0; idx <= lastIdx; idx++) {
                            setStandardColumnPropertiesArray(makeArrayBigDecimalFieldColumns(columnInfo, idx, camelName, readonly), columnInfo, dynamicParameters.get(columnInfo.propertyName + HEADER+ idx), "[" + (idx+1) + "]");   // Database array is 1 based
                            colIdx++;
                        }
                    }
                    case "ArrayFloatEditor" -> {
                        if (columnInfo.alternatingCol) {                // is it a new alternating column type
                            indexesOfAlternatingCols.add(colIdx);
                        }
                        int lastIdx = (dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX) != null ? Math.min(columnInfo.arrayEndIdx, Integer.parseInt(dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX))) : columnInfo.arrayEndIdx);
                        for (int idx = 0; idx <= lastIdx; idx++) {
                            setStandardColumnPropertiesArray(makeArrayFloatFieldColumns(columnInfo, idx, camelName, readonly), columnInfo, dynamicParameters.get(columnInfo.propertyName + HEADER + idx), "[" + (idx+1) + "]");       // Database array is 1 based
                            colIdx++;
                        }
                    }
                    case "ArrayDoubleEditor" -> {
                        if (columnInfo.alternatingCol) {                // is it a new alternating column type
                            indexesOfAlternatingCols.add(colIdx);
                        }
                        int lastIdx = (dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX) != null ? Math.min(columnInfo.arrayEndIdx, Integer.parseInt(dynamicParameters.get(columnInfo.propertyName + ARRAY_END_INDEX))) : columnInfo.arrayEndIdx);
                        for (int idx = 0; idx <= lastIdx; idx++) {
                            setStandardColumnPropertiesArray(makeArrayDoubleFieldColumns(columnInfo, idx, camelName, readonly), columnInfo, dynamicParameters.get(columnInfo.propertyName + HEADER + idx), "[" + (idx+1) + "]");      // Database array is 1 based
                            colIdx++;
                        }
                    }
                    default ->
                        throw new IllegalStateException("Unexpected value: " + columnInfo.editorClass);
                }   // switch
            }
        }

        // Do we have alternating columns, then we need to reorder
        if (!indexesOfAlternatingCols.isEmpty()) {
            List<Grid.Column<T>> existingColumns = genericGrid.getColumns();
            // First add columns before the alternating arrays
            List<Grid.Column<T>> nextColumns = new ArrayList<>(existingColumns.subList(0, indexesOfAlternatingCols.get(0)));
            int alternatingSpace = indexesOfAlternatingCols.get(1) - indexesOfAlternatingCols.get(0);

            for (int altIdx = 0; altIdx < alternatingSpace; altIdx++) {
                for (colIdx = 0; colIdx < indexesOfAlternatingCols.size(); colIdx++) {
                    nextColumns.add(existingColumns.get(indexesOfAlternatingCols.get(colIdx)+altIdx));
                }
            }
            genericGrid.setColumnOrder(nextColumns);
        }

        // Add delete column icon if not read only of entire table
        if (dynamicParameters.get("readonly") == null && canDeleteEntities()) {
            genericGrid.addColumn(new IconRenderer<>(item -> {
                Button btnRemove = new Button();
                btnRemove.setClassName("icon-trash");
                Icon iconTrashLock;
                if (isEditableEntity(item)) {
                    iconTrashLock = new Icon(VaadinIcon.TRASH);
                    PopoverMessage.addPopover("Delete row", btnRemove);
                    btnRemove.addClickListener(elem -> {            // No DB changes yet, only when updating the BidRequest as a whole
                        btnRemove.getStyle().set(COLOR, "red");
                        ConfirmationDialog.confirm("Warning", "You are deleting the row, continue?", () -> btnRemove.getStyle().remove(COLOR))
                            .addConfirmListener(event -> {
                                deleteEntity(item);
                                refreshGrid();
                            });
                    });
                } else {
                    iconTrashLock = new Icon(VaadinIcon.LOCK);
                    PopoverMessage.addPopover("Row is read only", btnRemove);
                }
                btnRemove.setIcon(iconTrashLock);
                return btnRemove;
                }, item -> ""))
            .setHeader(getAddRowHeader())
            .setAutoWidth(true)
            .setFlexGrow(0)
            .setResizable(false)
            .setTextAlign(ColumnTextAlign.END);
        }
    }

    protected String convertOrderString(String order) {
        if (order.toLowerCase().contains("asc"))
            return "asc";
        else
            return "desc";
    }

    protected String createOrderBy(List<QuerySortOrder> sortOrders) {
        StringBuilder orderBy = new StringBuilder();
        AtomicReference<Boolean> first = new AtomicReference<>();
        first.set(true);

        sortOrders.forEach(order -> {
            if (!order.getSorted().isEmpty()) {         // is dbColumnName set in the @GridEditColumn annotation?
                if (first.get()) {
                    orderBy.append(" order by ");
                } else {
                    orderBy.append(",");
                }
                orderBy.append(order.getSorted()).append(" ").append(convertOrderString(order.getDirection().toString())).append(" ");
                first.set(false);
            }
        });
        return orderBy.toString();
    }

    private Component getAddRowHeader() {
        if (canAddEntity()) {
            btnAdd.setClassName("icon-plus");
            Icon plus = new Icon(VaadinIcon.PLUS);
            btnAdd.setIcon(plus);
            btnAdd.addClickListener(evt -> addNew());
            PopoverMessage.addPopover("Add row", btnAdd);
            return btnAdd;
        } else {
            return new Span("");
        }
    }

    private void addFieldColumns(List<GridColumnInfo> gridColumns) {
        // Add field-based columns
        Arrays.stream(entityClass.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(GridEditColumn.class))
            .forEach(field -> {
                GridEditColumn annotation = field.getAnnotation(GridEditColumn.class);
                gridColumns.add(new GridColumnInfo(
                    field.getName(),
                    annotation.header().isEmpty() ? field.getName() : annotation.header(),
                    annotation.dbColumnName(),
                    annotation.order(),
                    annotation.sortable(),
                    field.getType(),
                    null,
                    annotation.format(),
                    annotation.editorClass(),
                    annotation.labelGenerator(),
                    annotation.fieldLength(),
                    annotation.flexGrow(),
                    annotation.minValue(),
                    annotation.maxValue(),
                    annotation.textAlign(),
                    annotation.autoWidth(),
                    annotation.resizable(),
                    annotation.arrayEndIdx(),
                    annotation.alternatingCol()
                ));
            });
    }

    private void addMethodColumns(List<GridColumnInfo> gridColumns) {
        // Add method-based columns
        Arrays.stream(entityClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(GridEditColumn.class))
            .forEach(method -> {
                GridEditColumn annotation = method.getAnnotation(GridEditColumn.class);
                String propertyName = method.getName();
                if (propertyName.startsWith("get")) {
                    propertyName = propertyName.substring(3, 4).toLowerCase() + propertyName.substring(4);
                }
                gridColumns.add(new GridColumnInfo(
                    propertyName,
                    annotation.header().isEmpty() ? propertyName : annotation.header(),
                    annotation.dbColumnName(),
                    annotation.order(),
                    annotation.sortable(),
                    method.getReturnType(),
                    method,
                    annotation.format(),
                    annotation.editorClass(),
                    annotation.labelGenerator(),
                    annotation.fieldLength(),
                    annotation.flexGrow(),
                    annotation.minValue(),
                    annotation.maxValue(),
                    annotation.textAlign(),
                    annotation.autoWidth(),
                    annotation.resizable(),
                    annotation.arrayEndIdx(),
                    annotation.alternatingCol()
                ));
            });
    }

    // All or some of these props could come from the annotations as well
    private void setStandardColumnProperties(Grid.Column<T> column, GridColumnInfo columnInfo, String header) {
        column
            .setFlexGrow(columnInfo.flexGrow)
            .setAutoWidth(columnInfo.autoWidth)
            .setResizable(columnInfo.resizable)
            .setHeader((header != null ? header : columnInfo.header()))
            .setSortable(columnInfo.sortable())
            .setTextAlign(columnInfo.textAlign());
        if (columnInfo.sortable())
            column.setSortProperty(columnInfo.dbColumnName());
    }

    private void setStandardColumnPropertiesArray(Grid.Column<T> column, GridColumnInfo columnInfo, String header, String arrayIndex) {
        setStandardColumnProperties(column, columnInfo, header);
        if (columnInfo.sortable())
            column.setSortProperty(columnInfo.dbColumnName()+arrayIndex);
    }

    /**
     * Below are the different column editor types
     */
    private Grid.Column<T> makeFixedCalculatedTextColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addColumn(columnInfo.propertyName())
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return getFixedCalculatedText(item, columnInfo.propertyName());
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeStandardTextFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createStandardTextField(columnInfo.fieldLength()), (item, newValue) -> {
                genericGrid.select(item);
                String setterMethod = "set" + camelName;
                try {
                    if (!entityClass.getMethod("get" + camelName).invoke(item).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeShortTextFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createShortTextField(columnInfo.fieldLength()), (item, newValue) -> {
                genericGrid.select(item);
                String setterMethod = "set" + camelName;
                try {
                    if (!entityClass.getMethod("get" + camelName).invoke(item).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName().getClass().getName(),  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeStandardTextAreaFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createStandardTextArea(columnInfo.fieldLength()), (item, newValue) -> {
                genericGrid.select(item);
                String setterMethod = "set" + camelName;
                try {
                    if (!entityClass.getMethod("get" + camelName).invoke(item).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeIntegerFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createShortIntegerField("", (long) columnInfo.minValue(), (long) columnInfo.maxValue(), 1), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (newValue == null || newValue < columnInfo.minValue() || newValue > columnInfo.maxValue()) {
                        setValueMustBeBetweenError(item, camelName, columnInfo);
                        return;
                    }
                    if (!entityClass.getMethod("get" + camelName).invoke(item).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return new DecimalFormat(columnInfo.format()).format( entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeBigDecimalFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createShortBigDecimalField(""), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (newValue == null || newValue.doubleValue() < columnInfo.minValue() || newValue.doubleValue() > columnInfo.maxValue()) {
                        setValueMustBeBetweenError(item, camelName, columnInfo);
                        return;
                    }
                    if (!entityClass.getMethod("get" + camelName).invoke(item).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return new DecimalFormat(columnInfo.format()).format( entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeNumericFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createShortNumberField(""), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (newValue == null || newValue.floatValue() < columnInfo.minValue() || newValue.floatValue() > columnInfo.maxValue()) {
                        setValueMustBeBetweenError(item, camelName, columnInfo);
                        return;
                    }
                    if (!entityClass.getMethod("get" + camelName).invoke(item).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return new DecimalFormat(columnInfo.format()).format( entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeBooleanFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(new Checkbox(), (item, newValue) -> {
                try {
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new ComponentRenderer<>(item -> {
                try {
                    return InputFieldCreator.createCheckbox((boolean) entityClass.getMethod("get" + camelName).invoke(item), !isEditableEntity(item), columnInfo.format);
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return null;
            }));
    }

    private <S> Grid.Column<T> makeSelectFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        Select<S> selectEditorComponent = new Select<>();
        selectEditorComponent.setItems(getItemsForSelect(columnInfo.propertyName()));
        if (!columnInfo.labelGenerator.isEmpty()) {
            selectEditorComponent.setItemLabelGenerator(item -> {
                try {
                    return columnInfo.type.getMethod(columnInfo.labelGenerator).invoke(item).toString();
                } catch (Exception e) {
                    setSystemError(columnInfo.type.getName(),  columnInfo.propertyName(), e);
                }
                return "";
            });
        }
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(selectEditorComponent, (item, newValue) -> {
                try {
                    if (((entityClass.getMethod("get" + camelName).invoke(item) == null) || ((newValue != null) && !entityClass.getMethod("get" + camelName).invoke(item).equals(newValue))) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeDatePickerFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(DateTimePickerCreator.createDatePicker("", columnInfo.format, true), (item, newValue) -> {
                try {
                    if (((entityClass.getMethod("get" + camelName).invoke(item) == null) || ((newValue != null) && !entityClass.getMethod("get" + camelName).invoke(item).equals(newValue))) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return (String)formatValue(entityClass.getMethod("get" + camelName).invoke(item), columnInfo);
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeTrafficlightFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {
        Select<String> trafficlightEditorComponent =  new Select<>();
        if (columnInfo.format.equalsIgnoreCase("reverse")) {
            trafficlightEditorComponent.setItems(TrafficLight.TRAFFICLIGHT_REVERSE);
        } else {
            trafficlightEditorComponent.setItems(TrafficLight.TRAFFICLIGHT_NORMAL);
        }
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(trafficlightEditorComponent, (item, newValue) -> {
                try {
                    if (!entityClass.getMethod("get" + camelName).invoke(item).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new ComponentRenderer<>(item -> {
                try {
                    if (columnInfo.format.equalsIgnoreCase("reverse")) {
                        return TrafficLight.createRadioButtonGroup("", TrafficLight.TRAFFICLIGHT_REVERSE, (String) entityClass.getMethod("get" + camelName).invoke(item), !isEditableEntity(item) || readonly, RadioButtonTheme.TRAFFICLIGHT);
                    } else {
                        return TrafficLight.createRadioButtonGroup("", TrafficLight.TRAFFICLIGHT_NORMAL, (String) entityClass.getMethod("get" + camelName).invoke(item), !isEditableEntity(item) || readonly, RadioButtonTheme.TRAFFICLIGHT);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(), columnInfo.propertyName(), e);
                }
                return null;
            }));
    }

    private void makeRichTextFieldColumn(GridColumnInfo columnInfo, String camelName, boolean readonly) {

        genericGrid.addColumn(new IconRenderer<>(item -> {
            Button btnEditor = new Button();
            btnEditor.setClassName("icon-trash");
            Icon iconEditor = new Icon(VaadinIcon.EDIT);

            btnEditor.addClickListener(elem -> {            // No DB changes yet, only when updating the BidRequest as a whole
                Dialog dialog = new Dialog();
                dialog.setModal(true);
                dialog.setDraggable(true);
                dialog.setResizable(true);
                dialog.setWidth("50rem");
                dialog.setHeight("35rem");
                btnEditor.getStyle().set(COLOR, "green");

                RichTextEditor rte = new RichTextEditor();
                rte.addThemeVariants(RichTextEditorVariant.LUMO_COMPACT);
                rte.setSizeFull();

                try {
                    rte.setValue((String)entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception ex) {
                    setSystemError(item.getClass().getName().getClass().getName(), columnInfo.propertyName(), ex);
                }
                Button saveButton = new Button("Save");
                Button cancelButton = new Button("Cancel");
                saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                HorizontalLayout buttonLayout = new HorizontalLayout();
                VerticalLayout verticalLayout = new VerticalLayout();
                verticalLayout.setSizeFull();
                verticalLayout.setPadding(false);
                verticalLayout.setMargin(false);

                if (isEditableEntity(item) && !readonly) {
                    addPopover("Edit text", btnEditor);
                    buttonLayout.add(saveButton, cancelButton);
                    verticalLayout.add(new H4("Edit description"), rte, buttonLayout);
                    rte.setReadOnly(false);
                } else {
                    addPopover("View text", btnEditor);
                    buttonLayout.add(cancelButton);
                    verticalLayout.add(new H4("View description"), rte, buttonLayout);
                    rte.setReadOnly(true);
                }
                dialog.add(verticalLayout);
                dialog.addDialogCloseActionListener(event -> {
                    // prevent closing by clicking outside
                });
                dialog.open();

                saveButton.addClickListener(e -> {
                    try {
                        entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, rte.getValue());
                        saveEntity(item);
                    } catch (Exception ex) {
                        setSystemError(item.getClass().getName().getClass().getName(), columnInfo.propertyName(), ex);
                    }
                    btnEditor.getStyle().remove(COLOR);
                    dialog.close();
                });

                cancelButton.addClickListener(e -> {
                    btnEditor.getStyle().remove(COLOR);
                    dialog.close();
                });

            });
            btnEditor.setIcon(iconEditor);
            return btnEditor;
            }, item -> ""))
            .setHeader("Description")
            .setAutoWidth(true)
            .setFlexGrow(0)
            .setResizable(false)
            .setTextAlign(ColumnTextAlign.END);
    }

    private Grid.Column<T> makeArrayIntegerFieldColumns(GridColumnInfo columnInfo, int idx, String camelName, boolean readonly) {
        return genericGrid.addEditColumn((ValueProvider<T, ?>) item -> {
                try {
                    return (Integer) entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx);  }
                catch (Exception e) {
                    setValueMustBeBetweenError(item, camelName, columnInfo);
                    return null;
                }
            })
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createShortIntegerField("", round(columnInfo.minValue()), round(columnInfo.maxValue()), 1), (item, newValue) -> {
                try {
                    if (newValue == null || newValue < columnInfo.minValue() || newValue > columnInfo.maxValue()) {
                        setValueMustBeBetweenError(item, camelName, columnInfo);
                        return;
                    }
                    if (!entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, Integer.TYPE, Integer.class).invoke(item, idx, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return new DecimalFormat(columnInfo.format()).format( entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeArrayBigDecimalFieldColumns(GridColumnInfo columnInfo, int idx, String camelName, boolean readonly) {
        return genericGrid.addEditColumn((ValueProvider<T, ?>) item -> {
            try {
                return entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx);  }
            catch (Exception e) {
                setValueMustBeBetweenError(item, camelName, columnInfo);
                return null;
            }
            })
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createShortBigDecimalField(""), (item, newValue) -> {
                try {
                    if (newValue == null || newValue.doubleValue() < columnInfo.minValue() || newValue.doubleValue() > columnInfo.maxValue()) {
                        setValueMustBeBetweenError(item, camelName, columnInfo);
                        return;
                    }
                    if (!entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, Integer.TYPE, BigDecimal.class).invoke(item, idx, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return new DecimalFormat(columnInfo.format()).format( entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeArrayDoubleFieldColumns(GridColumnInfo columnInfo, int idx, String camelName, boolean readonly) {
        return genericGrid.addEditColumn((ValueProvider<T, ?>) item -> {
            try {
                return entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx);  }
            catch (Exception e) {
                setValueMustBeBetweenError(item, camelName, columnInfo);
                return null;
            }
            })
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createShortNumberField(""), (item, newValue) -> {
                try {
                    if (newValue == null || newValue < columnInfo.minValue() || newValue > columnInfo.maxValue()) {
                        setValueMustBeBetweenError(item, camelName, columnInfo);
                        return;
                    }
                    if (!entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, Integer.TYPE,Double.class).invoke(item, idx, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return new DecimalFormat(columnInfo.format()).format( entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeArrayFloatFieldColumns(GridColumnInfo columnInfo, int idx, String camelName, boolean readonly) {
        return genericGrid.addEditColumn((ValueProvider<T, ?>) item -> {
            try {
                return entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx);  }
            catch (Exception e) {
                setValueMustBeBetweenError(item, camelName, columnInfo);
                return null;
            }
            })
            .withCellEditableProvider(item -> this.isEditableEntity(item) && !readonly)     // Takes care of skipping column when readonly and using tab from field to field
            .custom(InputFieldCreator.createShortFloatField(""), (item, newValue) -> {
                try {
                    if (newValue == null || newValue.floatValue() < columnInfo.minValue() || newValue.floatValue() > columnInfo.maxValue()) {
                        setValueMustBeBetweenError(item, camelName, columnInfo);
                        return;
                    }
                    if (!entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx).equals(newValue) && validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, Integer.TYPE, Float.class).invoke(item, idx, newValue.floatValue());
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return new DecimalFormat(columnInfo.format()).format( entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeArrayCalculatorColumns(GridColumnInfo columnInfo, int idx, String camelName) {
        return genericGrid.addColumn(item -> {
            try {
                Object value = columnInfo.method().invoke(item, idx);
                return formatValue(value, columnInfo);
            } catch (Exception e) {
                setSystemError(item.getClass().getName(), columnInfo.propertyName(), e);
                return null;
            }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return new DecimalFormat(columnInfo.format()).format( entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private void setValueMustBeBetweenError(T item, String camelName, GridColumnInfo columnInfo) {
        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
    }

    private void addPopover(String text, Component target) {
        Popover popover = new Popover();
        popover.add(text);
        popover.addThemeVariants(PopoverVariant.ARROW);
        popover.setPosition(PopoverPosition.TOP);
        popover.setOpenOnClick(false);
        popover.setOpenOnHover(true);
        popover.setOpenOnFocus(false);
        popover.setTarget(target);
    }

    private boolean isTemporalType(Class<?> type) {
        return LocalDateTime.class.isAssignableFrom(type) ||
            LocalDate.class.isAssignableFrom(type) ||
            Date.class.isAssignableFrom(type);
    }

    private Object formatValue(Object value, GridColumnInfo columnInfo) {
        if (value == null) return "";

        if (!(columnInfo.format().isEmpty() && isTemporalType(columnInfo.type()))) {
            switch (value) {
                case LocalDateTime localDateTime -> {
                    return DateTimeFormatter.ofPattern(columnInfo.format()).format(localDateTime);
                }
                case LocalDate localDate -> {
                    return DateTimeFormatter.ofPattern(columnInfo.format()).format(localDate);
                }
                case Date date -> {
                    return new SimpleDateFormat(columnInfo.format()).format(date);
                }
                default -> { }  // Don't set to "" it will ruin display of calculated columns based on the date
            }
        }
        return value;
    }

    private record GridColumnInfo(
        String propertyName,
        String header,
        String dbColumnName,
        int order,
        boolean sortable,
        Class<?> type,
        Method method,
        String format,
        Class<?> editorClass,
        String labelGenerator,
        int fieldLength,
        int flexGrow,
        double minValue,
        double maxValue,
        ColumnTextAlign textAlign,
        boolean autoWidth,
        boolean resizable,
        int arrayEndIdx,
        boolean alternatingCol) {
    }

}
