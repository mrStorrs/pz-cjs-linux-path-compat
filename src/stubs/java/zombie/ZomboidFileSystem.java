package zombie;

import java.net.URI;

public class ZomboidFileSystem {
    public static ZomboidFileSystem instance = new ZomboidFileSystem();

    public String getRelativeFile(URI root, String path) {
        return path;
    }
}
