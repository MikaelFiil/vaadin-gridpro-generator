# Vaadin GridPro Generator

## A Vaadin GridPro toolkit that converts annotations on your POJO entity class into a GridPro grid for updating
![cover image](https://github.com/MikaelFiil/vaadin-gridpro-generator/blob/main/cover.png?raw=true)

### Version history
* ### Version 0.20 - 2024-11-23  - Initial commit
* ### Version 0.21 - 2024-11-23  - Updated editor types with BigDecimal, improved input handling of numbers. Handling of validation- and error messages relayed to domain view class. Bindings on main class GenericGridProEditView are now at a minimum.
* ### Version 0.22 - 2024-11-24  - Added new columns type Trafficlight aka Radiobutton Group
* ### Version 0.23 - 2024-11-25  - Moved creation of new entity out of generic code and into the domain specific subclass, small text improvements in Trafficlight, simplified columns creation in Generic class, fiddled with Grid layout
* ### Version 0.24 - 2024-11-27  - Removed state (item list) from the service as it should be, even for a demo :-) added the possibility to have one set of alternating array columns in the end (see demo to understand). Changes to main loop. Added TextAlign as annotation.
* ### Version 0.25 - 2024-11-29  - Ups, had an immutable list, SORRY - fixed. Created a Icon(VaadinIcon.PLUS) as header of delete column and made it add a new row, removed gridContainer and old add button  - there is some very experimental TreeGrid code too ...
* ### Version 0.26 - 2024-11-30  - UI improvements and code cleanup
* ### Version 0.27 - 2024-12-04  - Support for embedded entities via the Select editor class. Take a look at the Warehouse in Item
* ### Version 0.28 - 2024-12-05  - Bug fix - Removed constant recalculation of column widths, it interfered with automatic horizontal scrolling when the grid is wider than view port. 
* ### Version 0.29 - 2024-12-19  - Added the possibility to hide specific columns by adding e.g. -  params.put("price.hidden", ""); - to hide the price column  
* ### Version 0.30 - 2025-04-23  - Major changes to code structure and with enhancements to central class GenericGridProEditView. Added some performance tests behind the menu "Load and performance test" using H2 memory database.
* ### Version 0.31 - 2025-04-26  - Many small improvement in both code and UI. Testing load and performance shows production readiness 
* ### Version 0.40 - 2025-06-25  - Major changes, including some breaking changes. Now it uses Spring Data JDBC in combination with paging, sorting and filtering in the "Load & performance test" menu. Code has been simplified, especially for Readonly situations. Formatting of cells has changed, see examples. Utilizing the Vaadin 24.8 new Signal concept to keep the 1-N relations updated    

### Author Mikael Fiil - mikael.fiil@netbizz.dk
###

*** 
### This is given to the public domain and without any warranty
### Original idea of using annotations for generating the Vaadin UI by Andreas Lange
### https://github.com/andrlange/vaadin-grid-form-entities
###
### This is a GridPro extension of the idea of using annotations on your POJO to simplify setting up your Vaadin UI.
### Besides the most common field types, there is a special option for editing variable length array fields, which is actually why I started to make this, since it avoids having a lot of repetitive application code.
### Since a user can move freely around in the grid, you can never know when editing is finished, hence there is instant persistence.
### Every edit of a single cell will cause a call to save the entity of the row.
### The application domain view must extend this class and replace the generic placeholder with the domain class for the grid and implement the abstract methods.
### Logning of validation and error messages refer to the domain view.
### When using the Load and Performance test, the H2 console is at http://localhost:8090/h2-console  - credentials: sa/password - try these:  select count(*) from tenant_company;   select count(*) from tenant_department;  select count(*) from application_user;  select count(*) from item;

***
### There is room for some improvement and I will probably still do some myself, but I basically considers it ready for production.
* ### More field types or generic field types for the arrays - DONE
* ### More validation options - Perhaps refer to domain view - DONE
* ### Cleaning up the code. Move editors into separate classes and perhaps use interfaces with generics for simplified extension  - Partly done
* ### More examples utilizing it for complex UI interactions with one-to-many relations etc. - DONE
* ### Tests
* ### Better readme ;-) 
* ### ... Oh yes, remove any bugs not yet found :-)
###
###  Notice that GridPro requires a commercial license from Vaadin
###

### Based on:
### - Spring Boot 3.5.1
### - Vaadin Flow 24.8.0
### - Java 21

## Annotations

### Custom Annotation: GridEditColumn
### Use this annotation to let the GenericView auto-render your Grid. Use the annotation properties to configure the behavior of each column. 

```JAVA
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GridEditColumn {
    String header() default "";
    String dbColumnName() default "";
    int order() default 999;
    boolean sortable() default true;
    String format() default "";     // Is mandatory for dates
    Class<?> editorClass() default TextField.class;
    String labelGenerator() default "";
    int fieldLength() default 50;
    int flexGrow() default 1;
    double minValue() default 0;
    double maxValue() default 1999999999;
    ColumnTextAlign textAlign() default ColumnTextAlign.START;
    boolean autoWidth() default true;
    boolean resizable() default true;
    int arrayEndIdx() default 0;         // Values from 0 to size of array
    boolean alternatingCol() default false;         // If there are multiple array columns they may alternate in the grid
}
```

## Code
### The tool specific files are in the packages
###  - dk.netbizz.vaadin.gridpro.utils;
### 

## Demos and experiments
### This "GridPro inline" menu demo provides one entity type, one view and one pseudo service
### - Item.java
### - ItemView.java
### - ItemDataService.java
###
### The "Load & performance test" menu demo shows a working multi hierarchy (1 -> N) relations example using Spring Data JDBC DataProvider.fromFilteringCallbacks together with the paging operations of the GridPro grids.
### The grids have some sorting and filtering options that goes all the way to the database for memory efficiency.
### The demo can use either H2 memory DB or PostgreSQL, consult the application.properties AND ItemRowMapper.java when choosing the DB
### Notice the schema.sql and data.sql files in the "resources" directory, only the "tenant_company" and "warehouse" tables are populated, the rest requires use of the "Generate ..." buttons at the top of the view.
### 
### Other menus are just experimental or a small helper like the "Warehouse"