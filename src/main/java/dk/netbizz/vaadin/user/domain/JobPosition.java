package dk.netbizz.vaadin.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPosition implements Serializable {
    private Integer id;
    private String jobPositionName;
    private Boolean resourceManager;
    private Boolean executiveSponsor;
    private String description;

    @Override
    public String toString() {
        return "JobPosition{" +
                "id=" + id +
                ", jobPositionName='" + jobPositionName + '\'' +
                ", resourceManager=" + resourceManager +
                ", executiveSponsor=" + executiveSponsor +
                ", description='" + description + '\'' +
                '}';
    }

}
