package game.scenes.maingame;

import game.Window;
import game.objects.ui.Button;

import com.raylib.java.Raylib;

import static com.raylib.java.core.Color.BLACK;
import static com.raylib.java.core.Color.WHITE;
import static game.Window.properties.rl;

public class Gui {
    public static int width = 100;
    public static int height;

    //Indicated whether routers or edges should be displayed in the GUI. 0 is routers, 1 is edges
    public static int currentSelectedMenu = 0;

    public static Button toggleRouterSelection;
    public static Button toggleEdgeSelection;

    public static Button[] routerButtons;
    public static Button[] edgeButtons;

    public static int[] routerLevels;
    public static int [] edgeLevels;

    public static void init() {
        height = Window.properties.windowHeight;

        toggleRouterSelection = new Button(Window.properties.windowWidth - width, 0, width / 2, 15, 1, WHITE, BLACK, "Routers", 2, BLACK);
        toggleEdgeSelection = new Button((Window.properties.windowWidth - width) + width / 2, 0, width / 2, 15, 1, WHITE, BLACK, "Cables", 2, BLACK);

        //Button details
        int buttonWidth = 90;
        int buttonHeight = 15;
        int buttonFontSize = 5;

        //Define the amount of routers and edges in the game
        int totalRouters = 4;
        int totalEdges = 2;

        routerButtons = new Button[totalRouters];
        edgeButtons = new Button[totalEdges];

        routerLevels = new int[totalRouters];
        edgeLevels = new int[totalEdges];

        //Create the buttons
        //First define an array of the router names and id's
        String[] routerNames = new String[totalRouters];

        //Then manually populate here
        routerNames[0] = "Small Router";
        routerLevels[0] = 1;

        routerNames[1] = "Medium Router";
        routerLevels[1] = 2;

        routerNames[2] = "Large Router";
        routerLevels[2] = 3;

        routerNames[3] = "Huge Router";
        routerLevels[3] = 4;

        //Add same for edges when they exist
        String[] edgeNames = new String[totalEdges];

        edgeNames[0] = "Copper Cable";
        edgeLevels[0] = 1;

        edgeNames[1] = "Gold Cable";
        edgeLevels[1] = 2;


        for (int i = 0; i < routerButtons.length; i++) {
            routerButtons[i] = new Button(Window.properties.windowWidth - width + ((width - buttonWidth) / 2), i * (buttonHeight + 2) + 17, buttonWidth, buttonHeight, 1, WHITE, BLACK, routerNames[i], buttonFontSize, BLACK);
        }
        for (int i = 0; i < edgeNames.length; i++) {
            edgeButtons[i] = new Button(Window.properties.windowWidth - width + ((width - buttonWidth) / 2), i * (buttonHeight + 2) + 17, buttonWidth, buttonHeight, 1, WHITE, BLACK, edgeNames[i], buttonFontSize, BLACK);
        }
    }
    public static void draw() {
        //Draw background
        rl.shapes.DrawRectangle(Window.properties.windowWidth - width, 0, width, Window.properties.windowHeight, WHITE);

        //Draw toggle buttons
        toggleRouterSelection.draw();
        toggleEdgeSelection.draw();

        if (currentSelectedMenu == 0) {
            for (Button routerButton : routerButtons) {
                routerButton.draw();
            }
        }
        else if (currentSelectedMenu == 1) {
            for (Button edgeButton : edgeButtons) {
                edgeButton.draw();
            }
        }
    }
}