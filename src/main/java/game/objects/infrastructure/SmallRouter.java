package game.objects.infrastructure;

import com.raylib.Jaylib;

public class SmallRouter {
    //The time taken between packet releases.
    //The smaller the number, the more packers per second can be handles
    public static float packetHandleDelay = 1f;
    public static int maxCacheSize = 5;
}