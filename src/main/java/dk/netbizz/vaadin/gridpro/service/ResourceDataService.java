package dk.netbizz.vaadin.gridpro.service;

import dk.netbizz.vaadin.gridpro.entity.Resource;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceDataService {

    @Getter
    private List<Resource> entityList = new ArrayList<>();     // DB

    public ResourceDataService() {
    }

    public void setData(List<Resource> entityList) {
        this.entityList = entityList;
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

            case "department":
                list.add("Sales");
                list.add("Consulting");
                list.add("Legal");
                list.add("Network");
                break;

            case "jobposition":
                list.add("CEO");
                list.add("CTO");
                list.add("SW Developer");
                list.add("Legal specialist");
                list.add("Bid Manager");
                list.add("Subject matter expert");
                break;
        }

        return list;
    }

    public List<Resource> findAll(Integer id) {

        if (id == null) return new ArrayList<>();

        switch (id) {
            case 1:
            return List.of(
                    new Resource(1, "Technical", "Legal", "Legal specialist", 6, "", true),
                    new Resource(2, "Delivery", "Sales", "Subject matter expert", 8, "", true));
            case 2:
                return List.of(
                        new Resource(3, "Quality", "Consulting", "Bid Manager", 4, "", true),
                        new Resource(4, "Technical", "Network", "Subject matter expert", 4, "", true));
            default:
                return new ArrayList<>();
        }

    }

    public void save(Resource entity) {
        if (entity.getId() == null) {
            Integer newId = entityList.stream().mapToInt(Resource::getId).max().orElse(0) + 1;
            entity.setId(newId);
            entityList.add(entity);
        } else {
            entityList.replaceAll(p -> p.getId().equals(entity.getId()) ? entity : p);
        }
    }

    public void delete(Resource entity) {
        entityList.removeIf(p -> p.getId().equals(entity.getId()));
    }

}
