package dk.netbizz.vaadin.gridpro.service;

import dk.netbizz.vaadin.gridpro.entity.Item;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class ItemDataService {

    @Getter
    private List<Item> itemList;                                // my DB :-)

    public List<String> getItemsForSelect(String colName) {
        List<String> list = new ArrayList<>();

        switch(colName.toLowerCase()) {
            case "category":
                list.add("Technical");
                list.add("Quality");
                list.add("Delivery");
                list.add("Legal");
            break;
        }
        return list;
    }

    public ItemDataService() {
        //Should be stateless, but itemList is my DB
        itemList = findAll();
    }

    public List<Item> findAll() {
        return new ArrayList<Item>(List.of(
                new Item(1L, "Swimsuit", "Technical", 120, LocalDate.of(1962, 2, 25), true, "Medium"),
                new Item(2L, "Skates", "Quality", 1100, LocalDate.of(1983, 11, 2), false, "Low"),
                new Item(3L, "MTB", "Delivery", 9495, LocalDate.of(1988, 12, 6), true, "High"),
                new Item(4L, "Volleyball", "Legal", 150, LocalDate.of(1997, 5, 3), true, "Low")));
    }

    public void save(Item item) {
        if (item.getItemId() == null) {
            Long newId = itemList.stream().mapToLong(Item::getItemId).max().orElse(0L) + 1;
            item.setItemId(newId);
            itemList.add(item);
        } else {
            itemList.replaceAll(p -> p.getItemId().equals(item.getItemId()) ? item : p);
        }
    }

    public void delete(Item item) {
        itemList.removeIf(p -> p.getItemId().equals(item.getItemId()));
    }

}
