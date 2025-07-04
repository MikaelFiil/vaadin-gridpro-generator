package dk.netbizz.vaadin.item.domain;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import dk.netbizz.vaadin.gridpro.utils.components.TrafficLight;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.*;
import dk.netbizz.vaadin.warehouse.domain.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Item implements BaseEntity {

    // Easier construction avoiding arrays in parameters
    public Item(Integer id, String itemName, String category, Integer price, Warehouse warehouse, LocalDate birthday, Boolean active, String criticality) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.warehouse = warehouse;
        this.birthday = birthday;
        this.active = active;
        this.criticality = criticality;
        for (int i = 0; i < yearlyAmount.length; i++) {
            this.yearlyAmount[i] = 0;
            this.impactAmount[i] = 0;
            this.likelihood[i] = BigDecimal.ZERO;
        }
    }


    @Id
    @EqualsAndHashCode.Include
    private Integer id;    // Must be immutable

    @Version
    private Integer version = null;

    private Integer applicationUserId;

    @GridEditColumn(header = "Item Name", order = 1, dbColumnName = "item_name", fieldLength = 15, editorClass = TextField.class)
    private String itemName = "New name ...";

    @GridEditColumn(header = "Category", order = 2, dbColumnName = "category", sortable = true, editorClass = Select.class)
    private String category = "";

    @GridEditColumn(header = "Kr./Liter", order = 3, dbColumnName = "kr_per_liter", sortable = false, maxValue = 25, format = GenericGridProEditView.CURRENCY_FORMAT_DECIMALS, textAlign =  ColumnTextAlign.END, editorClass = BigDecimalField.class)
    private BigDecimal krPerLiter = BigDecimal.valueOf(0.0);

    @GridEditColumn(header = "Price", order = 4, dbColumnName = "price", fieldLength = 7, maxValue = 9999, format = GenericGridProEditView.CURRENCY_FORMAT, textAlign =  ColumnTextAlign.END, editorClass = IntegerField.class)
    private Integer price = 0;

    @GridEditColumn(header = "Warehouse", order = 5, dbColumnName = "warehouse_id", sortable = false, labelGenerator = "getWarehouseName", editorClass = Select.class)
    @Transient
    private Warehouse warehouse;

    @GridEditColumn(header = "Birthday", order = 6, dbColumnName = "birthday", format = "dd.MM.yyyy", editorClass = DatePicker.class)
    private LocalDate birthday;

    @GridEditColumn(header = "Age", order = 7, sortable = false, textAlign =  ColumnTextAlign.END)
    public int getAge() {
        if (birthday == null) return 0;
        return Period.between(birthday.atStartOfDay().toLocalDate(), LocalDate.now()).getYears();
    }

    @GridEditColumn(header = "Active", order = 8, sortable = false, editorClass = Checkbox.class)
    private Boolean active = false;

    @GridEditColumn(header = "Critical", order = 9, dbColumnName = "criticality", flexGrow = 2, sortable = true, editorClass = TrafficLight.class)
    private String criticality = "Low";

    @GridEditColumn(header = "Description", order = 10, sortable = false, editorClass = RichTextEditor.class)
    private String description = "";

    @GridEditColumn(header = "Year", order = 11, dbColumnName = "yearly_amount", fieldLength = 5, sortable = true, maxValue = 25000, format = GenericGridProEditView.THOUSAND_FORMAT + " kg.", textAlign =  ColumnTextAlign.END, arrayEndIdx = 9, editorClass = ArrayIntegerEditor.class)
    private Integer[] yearlyAmount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};                // Up to 10 years, but can in principle be very long
    public Integer getYearlyAmount(int idx) { return yearlyAmount[idx]; }
    public void setYearlyAmount(int idx, Integer value) { yearlyAmount[idx] = value; }

    // Columns impactAmount and likelihood are paired alternating columns => from a UI perspective the first column of impactAmount is paired with the first column of likelihood etc.
    @GridEditColumn(header = "Impact", order = 12, fieldLength = 5, sortable = false, maxValue = 100000, format = GenericGridProEditView.CURRENCY_FORMAT, textAlign =  ColumnTextAlign.END, arrayEndIdx = 9, alternatingCol = true, editorClass = ArrayIntegerEditor.class)
    private Integer[] impactAmount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};                // Up to 10, but can in principle be very long
    public Integer getImpactAmount(int idx) { return impactAmount[idx]; }
    public void setImpactAmount(int idx, Integer value) { impactAmount[idx] = value; }

    @GridEditColumn(header = "Likelihood %", order = 13, fieldLength = 6, sortable = false, maxValue = 100,  format = GenericGridProEditView.PERCENT_FORMAT_DECIMALS_1, textAlign =  ColumnTextAlign.END, arrayEndIdx = 9, alternatingCol = true, editorClass = ArrayBigDecimalEditor.class)
    private BigDecimal[] likelihood = {BigDecimal.valueOf(0.0) , BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0)};                // Up to 10
    public BigDecimal getLikelihood(int idx) { return likelihood[idx]; }
    public void setLikelihood(int idx, BigDecimal value) { likelihood[idx] = value; }

    @GridEditColumn(header = "Weight", order = 14, sortable = false, format = GenericGridProEditView.CURRENCY_FORMAT_DECIMALS, textAlign =  ColumnTextAlign.END, arrayEndIdx = 9, alternatingCol = true, editorClass = ArrayCalculator.class)
    public BigDecimal getCalculatedImpact(int idx) {
        return  BigDecimal.valueOf(impactAmount[idx]).multiply((likelihood[idx])).divide(BigDecimal.valueOf(100.0));
    }

}


