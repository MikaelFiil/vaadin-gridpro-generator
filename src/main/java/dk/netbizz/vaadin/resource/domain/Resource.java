package dk.netbizz.vaadin.resource.domain;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.textfield.IntegerField;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.BaseEntity;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GridEditColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Resource implements BaseEntity {

    @EqualsAndHashCode.Include
    private Integer id;                            // Must be immutable

    @GridEditColumn(header = "Category", order = 1, fieldLength = 50)
    private String category = "";

    @GridEditColumn(header = "Department", order = 2, fieldLength = 50)
    private String department = "";

    @GridEditColumn(header = "Job position", order = 3, fieldLength = 50)
    private String jobPosition = "";

    @GridEditColumn(header = "Margin", order = 4, fieldLength = 7, maxValue = 9999, format = "%d kr.", textAlign =  ColumnTextAlign.END, editorClass = IntegerField.class)
    private Integer margin = 0;

    @GridEditColumn(header = "Description", order = 5, fieldLength = 50)
    private String description = "";

    @GridEditColumn(header = "Active", order = 6, editorClass = Checkbox.class)
    private Boolean active = false;

}
