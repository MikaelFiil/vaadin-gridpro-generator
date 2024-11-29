package dk.netbizz.vaadin.gridpro.service;

import dk.netbizz.vaadin.gridpro.entity.Template;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateDataService {

    ResourceDataService resourceDataService;
    CostDataService costDataService;
    RiskDataService riskDataService;

    public TemplateDataService(ResourceDataService resourceDataService, CostDataService costDataService, RiskDataService riskDataService) {
        this.resourceDataService = resourceDataService;
        this.costDataService = costDataService;
        this.riskDataService = riskDataService;
    }

    public List<Template> findAll() {
        List<Template> templates = List.of(
            new Template(1, "ATEA NAS II", 20,  "Simple bid ", true),
            new Template(2, "ATEA Network",  18,  "Complex bid", true),
            new Template(3, "ATEA 5G Data", 15,  "Simple bid", true),
            new Template(4, "ATEA PC Standard I", 20,  "Normal bid", true));

        for(Template template : templates) {
            template.setResources(resourceDataService.findAll(template.getId()));
            template.setCosts(costDataService.findAll(template.getId()));
            template.setRisks(riskDataService.findAll(template.getId()));
        }

        return templates;

    }

    public void save(Template entity, List<Template> items) {
        if (entity.getId() == null) {
            Integer newId = items.stream().mapToInt(Template::getId).max().orElse(0) + 1;
            entity.setId(newId);
            items.add(entity);
        } else {
            items.replaceAll(p -> p.getId().equals(entity.getId()) ? entity : p);
        }
    }

    public void delete(Template entity, List<Template> entities) {
        entities.removeIf(p -> p.getId().equals(entity.getId()));
    }

}
