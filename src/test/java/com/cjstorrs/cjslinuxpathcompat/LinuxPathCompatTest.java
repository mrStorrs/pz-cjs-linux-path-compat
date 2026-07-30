package com.cjstorrs.cjslinuxpathcompat;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import me.zed_0xff.zombie_buddy.Patch;
import zombie.ZomboidFileSystem;

public final class LinuxPathCompatTest {
    private LinuxPathCompatTest() {
    }

    public static void main(String[] args) throws Exception {
        testPatchMetadata();
        testDirectoryFallsThrough();
        testTextFilePreservesCase();
        testNonTextFileIsSkippedWithoutAddition();
        System.out.println("cjsLinuxPathCompat tests passed");
    }

    private static void testPatchMetadata() throws Exception {
        Patch patch = Patch_ScriptManager_SearchFolders.class.getAnnotation(Patch.class);
        check(patch != null, "patch annotation is required");
        check(
            "zombie.scripting.ScriptManager".equals(patch.className()),
            "patch must target ScriptManager"
        );
        check("searchFolders".equals(patch.methodName()), "patch must target searchFolders");
        check(patch.warmUp(), "ScriptManager patch must warm up before script loading");
        check(patch.strictMatch(), "ScriptManager overload matching must be strict");

        Patch.OnEnter enter = Patch_ScriptManager_SearchFolders.class
            .getDeclaredMethod(
                "preserveLeafPathCase",
                URI.class,
                File.class,
                ArrayList.class
            )
            .getAnnotation(Patch.OnEnter.class);
        check(enter != null && enter.skipOn(), "leaf interception must skip vanilla code");
    }

    private static void testDirectoryFallsThrough() throws Exception {
        Path directory = Files.createTempDirectory("cjs-linux-path-directory-");
        try {
            ArrayList<String> scripts = new ArrayList<>();
            boolean skip = Patch_ScriptManager_SearchFolders.preserveLeafPathCase(
                directory.toUri(),
                directory.toFile(),
                scripts
            );
            check(!skip, "directory traversal must remain vanilla");
            check(scripts.isEmpty(), "directory traversal must not add a script path");
        } finally {
            Files.deleteIfExists(directory);
        }
    }

    private static void testTextFilePreservesCase() {
        URI base = URI.create("file:/Mods/CaseSensitiveMod/");
        File file = new File("/Mods/CaseSensitiveMod/42.20/media/scripts/MyTemplate.TXT");
        ArrayList<String> scripts = new ArrayList<>();
        ZomboidFileSystem.instance = new FixedRelativeFileSystem(
            "42.20/media/scripts/MyTemplate.TXT"
        );

        boolean skip = Patch_ScriptManager_SearchFolders.preserveLeafPathCase(
            base,
            file,
            scripts
        );

        check(skip, "text-file leaf must skip vanilla lowercasing");
        check(
            scripts.equals(List.of("42.20/media/scripts/MyTemplate.TXT")),
            "text-file path case must be preserved"
        );
    }

    private static void testNonTextFileIsSkippedWithoutAddition() {
        ArrayList<String> scripts = new ArrayList<>();
        boolean skip = Patch_ScriptManager_SearchFolders.preserveLeafPathCase(
            URI.create("file:/Mods/Test/"),
            new File("/Mods/Test/poster.png"),
            scripts
        );
        check(skip, "non-script leaf must skip the vanilla leaf branch");
        check(scripts.isEmpty(), "non-script leaf must not be registered");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class FixedRelativeFileSystem extends ZomboidFileSystem {
        private final String result;

        private FixedRelativeFileSystem(String result) {
            this.result = result;
        }

        @Override
        public String getRelativeFile(URI root, String path) {
            return result;
        }
    }
}
