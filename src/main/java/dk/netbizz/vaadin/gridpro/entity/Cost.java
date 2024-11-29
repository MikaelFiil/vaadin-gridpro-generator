package dk.netbizz.vaadin.gridpro.entity;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import dk.netbizz.vaadin.gridpro.entity.base.BaseEntity;
import dk.netbizz.vaadin.gridpro.entity.base.GridEditColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cost implements BaseEntity {

    @EqualsAndHashCode.Include
    private Integer id;                            // Must be immutable

    @GridEditColumn(header = "Description", order = 1, editorClass = TextField.class)
    private String description;

    @GridEditColumn(header = "Commercial type", order = 2, sortable = false, editorClass = Select.class)
    private String commercialType = "";

    @GridEditColumn(header = "Cost category", order = 3, sortable = false, editorClass = Select.class)
    private String category = "";

    @GridEditColumn(header = "Yearly cost", order = 4, fieldLength = 7, maxValue = 999999, format = "%d", textAlign =  ColumnTextAlign.END, editorClass = IntegerField.class)
    private Integer yearlyCost = 0;

    @GridEditColumn(header = "Is Active", order = 5, editorClass = Checkbox.class)
    private Boolean active = false;

}