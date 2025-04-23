package dk.netbizz.vaadin.warehouse.service;

import dk.netbizz.vaadin.item.domain.Item;
import dk.netbizz.vaadin.warehouse.domain.Warehouse;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * NON DB version of service for simple demo
 */

@Service
public class WarehouseDataService {

    @Getter
    private List<Warehouse> warehouseList;                                // my DB :-)

    public WarehouseDataService() {
        //Should be stateless, but itemList is my DB
        warehouseList = findAll();
    }


    public List<Warehouse> findAll() {
        return new ArrayList<Warehouse>(List.of(
                new Warehouse(1, "Bilka", "Bilkavej 1", "Ishøj", 4000, null),
                new Warehouse(2, "Elgiganten", "Industrivej 17", "Roskilde", 3000, null),
                new Warehouse(3, "Power", "Roskildevej 166", "Glostrup", 3300, null),
                new Warehouse(4, "Bauhaus", "Bauhaus plads", "Roskilde", 6000, null)));
    }

    public void save(Warehouse warehouse) {
        if (warehouse.getId() == null) {
            Integer newId = warehouseList.stream().mapToInt(Warehouse::getId).max().orElse(0) + 1;
            warehouse.setId(newId);
            warehouseList.add(warehouse);
        } else {
            warehouseList.replaceAll(p -> p.getId().equals(warehouse.getId()) ? warehouse : p);
        }
    }

    public void delete(Item item) {
        warehouseList.removeIf(p -> p.getId().equals(item.getId()));
    }

}
