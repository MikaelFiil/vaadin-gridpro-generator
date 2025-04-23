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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Resource extends BaseEntity {

    @EqualsAndHashCode.Include
    private Integer id;                            // Must be immutable

    @GridEditColumn(header = "Category", order = 1, sortable = false, editorClass = Select.class)
    private String category = "";

    @GridEditColumn(header = "Department", order = 2, sortable = false, editorClass = Select.class)
    private String department = "";

    @GridEditColumn(header = "Job position", order = 3, sortable = false, editorClass = Select.class)
    private String jobPosition = "";

    @GridEditColumn(header = "Hours", order = 4, fieldLength = 7, maxValue = 999, format = "%d", textAlign =  ColumnTextAlign.END, editorClass = IntegerField.class)
    private Integer margin = 0;

    @GridEditColumn(header = "Description", order = 5, editorClass = TextField.class)
    private String description = "";

    @GridEditColumn(header = "Is Active", order = 6, editorClass = Checkbox.class)
    private Boolean active = false;

}
