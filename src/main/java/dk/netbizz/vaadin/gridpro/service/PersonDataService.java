package dk.netbizz.vaadin.gridpro.service;

import dk.netbizz.vaadin.gridpro.entity.Address;
import dk.netbizz.vaadin.gridpro.entity.Cost;
import dk.netbizz.vaadin.gridpro.entity.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonDataService {

    int idx = 1;

    public PersonDataService() {
        // Must be stateless, even in a demo ;-)
    }

    public void resetIdx() {
        idx = 1;
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


    public List<Person> findAll() {
        return List.of(
                new Person(idx++, "Peter Petersen", "Doctor", "pep@company.com", new Address(1, "Elisevej", "3", "4130", "12345678"), ""),
                new Person(idx++, "Lone Lonsen", "Psychologist", "lol@company.com", new Address(2, "Hannavej", "4", "4130", "12345678"), ""),
                new Person(idx++, "Kurt Kurtsen", "Salesman", "kuk@company.com", new Address(3, "Emilsgave", "123", "4130", "12345678"), ""),
                new Person(idx++, "Anne Annesen", "Designer", "ana@company.com", new Address(4, "Hovedgaden", "4", "4130", "12345678"), ""));
    }

    public List<Person> findStaffById(Integer id) {
        if (id > 10) { return new ArrayList<>(); }
        return List.of(
                new Person(idx++, "Hans Hansen", "Doctor", "pep@company.com", new Address(1, "Elisevej", "3", "4130", "12345678"), ""),
                new Person(idx++, "Katrine Katrinsen", "Psychologist", "lol@company.com", new Address(2, "Hannavej", "4", "4130", "12345678"), ""),
                new Person(idx++, "Niels Nielsen", "Salesman", "kuk@company.com", new Address(3, "Emilsgave", "123", "4130", "12345678"), ""),
                new Person(idx++, "Pia Piasen", "Designer", "ana@company.com", new Address(4, "Hovedgaden", "4", "4130", "12345678"), ""));
    }


    public void save(Person entity, List<Person> items) {
        if (entity.getId() == null) {
            Integer newId = items.stream().mapToInt(Person::getId).max().orElse(0) + 1;
            entity.setId(newId);
            items.add(entity);
        } else {
            items.replaceAll(p -> p.getId().equals(entity.getId()) ? entity : p);
        }
    }

    public void delete(Cost entity, List<Cost> entities) {
        entities.removeIf(p -> p.getId().equals(entity.getId()));
    }

}
