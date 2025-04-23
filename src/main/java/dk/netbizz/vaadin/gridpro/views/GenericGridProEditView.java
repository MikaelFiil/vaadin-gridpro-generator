package dk.netbizz.vaadin.gridpro.views;

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
 * Ideas
 * -----
 * field.getElement().callJsFunction("scrollIntoView");
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;
import dk.netbizz.vaadin.gridpro.entity.base.BaseEntity;
import dk.netbizz.vaadin.gridpro.entity.base.GridEditColumn;
import dk.netbizz.vaadin.gridpro.themes.RadioButtonTheme;
import dk.netbizz.vaadin.gridpro.utils.ConfirmationDialog;
import dk.netbizz.vaadin.gridpro.utils.DateTimePickerCreator;
import dk.netbizz.vaadin.gridpro.utils.InputFieldCreator;
import dk.netbizz.vaadin.gridpro.views.components.TrafficLight;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public abstract class GenericGridProEditView<T extends BaseEntity> extends VerticalLayout {

    protected final GridPro<T> genericGrid;
    private final Class<T> entityClass;
    private T selectedItem;                                     // This is not really useful with GridPro
    private final Button btnAdd = new Button();

    protected GenericGridProEditView(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.genericGrid = new GridPro<>(entityClass);
        this.genericGrid.setSingleCellEdit(false);      // Default
        this.genericGrid.setEditOnClick(true);
        setupLayout();
        // Further setup initialization done via subclass
        addClassName("viavea-zero-margin-padding");
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
    protected abstract List<T> loadEntities();
    protected abstract void deleteEntity(T entity);
    protected abstract void selectEntity(T entity);                                                 // Seldom used, GridPro makes row selection obsolete - I think.
    protected abstract <S> List<S> getItemsForSelect(String colName);
    protected abstract String getFixedCalculatedText(T entity, String colName);
    protected abstract boolean validUpdate(T entity, String colName, Object  newColValue);
    protected abstract boolean isEditableEntity(T entity);
    protected abstract boolean canAddEntity();                      // Should be based on user profile rights
    protected abstract String getCssClassName(String aCssClass);

    protected void setupGridEventHandlers() {
        // genericGrid.setSelectionMode(Grid.SelectionMode.SINGLE);  // Set this if you really want to be able to select a row
        genericGrid.setSelectionMode(Grid.SelectionMode.NONE);      // If you use this, you cannot addSelectionListener below

        // A selectionListener is a bit detached in GridPro you can easily have one selected row while editing another row !?
        // genericGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(item -> {
        //   selectedItem = item;
        //    selectEntity(selectedItem);
        // }));
    }

    protected void refreshGrid() {
        genericGrid.setItems(loadEntities());
        genericGrid.recalculateColumnWidths();
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
                .collect(Collectors.toList());

        // and add them to grid
        for (GridColumnInfo columnInfo : gridColumns) {

            String camelName = columnInfo.propertyName().substring(0, 1).toUpperCase() + columnInfo.propertyName().substring(1);

            if (columnInfo.method() != null) {
                // For method-based columns

                switch (columnInfo.editorClass.getName()) {                                 // using switch is future proof

                    case "dk.netbizz.vaadin.gridpro.entity.base.ArrayCalculator":           // Array method
                        if (columnInfo.alternatingCol) {
                            indexesOfAlternatingCols.add(colIdx);
                        }    // is it a new alternating column type
                        int lastIdx = ((dynamicParameters.get(columnInfo.propertyName + ".arrayEndIdx") != null ? Math.min(columnInfo.arrayEndIdx, Integer.parseInt(dynamicParameters.get(columnInfo.propertyName + ".arrayEndIdx"))) : columnInfo.arrayEndIdx));
                        for (int idx = 0; idx <= lastIdx; idx++) {
                            setStandardColumnProperties(makeArrayCalculatorColumns(columnInfo, idx, camelName), columnInfo, dynamicParameters.get(columnInfo.propertyName + ".header" + idx));
                        }
                        break;

                    default:                                // Simple Method
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

                if (columnInfo.editorClass == null) { // For un-editable field-based columns
                    if (isTemporalType(columnInfo.type()) && !columnInfo.format().isEmpty()) {
                        setStandardColumnProperties(
                            genericGrid.addColumn(item -> {
                                try {
                                    Field field = entityClass.getDeclaredField(columnInfo.propertyName());
                                    field.setAccessible(true);
                                    Object value = field.get(item);
                                    return formatValue(value, columnInfo);
                                } catch (Exception e) {
                                    setSystemError(item.getClass().getName(), columnInfo.propertyName(), e);
                                    return null;
                                }
                            })
                            , columnInfo, null);
                    } else {
                        setStandardColumnProperties(genericGrid.addColumn(columnInfo.propertyName()), columnInfo, null);      // Plain vanilla display only column
                    }
                    colIdx++;

                } else { // For field based editable columns

                    switch (columnInfo.editorClass.getName()) {
                        case "com.vaadin.flow.component.textfield.TextField":
                            if (columnInfo.fieldLength <= 15) {
                                setStandardColumnProperties(makeShortTextFieldColumn(columnInfo, camelName), columnInfo, null);
                            } else {
                                setStandardColumnProperties(makeStandardTextFieldColumn(columnInfo, camelName), columnInfo, null);
                            }
                            colIdx++;
                            break;

                        case "com.vaadin.flow.component.textfield.IntegerField":
                            setStandardColumnProperties(makeIntegerFieldColumn(columnInfo, camelName), columnInfo, null);
                            colIdx++;
                            break;

                        case "com.vaadin.flow.component.textfield.BigDecimalField":
                            setStandardColumnProperties(makeBigDecimalFieldColumn(columnInfo, camelName), columnInfo, null);
                            colIdx++;
                            break;

                        case "com.vaadin.flow.component.checkbox.Checkbox":
                            setStandardColumnProperties(makeBooleanFieldColumn(columnInfo, camelName), columnInfo, null);
                            colIdx++;
                            break;

                        case "com.vaadin.flow.component.select.Select":
                            setStandardColumnProperties(makeSelectFieldColumn(columnInfo, camelName), columnInfo, null);
                            colIdx++;
                            break;

                        case "com.vaadin.flow.component.datepicker.DatePicker":
                            setStandardColumnProperties(makeDatePickerFieldColumn(columnInfo, camelName), columnInfo, null);
                            colIdx++;
                            break;

                        case "dk.netbizz.vaadin.gridpro.views.components.TrafficLight":
                            setStandardColumnProperties(makeTrafficlightFieldColumn(columnInfo, camelName), columnInfo, null);
                            colIdx++;
                            break;

                        case "com.vaadin.flow.component.richtexteditor.RichTextEditor":
                            makeRichTextFieldColumn(columnInfo, camelName);
                            colIdx++;
                            break;

                        case "dk.netbizz.tendernisse.views.utils.gridprogenerator.FixedCalculatedText":
                            setStandardColumnProperties(makeFixedCalculatedTextColumn(columnInfo, camelName), columnInfo, null);
                            colIdx++;
                            break;

                        case "dk.netbizz.vaadin.gridpro.entity.base.ArrayIntegerEditor":
                        case "dk.netbizz.vaadin.gridpro.entity.base.ArrayBigDecimalEditor":
                            if (columnInfo.alternatingCol) { indexesOfAlternatingCols.add(colIdx); }    // is it a new alternating column type
                            int lastIdx = ((dynamicParameters.get(columnInfo.propertyName + ".arrayEndIdx") != null ? Math.min(columnInfo.arrayEndIdx, Integer.parseInt(dynamicParameters.get(columnInfo.propertyName + ".arrayEndIdx"))) : columnInfo.arrayEndIdx));
                            for (int idx = 0; idx <= lastIdx; idx++) {
                                if (columnInfo.editorClass.getName().contains("ArrayIntegerEditor")) {
                                    setStandardColumnProperties(makeArrayIntegerFieldColumns(columnInfo, idx, camelName), columnInfo, dynamicParameters.get(columnInfo.propertyName + ".header" + idx));
                                } else if (columnInfo.editorClass.getName().contains("ArrayBigDecimalEditor")) {
                                    setStandardColumnProperties(makeArrayBigDecimalFieldColumns(columnInfo, idx, camelName), columnInfo, dynamicParameters.get(columnInfo.propertyName + ".header" + idx));
                                }
                                colIdx++;
                            }
                            break;
                    }
                }
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

        // Add delete or lock column icon
        genericGrid.addColumn(new IconRenderer<>(item -> {
            Button btnRemove = new Button();
            btnRemove.addClassName("viavea-zero-margin-padding");
            Icon iconTrashLock;
            if (isEditableEntity(item)) {
                iconTrashLock = new Icon(VaadinIcon.TRASH);
                addPopover("Delete row", btnRemove);
                btnRemove.addClickListener(elem -> {            // No DB changes yet, only when updating the BidRequest as a whole
                    iconTrashLock.getStyle().set("color", "red");
                    ConfirmationDialog.confirm("Warning", "You are deleting the row, continue?", () -> iconTrashLock.getStyle().remove("color"))
                        .addConfirmListener(event -> {
                            deleteEntity(item);
                            refreshGrid();
                        });
                });
            } else {
                iconTrashLock = new Icon(VaadinIcon.LOCK);
                addPopover("Row is read only", btnRemove);
            }
            iconTrashLock.addClassName("viavea-grid-trash-icon");
            btnRemove.setIcon(iconTrashLock);
            return btnRemove;
        },item -> ""))
        .setHeader(getAddRowHeader())
        .setAutoWidth(true)
        .setFlexGrow(0)
        .setResizable(false)
        .setTextAlign(ColumnTextAlign.END);
    }

    private Component getAddRowHeader() {
        if (canAddEntity()) {
            Icon plus = new Icon(VaadinIcon.PLUS);
            plus.addClassName("viavea-grid-plus-icon");
            btnAdd.setIcon(plus);
            btnAdd.addClickListener(evt -> addNew());
            btnAdd.addClassName("viavea-zero-margin-padding");
            addPopover("Add row", btnAdd);
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
                    annotation.order(),
                    annotation.sortable(),
                    field.getType(),
                    null,
                    annotation.format(),
                    annotation.editorClass(),
                    annotation.labelGenerator(),
                    annotation.fieldLength(),
                    annotation.minValue(),
                    annotation.maxValue(),
                    annotation.textAlign(),
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
                    annotation.order(),
                    annotation.sortable(),
                    method.getReturnType(),
                    method,
                    annotation.format(),
                    annotation.editorClass(),
                    annotation.labelGenerator(),
                    annotation.fieldLength(),
                    annotation.minValue(),
                    annotation.maxValue(),
                    annotation.textAlign(),
                    annotation.arrayEndIdx(),
                    annotation.alternatingCol()
                ));
            });
    }

    // All or some of these props could come from the annotations as well
    private void setStandardColumnProperties(Grid.Column<T> column, GridColumnInfo columnInfo, String header) {
        column
            .setFlexGrow(1)
            .setAutoWidth(true)
            .setResizable(true)
            .setHeader((header != null ? header : columnInfo.header()))
            .setSortable(columnInfo.sortable())
            .setTextAlign(columnInfo.textAlign());
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

    private Grid.Column<T> makeStandardTextFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(this::isEditableEntity)
            .custom(InputFieldCreator.createStandardTextField(columnInfo.fieldLength()), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeShortTextFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(this::isEditableEntity)
            .custom(InputFieldCreator.createShortTextField(columnInfo.fieldLength()), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName().getClass().getName(),  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeIntegerFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(this::isEditableEntity)
            .custom(InputFieldCreator.createShortIntegerField("", (long) columnInfo.minValue(), (long) columnInfo.maxValue(), 1), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (newValue == null || newValue < columnInfo.minValue() || newValue > columnInfo.maxValue()) {
                        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
                        return;
                    }
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeBigDecimalFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(this::isEditableEntity)
            .custom(InputFieldCreator.createShortBigDecimalField(""), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (newValue == null || newValue.doubleValue() < columnInfo.minValue() || newValue.doubleValue() > columnInfo.maxValue()) {
                        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
                        return;
                    }
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeBooleanFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(this::isEditableEntity)
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
                    return InputFieldCreator.createCheckbox((boolean) entityClass.getMethod("get" + camelName).invoke(item), !isEditableEntity(item));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return null;
            }));
    }

    private <S> Grid.Column<T> makeSelectFieldColumn(GridColumnInfo columnInfo, String camelName) {
        Select<S> selectEditorComponent = new Select<>();
        selectEditorComponent.addClassName(getCssClassName("viavea-select-class"));
        selectEditorComponent.setOverlayClassName(getCssClassName("viavea-select-class"));
        selectEditorComponent.setItems(getItemsForSelect(columnInfo.propertyName()));

        if (!columnInfo.labelGenerator.isEmpty()) {
            selectEditorComponent.setItemLabelGenerator( (item) -> {
                try {
                    return columnInfo.type.getMethod(columnInfo.labelGenerator).invoke(item).toString();
                } catch (Exception e) {
                    setSystemError(columnInfo.type.getName(),  columnInfo.propertyName(), e);
                }
                return "";
            });
        }

        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(this::isEditableEntity)
            .custom(selectEditorComponent, (item, newValue) -> {
                try {
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            });
    }


    private Grid.Column<T> makeDatePickerFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(this::isEditableEntity)
            .custom(DateTimePickerCreator.createDatePicker("", columnInfo.format, true), (item, newValue) -> {
                try {
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
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

    private Grid.Column<T> makeTrafficlightFieldColumn(GridColumnInfo columnInfo, String camelName) {
        Select<String> trafficlightEditorComponent =  new Select<>(); // InputFieldCreator.createStandardSelect("", null, null);
        if (columnInfo.format.equalsIgnoreCase("reverse")) {
            trafficlightEditorComponent.setItems(TrafficLight.TRAFFICLIGHT_REVERSE);
        } else {
            trafficlightEditorComponent.setItems(TrafficLight.TRAFFICLIGHT_NORMAL);
        }

        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(this::isEditableEntity)
            .custom(trafficlightEditorComponent, (item, newValue) -> {
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
                    if (columnInfo.format.equalsIgnoreCase("reverse")) {
                        return TrafficLight.createRadioButtonGroup("", TrafficLight.TRAFFICLIGHT_REVERSE, (String) entityClass.getMethod("get" + camelName).invoke(item), !isEditableEntity(item), RadioButtonTheme.TRAFFICLIGHT);
                    } else {
                        return TrafficLight.createRadioButtonGroup("", TrafficLight.TRAFFICLIGHT_NORMAL, (String) entityClass.getMethod("get" + camelName).invoke(item), !isEditableEntity(item), RadioButtonTheme.TRAFFICLIGHT);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(), columnInfo.propertyName(), e);
                }
                return null;
            }));
    }

    private void makeRichTextFieldColumn(GridColumnInfo columnInfo, String camelName) {

        genericGrid.addColumn(new IconRenderer<>(item -> {
            Button btnEditor = new Button();
            btnEditor.setClassName("icon-trash");
            Icon iconEditor;
            iconEditor = new Icon(VaadinIcon.EDIT);
            addPopover("Edit text", btnEditor);
            btnEditor.addClickListener(elem -> {            // No DB changes yet, only when updating the BidRequest as a whole
                Dialog dialog = new Dialog();
                dialog.setModal(true);
                dialog.setDraggable(true);
                dialog.setWidth("50rem");
                dialog.setHeight("30rem");
                btnEditor.getStyle().set("color", "green");

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

                if (isEditableEntity(item)) {
                    buttonLayout.add(saveButton, cancelButton);
                    verticalLayout.add(new H4("Edit description"), rte, buttonLayout);
                    rte.setReadOnly(false);
                } else {
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
                    btnEditor.getStyle().remove("color");
                    dialog.close();
                });

                cancelButton.addClickListener(e -> {
                    btnEditor.getStyle().remove("color");
                    dialog.close();
                });

            });
            iconEditor.setSize("20px");
            iconEditor.getStyle().set("padding", "0");
            btnEditor.setIcon(iconEditor);

            return btnEditor;
        }, item -> ""))
        .setHeader("Description")
        .setAutoWidth(true)
        .setFlexGrow(0)
        .setResizable(false)
        .setTextAlign(ColumnTextAlign.END);
    }

    private Grid.Column<T> makeArrayIntegerFieldColumns(GridColumnInfo columnInfo, int idx, String camelName) {
        return genericGrid.addEditColumn((ValueProvider<T, ?>) item -> {
            try {
                return (Integer) entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx);
            } catch (Exception e) {
                setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());
                return null;
            }})
            .withCellEditableProvider(this::isEditableEntity)
            .custom(InputFieldCreator.createShortIntegerField("", Math.round(columnInfo.minValue()), Math.round(columnInfo.maxValue()), 1), (item, newValue) -> {
                try {
                    if (newValue == null || newValue < columnInfo.minValue() || newValue > columnInfo.maxValue()) {
                        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
                        return;
                    }
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, Integer.TYPE, Integer.class).invoke(item, idx, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeArrayBigDecimalFieldColumns(GridColumnInfo columnInfo, int idx, String camelName) {
        return genericGrid.addEditColumn((ValueProvider<T, ?>) item -> {
            try {
                return entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx);
            } catch (Exception e) {
                setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());
                return null;
            }})
            .withCellEditableProvider(this::isEditableEntity)
            .custom(InputFieldCreator.createShortBigDecimalField(""), (item, newValue) -> {
                try {
                    if (newValue == null || newValue.doubleValue() < columnInfo.minValue() || newValue.doubleValue() > columnInfo.maxValue()) {
                        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
                        return;
                    }
                    if (validUpdate(item, columnInfo.propertyName(), newValue)) {
                        entityClass.getMethod("set" + camelName, Integer.TYPE, java.math.BigDecimal.class).invoke(item, idx, newValue);
                        saveEntity(item);
                    }
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
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
            }})
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item.getClass().getName(),  columnInfo.propertyName(), e);
                }
                return "";
            }));
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
                default -> {
                }
            }
        }
        return value;
    }

    private record GridColumnInfo(
        String propertyName,
        String header,
        int order,
        boolean sortable,
        Class<?> type,
        Method method,
        String format,
        Class<?> editorClass,
        String labelGenerator,
        int fieldLength,
        double minValue,
        double maxValue,
        ColumnTextAlign textAlign,
        int arrayEndIdx,
        boolean alternatingCol) {
    }
}
