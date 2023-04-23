package game.objects;

import com.raylib.Jaylib;
import com.raylib.Raylib;

import static game.objects.Player.properties.currentScene;

public class Player {
    public static class properties {
        public static int currentScene = 1;
        //0 means that the player has selected a router, and 1 is an edge
        public static int currentSelectedStructureType = 0;
        //indicated the level of edge / router
        public static int currentStructureLevel = 1;
    }
    public static void setScene(int scene) {
        currentScene = scene;
    }
    public static Raylib.Camera2D camera = new Raylib.Camera2D(0);
    public static Raylib.Vector2 cameraTarget = new Jaylib.Vector2();

    public static boolean placingCable = false;
    public static int firstStructure;
    public static boolean drawIds = true;
    public static int lostPackets = 0;
    public static int money;
    public static int deliveredPackets;

    public static void changeZoom(float zoomChange) {
        float currentZoom = Player.camera.zoom();
        currentZoom += zoomChange;
        if (currentZoom < 1) {
            currentZoom = 1;
        }
        Raylib.Vector2 mouseWorldPos = Raylib.GetScreenToWorld2D(Raylib.GetMousePosition(), Player.camera);
        Player.camera.offset(Raylib.GetMousePosition());
        Player.camera.target(mouseWorldPos);
        Player.camera.zoom(currentZoom);
    }

    public static void resetCam() {
        Player.camera.rotation(0.0f);
        Player.camera.target(cameraTarget);
        Player.camera.zoom(1.0f);
    }

    public static void init() {
        cameraTarget.x(0.0f).y(0.0f);
        Player.camera.target(cameraTarget);
        Player.camera.zoom(1.0f);
        Player.camera.rotation(0.0f);
    }
}
