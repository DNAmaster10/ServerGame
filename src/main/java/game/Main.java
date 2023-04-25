package game;

import com.raylib.java.Raylib;

import game.scenes.DrawScenes;
import game.scenes.Init;
import game.scenes.TickScenes;

import com.raylib.java.Raylib;


public class Main {
    public static void main (String[] args) throws Exception {
        Raylib rl = Window.properties.rl;
        rl.core.InitWindow(Window.properties.windowWidth, Window.properties.windowHeight, "Internet Game");
        rl.core.SetTargetFPS(60);

        //init
        Init.init();

        while (!rl.core.WindowShouldClose()) {
            TickScenes.tick();
            rl.core.BeginDrawing();
            rl.text.DrawFPS(20, 20);
            DrawScenes.draw();
            rl.core.EndDrawing();
        }
        rl.core.CloseWindow();
    }
}
