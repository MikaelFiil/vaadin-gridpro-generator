package dk.netbizz.vaadin.item.service;

import dk.netbizz.vaadin.item.domain.Item;
import dk.netbizz.vaadin.service.ServicePoint;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemRowMapper implements RowMapper<Item> {


    private ItemRowMapper() {}

    private static final ItemRowMapper INSTANCE = new ItemRowMapper();

    public static ItemRowMapper getInstance() {
        return INSTANCE;
    }


    @Override
    public Item mapRow (ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));

        item.setApplicationUserId(rs.getInt("application_user_id"));
        item.setItemName("A " + rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setKrPerLiter(rs.getBigDecimal("kr_per_liter"));
        item.setPrice(rs.getInt("price"));
        item.setWarehouse(ServicePoint.getInstance().getWarehouseRepository().findById(rs.getInt("warehouse_id")).orElse(null));

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
        return item;
    };


}
