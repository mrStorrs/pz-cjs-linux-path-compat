package com.cjstorrs.cjslinuxpathcompat;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import me.zed_0xff.zombie_buddy.Patch;
import zombie.ZomboidFileSystem;

@Patch(
    className = "zombie.scripting.ScriptManager",
    methodName = "searchFolders",
    warmUp = true,
    strictMatch = true
)
public final class Patch_ScriptManager_SearchFolders {
    private Patch_ScriptManager_SearchFolders() {
    }

    @Patch.OnEnter(skipOn = true)
    public static boolean preserveLeafPathCase(
        @Patch.Argument(0) URI base,
        @Patch.Argument(1) File file,
        @Patch.Argument(2) List<String> scriptPaths
    ) {
        if (file.isDirectory()) {
            return false;
        }

        String absolutePath = file.getAbsolutePath();
        if (absolutePath.toLowerCase(Locale.ROOT).endsWith(".txt")) {
            String relativePath = ZomboidFileSystem.instance.getRelativeFile(base, absolutePath);
            scriptPaths.add(relativePath);
        }

        return true;
    }
}
