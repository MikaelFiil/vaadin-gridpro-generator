# Vaadin GridPro Generator

## A Vaadin GridPro toolkit that converts annotations on your POJO entity class into a GridPro grid for updating
![cover image](https://github.com/MikaelFiil/vaadin-gridpro-generator/blob/main/cover.png?raw=true)

### Version history
* ### Version 0.20 - 2024-11-23  - Initial commit
* ### Version 0.21 - 2024-11-23  - Updated editor types with BigDecimal, improved input handling of numbers. Handling of validation- and error messages relayed to domain view class. Bindings on main class GenericGridProEditView are now at a minimum.
* ### Version 0.22 - 2024-11-24  - Added new columns type Trafficlight aka Radiobutton Group
* ### Version 0.23 - 2024-11-25  - Moved creation of new entity out of generic code and into the domain specific subclass, small text improvements in Trafficlight, simplified columns creation in Generic class, fiddled with Grid layout
* ### Version 0.24 - 2024-11-26  - Removed state (item list) from the service as it should be, even for a demo :-) added the possibility to have alternating array columns (see demo to understand).
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
***
### There is room for improvement and I will probably do some myself, since I have only started to use it.
* ### More field types
* ### Having a more dynamic set of parameters for all types of fields
* ### More validation options 
* ### Cleaning up the code, maybe further improve exception handling with lambda wrappers
* ### More examples utilizing it for complex UI interactions with one-to-many relations etc.
* ### Tests
* ### Better readme ;-) 
* ### ... Oh yes, remove any bugs not yet found :-)
###
###  Notice that GridPro requires a commercial license from Vaadin
###

### Based on:
### - Spring Boot 3.3.5
### - Vaadin Flow 24.5.5

## Annotations

### Custom Annotation: GridEditColumn
### Use this annotation to let the GenericView auto-render your Grid. Use the annotaion properties to configure the behavior of each column. 

```JAVA
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GridEditColumn {
   String header() default "";
   int order() default 999;
   boolean sortable() default true;
   String format() default "";     // Is mandatory for dates
   java.lang.Class editorClass() default TextField.class;
   int fieldLength() default 50;
   double minValue() default 0;
   double maxValue() default 1999999999;
   int arrayEndIdx() default 0;         // Values from 0 to size of array
}
```

## Code
### The tool specific files are in the two packages
###  - package dk.netbizz.vaadin.gridpro.entity.base
###  - package dk.netbizz.vaadin.gridpro.utils;
### 

## Demo
### This demo provides one entity type, one view and one service:
### - Item.java
### - ItemView.java
### - ItemDataService.java
