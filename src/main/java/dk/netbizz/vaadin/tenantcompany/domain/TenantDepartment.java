package dk.netbizz.vaadin.tenantcompany.domain;

import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.BaseEntity;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GridEditColumn;
import dk.netbizz.vaadin.user.domain.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDepartment implements BaseEntity, Serializable {


    public TenantDepartment(String departmentName, String description) {
        this.departmentName = departmentName;
        this.description = description;
    }

    @GridEditColumn(header = "Id", dbColumnName = "id", order = 0)
    @EqualsAndHashCode.Include
    @Id
    private Integer id;
    @Version
    private Integer version = null;

    private Integer tenantCompanyId;                    // Foreign Key to TenantCompany

    @GridEditColumn(header = "Department name", dbColumnName = "department_name", order = 1, fieldLength = 50)
    private String departmentName;

    @GridEditColumn(header = "Description", dbColumnName = "description", order = 2, fieldLength = 50)
    private String description;

    @Transient
    private List<ApplicationUser> employees = new ArrayList<>();

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
