package dk.netbizz.vaadin.gridpro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class TenantDepartment implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String tenantId;
    private Integer tenantCompanyId;
    private String departmentName = "";

    // @ManyToOne
    // private TenantCompany company;

    @Override
    public String toString() {
        return departmentName;
    }

}
