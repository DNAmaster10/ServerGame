package game.scenes.maingame;

import com.raylib.java.core.rCore;
import com.raylib.java.raymath.Vector2;
import game.Window;
import game.objects.Player;
import game.objects.infrastructure.Router;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

import com.raylib.java.Raylib;

import static com.raylib.java.core.input.Keyboard.KEY_R;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_RIGHT;
import static com.raylib.java.core.rCore.GetMousePosition;
import static com.raylib.java.core.rCore.GetMouseWheelMove;
import static game.Window.properties.rl;

public class Controls {
    public static void handleInputs() throws Exception {
        if (rl.core.GetMouseX() > Window.properties.windowWidth - Gui.width) {
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
            if (rl.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                //If player has "routers" selected
                if (Player.properties.currentSelectedStructureType == 0) {
                    //Then place a router if there isn't already a router present in the given cell
                    Vector2 screenToWorldPos = rl.core.GetScreenToWorld2D(GetMousePosition(), Player.camera);
                    int gridX = Math.round(screenToWorldPos.x / MainGame.grid.cellWindowWidth);
                    int gridY = Math.round(screenToWorldPos.y / MainGame.grid.cellWindowHeight);
                    Construct.placeRouter(Player.properties.currentStructureLevel, gridX, gridY);
                }
                //If the player has "Cables" selected
                else if (Player.properties.currentSelectedStructureType == 1) {
                    Vector2 screenToWorldPos = rl.core.GetScreenToWorld2D(GetMousePosition(), Player.camera);
                    int gridX = Math.round(screenToWorldPos.x / MainGame.grid.cellWindowWidth);
                    int gridY = Math.round(screenToWorldPos.y / MainGame.grid.cellWindowHeight);
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
                                Player.placingCable = false;
                                Player.firstStructure = -1;
                            }
                        }
                        int secondStructureId = secondStructure.id;
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
                                return;
                            }
                        }
                        Player.placingCable = true;
                        Player.firstStructure = firstStructure.id;
                        return;
                    }
                }
            }
            else if (rl.core.IsMouseButtonPressed(MOUSE_BUTTON_RIGHT)) {
                //If the player is placing cable
                if (Player.properties.currentSelectedStructureType == 1) {
                    if (Player.placingCable) {
                        Player.placingCable = false;
                        Player.firstStructure = -1;
                        return;
                    }
                }
                Vector2 screenToWorldPos = rl.core.GetScreenToWorld2D(GetMousePosition(), Player.camera);
                int gridX = Math.round(screenToWorldPos.x / MainGame.grid.cellWindowWidth);
                int gridY = Math.round(screenToWorldPos.y / MainGame.grid.cellWindowHeight);
                Structure structure = MainGame.getStructureByGridPos(gridX, gridY);
                if (structure instanceof Router) {
                    Construct.removeRouterByGridPos(gridX, gridY);
                }
            }
            //Camera
            if (GetMouseWheelMove() != 0) {
                Player.changeZoom(GetMouseWheelMove());
            }
            if (rl.core.IsKeyPressed(KEY_R)) {
                Player.resetCam();
            }
        }
    }
}
