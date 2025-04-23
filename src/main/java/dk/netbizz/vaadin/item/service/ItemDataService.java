package dk.netbizz.vaadin.item.service;

import dk.netbizz.vaadin.item.domain.Item;
import dk.netbizz.vaadin.warehouse.domain.Warehouse;
import dk.netbizz.vaadin.warehouse.service.WarehouseDataService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



/**
 * NON DB version of service for simple demo
 */

@Service
public class ItemDataService {

    @Getter
    private List<Item> itemList;                                // my DB :-)
    private WarehouseDataService warehouseDataService;

    public <S>List<S> getItemsForSelect(String colName) {
        List<String> list = new ArrayList<>();

        switch(colName.toLowerCase()) {
            case "category":
                list.add("Technical");
                list.add("Quality");
                list.add("Delivery");
                list.add("Legal");

            break;
            case "warehouse":
                return (List<S>) warehouseDataService.findAll();
        }
        return (List<S>) list;
    }

    public ItemDataService(WarehouseDataService warehouseDataService) {
        this.warehouseDataService = warehouseDataService;
        //Should be stateless, but itemList is my DB
        itemList = findAll();
    }

    public List<Item> findAll() {
        List<Warehouse> warehouseList = warehouseDataService.findAll();
        return new ArrayList<Item>(List.of(
                new Item(1, "Swimsuit", "Technical", 120, warehouseList.get(0), LocalDate.of(1962, 2, 25), true, "Medium"),
                new Item(2, "Skates", "Quality", 1100, warehouseList.get(1), LocalDate.of(1983, 11, 2), false, "Low"),
                new Item(3, "MTB", "Delivery", 9495, warehouseList.get(2), LocalDate.of(1988, 12, 6), true, "High"),
                new Item(4, "Volleyball", "Legal", 150, warehouseList.get(3), LocalDate.of(1997, 5, 3), true, "Low")));
    }

    public void save(Item item) {
        if (item.getId() == null) {
            Integer newId = itemList.stream().mapToInt(Item::getId).max().orElse(0) + 1;
            item.setId(newId);
            itemList.add(item);
        } else {
            itemList.replaceAll(p -> p.getId().equals(item.getId()) ? item : p);
        }
    }

    public void delete(Item item) {
        itemList.removeIf(p -> p.getId().equals(item.getId()));
    }

}
