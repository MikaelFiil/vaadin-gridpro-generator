package dk.netbizz.vaadin.gridpro.service;

import dk.netbizz.vaadin.gridpro.entity.Item;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemDataService {

    private final List<Item> items = new ArrayList<>();

    public List<String> getItemsForSelect(String colName) {
        List<String> list = new ArrayList<>();

        switch(colName) {
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
        // Integer[] yearlyAmount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        items.addAll(List.of(
                new Item(1L, "Swimsuit", "Technical", 120, LocalDate.of(1962, 2, 25), true),
                new Item(2L, "Skates", "Quality", 1100, LocalDate.of(1983, 11, 2), false),
                new Item(3L, "MTB", "Delivery", 10495, LocalDate.of(1988, 12, 6), true),
                new Item(4L, "Volleyball", "Legal", 150, LocalDate.of(1997, 5, 3), true)));
    }


    public List<Item> findAll() {
        return items;
    }

    public void save(Item item) {
        if (item.getItemId() == null) {
            Long newId = items.stream().mapToLong(Item::getItemId).max().orElse(0L) + 1;
            item.setItemId(newId);
            items.add(item);
        } else {
            items.replaceAll(p -> p.getItemId().equals(item.getItemId()) ? item : p);
        }
    }

    public void delete(Item item) {
        items.removeIf(p -> p.getItemId().equals(item.getItemId()));
    }

}
