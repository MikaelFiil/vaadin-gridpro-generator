package dk.netbizz.vaadin.secutiry.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSecurityRole implements Serializable {
    private Integer userId; // Foreign Key to ApplicationUser
    private SecurityRole role;

    @Override
    public String toString() {
        return "UserRole{" +
                "userId=" + userId +
                ", role='" + role + '\'' +
                '}';
    }

}
