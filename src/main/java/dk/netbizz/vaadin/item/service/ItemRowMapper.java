package dk.netbizz.vaadin.item.service;

import dk.netbizz.vaadin.item.domain.Item;
import dk.netbizz.vaadin.warehouse.domain.Warehouse;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemRowMapper implements RowMapper<Item> {


    private ItemRowMapper() {}
    private static final ItemRowMapper INSTANCE = new ItemRowMapper();
    List<Warehouse> warehouseList;

    public static ItemRowMapper getInstance() {
        return INSTANCE;
    }


    @Override
    public Item mapRow (ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));

        item.setApplicationUserId(rs.getInt("application_user_id"));
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setKrPerLiter(rs.getBigDecimal("kr_per_liter"));
        item.setPrice(rs.getInt("price"));
        Integer warehouseId = rs.getInt("warehouse_id");
        if (rs.wasNull()) { warehouseId = null; }
        item.setWarehouse(getWarehouse(warehouseId));

        item.setBirthday((rs.getDate("birthday") != null) ? rs.getDate("birthday").toLocalDate() : null);
        item.setActive(rs.getBoolean("active"));
        item.setCriticality(rs.getString("criticality"));
        item.setDescription(rs.getString("description"));

        // Following is H2 specific
        // item.setYearlyAmount((Integer[]) rs.getObject("yearly_amount", Integer[].class));  //  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};                // Up to 10 years, but can in principle be very long
        // item.setImpactAmount((Integer[]) rs.getObject("impact_amount", Integer[].class));  //  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};                // Up to 10 years, but can in principle be very long
        // item.setLikelihood((BigDecimal[]) rs.getObject("likelihood", BigDecimal[].class));    //  = {BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0),
        // Following is PostgreSQL specific
        item.setYearlyAmount((Integer[]) rs.getArray("yearly_amount").getArray());  //  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};                // Up to 10 years, but can in principle be very long
        item.setImpactAmount((Integer[]) rs.getArray("impact_amount").getArray());  //  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};                // Up to 10 years, but can in principle be very long
        item.setLikelihood((BigDecimal[]) rs.getArray("likelihood").getArray());    //  = {BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0),

        item.setVersion(rs.getInt("version"));
        if (rs.wasNull()) {
            item.setVersion(null);
        }
        return item;
    }

    public ItemRowMapper setWareHouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
        return this;
    }

    private Warehouse getWarehouse(Integer warehouseId) {
        for (Warehouse warehouse : warehouseList) {
            if (warehouse.getId().equals(warehouseId)) return warehouse;
        }
        return null;
    }
}
