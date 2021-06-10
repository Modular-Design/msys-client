package msys.client.stages.tools.management;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.HashMap;
import java.util.Map;

public class ProjectManager extends VisualElement {
    private MenuBar root = new MenuBar();
    private Menu fileMenu = new Menu("_File");
    private MenuItem open = new MenuItem("Open");
    private MenuItem close = new MenuItem("Close");
    private MenuItem save = new MenuItem("Save");
    private MenuItem exit = new MenuItem("Exit");

    //Create the Options menu.
    private Menu optionsMenu = new Menu("_Options");
    // Create the Colors submenu.
    private Menu colorsMenu = new Menu("Colors");
    private MenuItem red = new MenuItem("Red");
    private MenuItem green = new MenuItem("Green");
    private MenuItem blue = new MenuItem("Blue");

    private Menu helpMenu = new Menu("_Help");
    private MenuItem about = new MenuItem("About");

    public ProjectManager() {
        super(0, 4);
        // Add keyboard accelerators for the File menu.
        open.setAccelerator(KeyCombination.keyCombination("shortcut+O"));
        close.setAccelerator(KeyCombination.keyCombination("shortcut+C"));
        save.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
        exit.setAccelerator(KeyCombination.keyCombination("shortcut+E"));

        fileMenu.getItems().addAll(open, close, save, new SeparatorMenuItem(), exit);

        //Add File menu to the menu bar.
        root.getMenus().add(fileMenu);

        colorsMenu.getItems().addAll(red, green, blue);
        optionsMenu.getItems().add(colorsMenu);


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
        root.getMenus().add(optionsMenu);


        //Create the Help menu.

        helpMenu.getItems().add(about);


        //Add Help menu to the menu bar.
        root.getMenus().add(helpMenu);


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


        Map<String, Object> map = new HashMap<>();
        map.put("url","/extensions/");
        publishEvent("Client", 0,Events.GET, map);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        if (receiver != null){
            if (receiver.equals("ProjectManager")){
                processGUIEvent(sender, event, msg);
            }
        }
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {

    }

    @Override
    public Node getVisual() {
        return root;
    }
}
