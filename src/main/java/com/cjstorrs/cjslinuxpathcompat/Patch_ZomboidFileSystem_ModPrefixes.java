package com.cjstorrs.cjslinuxpathcompat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import me.zed_0xff.zombie_buddy.Patch;

@Patch(className = "zombie.ZomboidFileSystem", methodName = "getAllModFolders", strictMatch = true)
public final class Patch_ZomboidFileSystem_ModPrefixes {
    private Patch_ZomboidFileSystem_ModPrefixes() {}

    @Patch.OnExit
    public static void addRealModRoots(List<String> folders) throws IOException {
        List<String> resolved = new ArrayList<>();
        for (String folder : folders) {
            Path path = Path.of(folder);
            if (Files.isSymbolicLink(path)) {
                String real = path.toRealPath().toString();
                if (!folders.contains(real) && !resolved.contains(real)) {
                    resolved.add(real);
                }
            }
        }
        folders.addAll(resolved);
    }
}
