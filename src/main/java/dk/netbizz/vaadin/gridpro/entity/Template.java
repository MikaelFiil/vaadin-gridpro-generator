package dk.netbizz.vaadin.gridpro.entity;


import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import dk.netbizz.vaadin.gridpro.entity.base.BaseEntity;
import dk.netbizz.vaadin.gridpro.entity.base.GridEditColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Template extends BaseEntity {

    @EqualsAndHashCode.Include
    private Integer id;                            // Must be immutable

    @GridEditColumn(header = "Template Name", order = 1, fieldLength = 50, editorClass = TextField.class)
    private String templateName = "New name ...";

    @GridEditColumn(header = "Margin %", order = 4, fieldLength = 7, maxValue = 9999, format = "%d %%", textAlign =  ColumnTextAlign.END, editorClass = IntegerField.class)
    private Integer margin = 0;

    @GridEditColumn(header = "Description", order = 5, editorClass = TextField.class)
    private String description;

    @GridEditColumn(header = "Is Active", order = 6, editorClass = Checkbox.class)
    private Boolean active = false;


    List<Resource> resources = new ArrayList<>();
    List<Cost> costs = new ArrayList<>();
    List<Risk> risks = new ArrayList<>();


    // Easier construction avoiding arrays in parameters
    public Template(Integer id, String templateName, Integer margin, String description, Boolean active) {
        this.id = id;
        this.templateName = templateName;
        this.margin = margin;
        this.description = description;
        this.active = active;
    }

}
