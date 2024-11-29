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
 * Since a user can move complete freely around in the grid you can never know when editing is finished, hence there is instant persistence.
 * Every edit of a single cell will cause a call to save the entity of the row.
 *
 * The application domain view must extend this class and replace the generic placeholder with the domain class for the grid.
 *
 * Further improvements should probably be about overall code structure and utilize interfaces more etc.
 *
 * Git Repo
 * https://github.com/MikaelFiil/vaadin-gridpro-generator
 *
 *
 */

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
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
    private T selectedItem;     // This is not really useful with GridPro
    private Button btnAdd = new Button();

    protected GenericGridProEditView(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.genericGrid = new GridPro<>(entityClass);
        this.genericGrid.removeAllColumns();
        this.genericGrid.setSingleCellEdit(false);      // Default
        this.genericGrid.setEditOnClick(true);
        setupLayout();
        // Further setup initialization done via subclass
    }

    private void setupLayout() {
        setSizeFull();
        genericGrid.setSizeFull();
        add(genericGrid);

        btnAdd.setClassName("icon-plus");
        btnAdd.setIcon(new Icon(VaadinIcon.PLUS));
        btnAdd.addClickListener(evt -> addNew());
    }

    // Abstract methods to be implemented by specific domain views
    protected abstract void setValidationError(T entity, String columName, String msg);     // Let domain view handle UI messages
    protected abstract void setSystemError(T entity, String columName, Exception e);        // Let domain view handle UI messages
    protected abstract void saveEntity(T entity);
    protected abstract void addNew();
    protected abstract List<T> loadEntities();
    protected abstract void deleteEntity(T entity);
    protected abstract void selectEntity(T entity);                                         // Seldom used, GridPro makes row selection obsolete - I think.
    protected abstract List<String> getItemsForSelect(String colName);

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
    }

    protected void setupGrid(Map<String, String> dynamicParameters) {

        List<GridColumnInfo> gridColumns = new ArrayList<>();
        addFieldColumns(gridColumns);   // Using side effects - sorry
        addMethodColumns(gridColumns);

        List<Integer> indexesOfAlternatingCols = new ArrayList<>();
        int colIdx = 0;     // Global column index when adding to grid, needed for alternating columns

        // Sort columns by order
        gridColumns = gridColumns.stream()
            .sorted(Comparator.comparingInt(GridColumnInfo::order))
            .collect(Collectors.toList());

        // and add them to grid
        for (GridColumnInfo columnInfo : gridColumns) {

            if (columnInfo.method() != null) {
                // For method-based columns
                String camelName = columnInfo.propertyName().substring(0, 1).toUpperCase() + columnInfo.propertyName().substring(1);

                switch (columnInfo.editorClass.getName()) {

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
                                    setSystemError(item, columnInfo.propertyName(), e);
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
                                        setSystemError(item, columnInfo.propertyName(), e);
                                        return null;
                                    }
                                })
                                , columnInfo, null);
                        colIdx++;

                    } else {
                        setStandardColumnProperties(genericGrid.addColumn(columnInfo.propertyName()), columnInfo, null);      // Plain vanilla display only column
                        colIdx++;
                    }

                } else { // For field based editable columns

                    String camelName = columnInfo.propertyName().substring(0, 1).toUpperCase() + columnInfo.propertyName().substring(1);

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

        // Add delete column icon
        genericGrid.addColumn(new IconRenderer<>(item -> {
            Button btnRemove = new Button();
            btnRemove.setClassName("icon-trash");
            btnRemove.setIcon(new Icon(VaadinIcon.TRASH));
            btnRemove.addClickListener(elem -> {            // No DB changes yet, only when updating the BidRequest as a whole
                ConfirmationDialog.confirm("Warning", "You are deleting the item, continue?").addConfirmListener(event -> {
                    deleteEntity(item);
                    refreshGrid();
                });
            });
            return btnRemove;
        },item -> ""))
        .setHeader(btnAdd)
        .setAutoWidth(true)
        .setFlexGrow(0)
        .setResizable(false)
        .setTextAlign(ColumnTextAlign.END);
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
            .setTextAlign(columnInfo.textAlign());;
    }

    /**
     * Below are the different column editor types
     */

    private Grid.Column<T> makeStandardTextFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .custom(InputFieldCreator.createStandardTextField(columnInfo.fieldLength()), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeShortTextFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .withCellEditableProvider(item -> {
                Set<T> sl = genericGrid.getSelectedItems();
                return true;
            })
            .custom(InputFieldCreator.createShortTextField(columnInfo.fieldLength()), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeIntegerFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .custom(InputFieldCreator.createShortIntegerField("", (long) columnInfo.minValue(), (long) columnInfo.maxValue(), 1), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (newValue == null || newValue < columnInfo.minValue() || newValue > columnInfo.maxValue()) {
                        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
                        return;
                    }
                    entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeBigDecimalFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .custom(InputFieldCreator.createShortBigDecimalField(""), (item, newValue) -> {
                String setterMethod = "set" + camelName;
                try {
                    if (newValue == null || newValue.doubleValue() < columnInfo.minValue() || newValue.doubleValue() > columnInfo.maxValue()) {
                        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
                        return;
                    }
                    entityClass.getMethod(setterMethod, columnInfo.type).invoke(item, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeBooleanFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .custom(new Checkbox(), (item, newValue) -> {
                try {
                    entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new ComponentRenderer<>(item -> {
                try {
                    return new Checkbox((boolean) entityClass.getMethod("get" + camelName).invoke(item));
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
                return null;
            }));
    }

    private Grid.Column<T> makeSelectFieldColumn(GridColumnInfo columnInfo, String camelName) {
        Select<String> selectEditorComponent = new Select<>();
        selectEditorComponent.setItems(getItemsForSelect(columnInfo.propertyName()));

        return genericGrid.addEditColumn(columnInfo.propertyName())
            .custom(selectEditorComponent, (item, newValue) -> {
                try {
                    entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            });
    }

    private Grid.Column<T> makeDatePickerFieldColumn(GridColumnInfo columnInfo, String camelName) {
        return genericGrid.addEditColumn(columnInfo.propertyName())
            .custom(DateTimePickerCreator.createDatePicker("", columnInfo.format, true), (item, newValue) -> {
                try {
                    entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return (String)formatValue(entityClass.getMethod("get" + camelName).invoke(item), columnInfo);
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeTrafficlightFieldColumn(GridColumnInfo columnInfo, String camelName) {
        Select<String> trafficlightEditorComponent =  new Select<>(); // InputFieldCreator.createStandardSelect("", null, null);
        trafficlightEditorComponent.setItems(TrafficLight.TRAFFICLIGHT_NORMAL);

        return genericGrid.addEditColumn(columnInfo.propertyName())
            .custom(trafficlightEditorComponent, (item, newValue) -> {
                try {
                    entityClass.getMethod("set" + camelName, columnInfo.type).invoke(item, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new ComponentRenderer<RadioButtonGroup<String>, T>(item -> {
                try {
                    RadioButtonGroup<String> prop = TrafficLight.createRadioButtonGroup("", TrafficLight.TRAFFICLIGHT_NORMAL, RadioButtonTheme.TRAFFICLIGHT);
                    prop.setValue((String)entityClass.getMethod("get" + camelName).invoke(item));
                    return prop;
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
                return null;
            }));
    }

    private Grid.Column<T> makeArrayIntegerFieldColumns(GridColumnInfo columnInfo, int idx, String camelName) {
        return genericGrid.addEditColumn((ValueProvider<T, ?>) item -> {
                try {
                    return (Integer) entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx);  }
                catch (Exception e) {
                    setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());
                    return null;
                }
            })
            .custom(InputFieldCreator.createShortIntegerField("", Math.round(columnInfo.minValue()), Math.round(columnInfo.maxValue()), 1), (item, newValue) -> {
                 try {
                    if (newValue == null || newValue < columnInfo.minValue() || newValue > columnInfo.maxValue()) {
                        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
                        return;
                    }
                    Method method = entityClass.getMethod("set" + camelName, Integer.TYPE, java.lang.Integer.class);
                    method.invoke(item, idx, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                     setSystemError(item,  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
                return "";
            }));
    }

    private Grid.Column<T> makeArrayBigDecimalFieldColumns(GridColumnInfo columnInfo, int idx, String camelName) {
        return genericGrid.addEditColumn((ValueProvider<T, ?>) item -> {
                try {
                    return entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx);  }
                catch (Exception e) {
                    setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());
                    return null;
                }
            })
            .custom(InputFieldCreator.createShortBigDecimalField(""), (item, newValue) -> {
                try {
                    if (newValue == null || newValue.doubleValue() < columnInfo.minValue() || newValue.doubleValue() > columnInfo.maxValue()) {
                        setValidationError(item, camelName, "Value must be between " + columnInfo.minValue() + " and " + columnInfo.maxValue());     // Let domain view handle UI messaging
                        return;
                    }
                    Method method = entityClass.getMethod("set" + camelName, Integer.TYPE, java.math.BigDecimal.class);
                    method.invoke(item, idx, newValue);
                    saveEntity(item);
                    genericGrid.recalculateColumnWidths();
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
                }
            })
            .setRenderer(new TextRenderer<>(item -> {
                try {
                    return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
                } catch (Exception e) {
                    setSystemError(item,  columnInfo.propertyName(), e);
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
                setSystemError(item, columnInfo.propertyName(), e);
                return null;
            }
        })
        .setRenderer(new TextRenderer<>(item -> {
            try {
                return String.format(columnInfo.format() , entityClass.getMethod("get" + camelName, Integer.TYPE).invoke(item, idx));
            } catch (Exception e) {
                setSystemError(item,  columnInfo.propertyName(), e);
            }
            return "";
        }));

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
        Class editorClass,
        int fieldLength,
        double minValue,
        double maxValue,
        ColumnTextAlign textAlign,
        int arrayEndIdx,
        boolean alternatingCol) {
    }
}
