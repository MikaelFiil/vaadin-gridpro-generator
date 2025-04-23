package dk.netbizz.vaadin.tenantcompany.domain;

import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.BaseEntity;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GridEditColumn;
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
public class TenantCompany implements BaseEntity, Serializable {

    @GridEditColumn(header = "Id", order = 0)
    @EqualsAndHashCode.Include
    @Id
    private Integer id;

    @GridEditColumn(header = "Company name", order = 1, fieldLength = 50)
    private String companyName;

    @GridEditColumn(header = "Street address", order = 2, fieldLength = 50)
    private String addressStreet;

    @GridEditColumn(header = "Zip code and City", order = 3, fieldLength = 50)
    private String addressZipCity;

    @Transient
    private List<TenantDepartment> departments = new ArrayList<>();

    @Version
    private Integer version;

    @Override
    public String toString() {
        return "TenantCompany{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", addressStreet='" + addressStreet + '\'' +
                ", addressZipCity='" + addressZipCity + '\'' +
                '}';
    }
}
