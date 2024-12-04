package dk.netbizz.vaadin.gridpro.entity;

import dk.netbizz.vaadin.gridpro.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Warehouse implements BaseEntity {

    @EqualsAndHashCode.Include
    private Integer id;

    String warehouseName;
    String street;
    String city;
    Integer sqrM2;

    @Override
    public String toString() {
        return warehouseName + " - " + sqrM2 + " m2";
    }

}
