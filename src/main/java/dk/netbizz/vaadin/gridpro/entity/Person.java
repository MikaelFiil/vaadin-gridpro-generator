package dk.netbizz.vaadin.gridpro.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {

    @EqualsAndHashCode.Include
    Integer id;


    String fullName;
    String profession;
    String email;
    Address address;
    String pictureUrl;
}
