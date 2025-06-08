package dk.netbizz.vaadin.tenantcompany.domain;

import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.BaseEntity;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GridEditColumn;
import dk.netbizz.vaadin.user.domain.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.io.Serializable;
import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDepartment implements BaseEntity, Serializable {


    public TenantDepartment(String departmentName, String description) {
        this.departmentName = departmentName;
        this.description = description;
    }

    @GridEditColumn(header = "Id", order = 0)
    @EqualsAndHashCode.Include
    @Id
    private Integer id;

    private Integer tenantCompanyId;                    // Foreign Key to TenantCompany

    @GridEditColumn(header = "Department name", order = 1, fieldLength = 50)
    private String departmentName;

    @GridEditColumn(header = "Description", order = 2, fieldLength = 50)
    private String description;

    @MappedCollection(idColumn = "TENANT_DEPARTMENT_ID", keyColumn = "ID")
    private Set<ApplicationUser> employees;

    @Version
    private Integer version;

    @Override
    public String toString() {
        return "TenantDepartment{" +
                "id=" + id +
                ", tenantCompanyId=" + tenantCompanyId +
                ", departmentName='" + departmentName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
