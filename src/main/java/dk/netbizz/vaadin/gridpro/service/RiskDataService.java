package dk.netbizz.vaadin.gridpro.service;

import dk.netbizz.vaadin.gridpro.entity.Risk;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class RiskDataService {

    @Getter
    private List<Risk> entityList = new ArrayList<>();     // DB

    public RiskDataService() {
    }

    public List<String> getItemsForSelect(String colName) {
        List<String> list = new ArrayList<>();

        switch(colName.toLowerCase()) {
            case "category":
                list.add("Technical");
                list.add("Quality");
                list.add("Delivery");
                list.add("Legal");
                break;

            case "commercialtype":
                list.add("Core solution");
                list.add("Core option");
                // list.add("Upside");
                break;
        }
        return list;
    }

    public List<Risk> findAll(Integer id) {

        if (id == null) return new ArrayList<>();

        switch(id) {
            case 1:
                return List.of(
                    new Risk(1, "Speed", "Core solution", "Technical", 1300, new BigDecimal(20), true),
                    new Risk(2, "Stability", "Core solution", "Delivery", 1200, new BigDecimal(20), true));

            case 2:
                return List.of(
                        new Risk(3, "Consumption", "Core option", "Quality", 1100, new BigDecimal(20), true),
                        new Risk(4, "Speed", "Core option", "Technical", 1000, new BigDecimal(20), true));

            default:
                return new ArrayList<>();
        }
    }

    public void save(Risk entity) {
        if (entity.getId() == null) {
            Integer newId = entityList.stream().mapToInt(Risk::getId).max().orElse(0) + 1;
            entity.setId(newId);
            entityList.add(entity);
        } else {
            entityList.replaceAll(p -> p.getId().equals(entity.getId()) ? entity : p);
        }
    }

    public void delete(Risk entity) {
        entityList.removeIf(p -> p.getId().equals(entity.getId()));
    }

}
