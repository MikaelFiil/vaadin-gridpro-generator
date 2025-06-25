package dk.netbizz.vaadin.user.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.datepicker.DatePicker;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.BaseEntity;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GridEditColumn;
import dk.netbizz.vaadin.item.domain.Item;
import dk.netbizz.vaadin.secutiry.domain.SecurityRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUser implements BaseEntity, Serializable {

    @GridEditColumn(header = "Id", dbColumnName = "id", order = 0)
    @EqualsAndHashCode.Include
    @Id
    private Integer id;

    private Integer tenantDepartmentId;                     // Foreign Key to TenantDepartment

    @JsonIgnore
    private String hashedPassword = "";

    private Boolean mustChangePwd = false;

    @GridEditColumn(header = "Full name", order = 1, dbColumnName = "fullname", fieldLength = 50)
    private String fullname = "";

    @GridEditColumn(header = "Email", order = 2, dbColumnName = "email", fieldLength = 150)
    private String email = "";

    private Boolean emailConfirmed = false;
    private String emailConfirmationString = "";

    @GridEditColumn(header = "Birthday", order = 3, dbColumnName = "birthday", format = "dd.MM.yyyy", editorClass = DatePicker.class)
    private LocalDate birthday;

    @GridEditColumn(header = "Phone", order = 4, dbColumnName = "phone", fieldLength = 15)
    private String phone = "";

    private LocalDateTime created;
    private LocalDateTime lastLogin;
    private Boolean isLocked = false;                       //  used for spring security  boolean isAccountNonLocked();
    private Boolean isDisabled = false;                     //  when an employee is terminated
    private String description = "";                        // Max 250 characters
    private byte[] picture;                                 // bytea in database column

    @Transient
    private List<Item> items = new ArrayList<>();

    @Version
    private Integer version;

    @Transient
    private Set<SecurityRole> securityRoles;                // Used by Spring security

    @Override
    public String toString() {
        return this.fullname;
    }
}
