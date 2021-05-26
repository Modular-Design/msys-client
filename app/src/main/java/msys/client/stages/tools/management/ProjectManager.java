package msys.client.stages.tools.management;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class ProjectManager extends MenuBar {
    public ProjectManager() {
        Menu fileMenu = new Menu("_File");

        MenuItem open = new MenuItem("Open");
        MenuItem close = new MenuItem("Close");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");

        // Add keyboard accelerators for the File menu.
        open.setAccelerator(KeyCombination.keyCombination("shortcut+O"));
        close.setAccelerator(KeyCombination.keyCombination("shortcut+C"));
        save.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
        exit.setAccelerator(KeyCombination.keyCombination("shortcut+E"));

        fileMenu.getItems().addAll(open, close, save, new SeparatorMenuItem(), exit);

        //Add File menu to the menu bar.
        getMenus().add(fileMenu);


        //Create the Options menu.
        Menu optionsMenu = new Menu("_Options");
        // Create the Colors submenu.
        Menu colorsMenu = new Menu("Colors");
        MenuItem red = new MenuItem("Red");
        MenuItem green = new MenuItem("Green");
        MenuItem blue = new MenuItem("Blue");


        colorsMenu.getItems().

                addAll(red, green, blue);

        optionsMenu.getItems().

                add(colorsMenu);


// Create the Priority submenu.


        Menu priorityMenu = new Menu("Priority");

        MenuItem high = new MenuItem("High");

        MenuItem low = new MenuItem("Low");

        priorityMenu.getItems().

                addAll(high, low);

        optionsMenu.getItems().

                add(priorityMenu);


// Add a separator.


        optionsMenu.getItems().add(new SeparatorMenuItem());


        //Create the Reset menu item.

        MenuItem reset = new MenuItem("Reset");

        optionsMenu.getItems().

                add(reset);


        //Add Options menu to the menu bar.


        getMenus().add(optionsMenu);


        //Create the Help menu.


        Menu helpMenu = new Menu("_Help");

        MenuItem about = new MenuItem("About");
        helpMenu.getItems().

                add(about);


        //Add Help menu to the menu bar.

        getMenus().add(helpMenu);


        //Create one event handler that will handle menu action events.


        EventHandler<ActionEvent> MEHandler =


                ae -> {


                    String name = ((MenuItem) ae.getTarget()).getText();


// If Exit is chosen, the program is terminated.

                    if (name.equals("Exit")) Platform.exit();


                };


        // Set action event handlers for the menu items.

        open.setOnAction(MEHandler);
        close.setOnAction(MEHandler);

        save.setOnAction(MEHandler);
        exit.setOnAction(MEHandler);

        red.setOnAction(MEHandler);
        green.setOnAction(MEHandler);

        blue.setOnAction(MEHandler);
        high.setOnAction(MEHandler);

        low.setOnAction(MEHandler);
        reset.setOnAction(MEHandler);

        about.setOnAction(MEHandler);
    }
}
