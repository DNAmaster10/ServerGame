package game.scenes.maingame;

import com.raylib.Raylib;
import game.Window;
import game.objects.Player;
import game.objects.infrastructure.Router;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

import static com.raylib.Raylib.*;

public class Controls {
    public static void handleInputs() throws Exception {
        if (GetMouseX() > Window.properties.windowWidth - Gui.width) {
            if (Gui.toggleRouterSelection.checkClick()) {
                Gui.currentSelectedMenu = 0;
                Player.properties.currentSelectedStructureType = 0;
                Player.properties.currentStructureLevel = 1;
                return;
            }
            else if (Gui.toggleEdgeSelection.checkClick()) {
                Gui.currentSelectedMenu = 1;
                Player.properties.currentSelectedStructureType = 1;
                Player.properties.currentStructureLevel = 1;
                return;
            }
            if (Gui.currentSelectedMenu == 0) {
                for (int i = 0; i < Gui.routerButtons.length; i++) {
                    if(Gui.routerButtons[i].checkClick()) {
                        Player.properties.currentStructureLevel = Gui.routerLevels[i];
                    }
                }
            }
            else {
                for (int i = 0; i < Gui.edgeButtons.length; i++) {
                    if (Gui.edgeButtons[i].checkClick()) {
                        Player.properties.currentStructureLevel = Gui.edgeLevels[i];
                    }
                }
            }
        }
        else {
            //Is the player currently left-clicking?
            if (Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_LEFT)) {
                //If player has "routers" selected
                if (Player.properties.currentSelectedStructureType == 0) {
                    //Then place a router if there isn't already a router present in the given cell
                    Raylib.Vector2 screenToWorldPos = Raylib.GetScreenToWorld2D(Raylib.GetMousePosition(), Player.camera);
                    int gridX = Math.round(screenToWorldPos.x() / MainGame.grid.cellWindowWidth);
                    int gridY = Math.round(screenToWorldPos.y() / MainGame.grid.cellWindowHeight);
                    Construct.placeRouter(Player.properties.currentStructureLevel, gridX, gridY);
                }
                //If the player has "Cables" selected
                else if (Player.properties.currentSelectedStructureType == 1) {
                    Raylib.Vector2 screenToWorldPos = Raylib.GetScreenToWorld2D(Raylib.GetMousePosition(), Player.camera);
                    int gridX = Math.round(screenToWorldPos.x() / MainGame.grid.cellWindowWidth);
                    int gridY = Math.round(screenToWorldPos.y() / MainGame.grid.cellWindowHeight);
                    //If the player is already in the process of placing a cable
                    if (Player.placingCable) {
                        Structure secondStructure = MainGame.getStructureByGridPos(gridX, gridY);
                        //If the player then clicks on nothing, cancel the cable placement
                        if (secondStructure == null) {
                            Player.placingCable = false;
                            Player.firstStructure = -1;
                            return;
                        }
                        //If they click on another structure, check building stuff
                        if (!(secondStructure instanceof Router)) {
                            if (NodeGraph.checkNode(secondStructure.id)) {
                                System.out.println("Connection already made");
                                Player.placingCable = false;
                                Player.firstStructure = -1;
                            }
                        }
                        int secondStructureId = secondStructure.id;
                        System.out.println("Creating cable: " + Player.firstStructure + " " + secondStructureId);
                        Construct.placeCable(Player.properties.currentStructureLevel, Player.firstStructure, secondStructureId);
                        Player.firstStructure = -1;
                        Player.placingCable = false;
                        return;
                    }
                    //If they're not already placing a cable, start a new cable placement
                    else {
                        Structure firstStructure = MainGame.getStructureByGridPos(gridX, gridY);
                        if (firstStructure == null) {
                            return;
                        }
                        if (!(firstStructure instanceof Router)) {
                            if (NodeGraph.checkNode(firstStructure.id)) {
                                System.out.println("Connection already made");
                                return;
                            }
                        }
                        Player.placingCable = true;
                        Player.firstStructure = firstStructure.id;
                        return;
                    }
                }
            }
            else if (Raylib.IsMouseButtonPressed(MOUSE_BUTTON_RIGHT)) {
                if (Player.properties.currentSelectedStructureType == 1) {
                    if (Player.placingCable) {
                        Player.placingCable = false;
                        Player.firstStructure = -1;
                    }
                }
            }
            //Camera
            if (GetMouseWheelMove() != 0) {
                Player.changeZoom(GetMouseWheelMove());
            }
            if (IsKeyPressed(KEY_R)) {
                Player.resetCam();
            }
        }
    }
}
