package game.objects;

import com.raylib.java.core.camera.Camera2D;
import com.raylib.java.core.camera.rCamera;
import com.raylib.java.raymath.Vector2;

import static com.raylib.java.core.rCore.GetMousePosition;
import static game.Window.properties.rl;
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
    public static Camera2D camera = new Camera2D();
    public static Vector2 cameraTarget = new Vector2();

    public static boolean placingCable = false;
    public static int firstStructure;
    public static boolean drawIds = true;
    public static int lostPackets = 0;
    public static int money;
    public static int deliveredPackets;

    public static void changeZoom(float zoomChange) {
        float currentZoom = camera.zoom;
        currentZoom += zoomChange;
        if (currentZoom < 1) {
            currentZoom = 1;
        }
        Vector2 mouseWorldPos = rl.core.GetScreenToWorld2D(GetMousePosition(), Player.camera);
        Player.camera.offset = GetMousePosition();
        Player.camera.target = mouseWorldPos;
        Player.camera.zoom = currentZoom;
    }

    public static void resetCam() {
        Player.camera.rotation = 0.0f;
        Player.camera.target = cameraTarget;
        Player.camera.zoom = 1.0f;
    }

    public static void init() {
        cameraTarget.x = 0.0f;
        cameraTarget.y = 0.0f;
        Player.camera.target = cameraTarget;
        Player.camera.zoom = 1.0f;
        Player.camera.rotation = 0.0f;
    }
}
