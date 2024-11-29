package dk.netbizz.vaadin.gridpro.service;

import dk.netbizz.vaadin.gridpro.entity.Cost;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CostDataService {

    @Getter
    private List<Cost> entityList = new ArrayList<>();     // DB

    public CostDataService() {
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

    public List<Cost> findAll(Integer id) {

        if (id == null) return new ArrayList<>();

            switch (id) {
                case 1,5,9:
                    return List.of(
                            new Cost(1, "Speed", "Core solution", "Technical", 13500, true),
                            new Cost(2, "Stability", "Core solution", "Delivery", 12500, true));

                case 2,6,10:
                    return List.of(
                            new Cost(3, "Consumption", "Core option", "Quality", 11500, true),
                            new Cost(4, "Speed", "Core option", "Technical", 10500, true));

                case 3,7,11:
                    return List.of(
                            new Cost(5, "Hardware", "Core solution", "Technical", 9500, true),
                            new Cost(6, "Software", "Core solution", "Delivery", 23500, true));

                case 4,8,12:
                    return List.of(
                            new Cost(7, "Network", "Core option", "Quality", 15500, true),
                            new Cost(8, "Management", "Core option", "Technical", 20500, true));

                default:
                    return new ArrayList<>();
            }

    }

    public void save(Cost entity) {
        if (entity.getId() == null) {
            Integer newId = entityList.stream().mapToInt(Cost::getId).max().orElse(0) + 1;
            entity.setId(newId);
            entityList.add(entity);
        } else {
            entityList.replaceAll(p -> p.getId().equals(entity.getId()) ? entity : p);
        }
    }

    public void delete(Cost entity) {
        entityList.removeIf(p -> p.getId().equals(entity.getId()));
    }

}
