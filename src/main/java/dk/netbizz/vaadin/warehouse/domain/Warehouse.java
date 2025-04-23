package dk.netbizz.vaadin.warehouse.domain;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.BaseEntity;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GridEditColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Warehouse implements BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    private Integer id;

    @GridEditColumn(header = "Warehouse name", order = 1, fieldLength = 50, editorClass = TextField.class)
    String warehouseName;

    @GridEditColumn(header = "Street", order = 2, fieldLength = 50, editorClass = TextField.class)
    String street;

    @GridEditColumn(header = "City", order = 3, fieldLength = 50, editorClass = TextField.class)
    String city;

    @GridEditColumn(header = "M2", order = 4, fieldLength = 5, minValue = 0, maxValue = 9999, format = "%d m2", textAlign =  ColumnTextAlign.END, editorClass = IntegerField.class)
    Integer sqrM2;

    @Version
    Integer version;

    @Override
    public String toString() {
        return warehouseName + " - " + sqrM2 + " m2";
    }

}
