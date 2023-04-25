package game;

import game.scenes.DrawScenes;
import game.scenes.Init;
import game.scenes.TickScenes;

import static com.raylib.Jaylib.*;

import com.raylib.java.Raylib;

public class Main {
    public static void main (String[] args) throws Exception {
        InitWindow(Window.properties.windowWidth, Window.properties.windowHeight, "Internet Game");
        SetTargetFPS(60);

        //init
        Init.init();

        while (!WindowShouldClose()) {
            TickScenes.tick();
            BeginDrawing();
            DrawFPS(20, 20);
            DrawScenes.draw();
            EndDrawing();
        }
        CloseWindow();
    }
}
