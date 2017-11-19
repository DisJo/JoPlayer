package jo.dis.player.cache;

import java.io.File;

import jo.dis.player.cache.file.DiskUsage;
import jo.dis.player.cache.file.FileNameGenerator;


class Config {

    public final File cacheRoot;
    public final FileNameGenerator fileNameGenerator;
    public final DiskUsage diskUsage;

    Config(File cacheRoot, FileNameGenerator fileNameGenerator, DiskUsage diskUsage) {
        this.cacheRoot = cacheRoot;
        this.fileNameGenerator = fileNameGenerator;
        this.diskUsage = diskUsage;
    }

    File generateCacheFile(String url) {
        String name = fileNameGenerator.generate(url);
        return new File(cacheRoot, name);
    }

}
