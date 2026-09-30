# CJS Linux Path Compatibility

Independent Project Zomboid B42.20 and B42.21 compatibility mod for Linux. It uses
ZombieBuddy to prevent `ScriptManager.searchFolders()` from lowercasing
case-sensitive script paths.

The mod intercepts only non-directory calls. It reproduces the vanilla `.txt`
filter, adds the path returned by `ZomboidFileSystem.getRelativeFile()` without
changing its case, and skips the original leaf-file branch. Directory traversal
continues through the vanilla method.

The live mod must be linked only into the existing user-data tree (the folder name retains 42.20 after the in-place 42.21 upgrade):

`/home/cjstorrs/games/Project Zomboid Linux 42.20.0/user-data/Zomboid/mods/cjsLinuxPathCompat`

Build and test with:

```bash
./tools/build_jar.sh
./tools/test.sh
```

B42.21 changes `searchFolders` from `ArrayList<String>` to `List<String>`.
The 42.21 payload targets that signature; the original 42.20 JAR is retained.

The B42.21 payload also registers real roots of installed mod symlinks with
the game filesystem, allowing the standard asset-prefix validator to recognize
project-backed mods. Runtime confirmation is pending the user's next launch.
