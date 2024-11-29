package dk.netbizz.vaadin.gridpro.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {

    @EqualsAndHashCode.Include
    Integer id;
    String streetAddress;
    String houseNumber;
    String zipcode;
    String phone;
}
