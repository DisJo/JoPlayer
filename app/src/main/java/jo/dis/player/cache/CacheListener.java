package jo.dis.player.cache;

import java.io.File;


public interface CacheListener {

    void onCacheAvailable(File cacheFile, String url, int percentsAvailable);
}
