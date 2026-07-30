# CJS Linux Path Compatibility

Independent Project Zomboid B42.20 compatibility mod for Linux. It uses
ZombieBuddy to prevent `ScriptManager.searchFolders()` from lowercasing
case-sensitive script paths.

The mod intercepts only non-directory calls. It reproduces the vanilla `.txt`
filter, adds the path returned by `ZomboidFileSystem.getRelativeFile()` without
changing its case, and skips the original leaf-file branch. Directory traversal
continues through the vanilla method.

The live mod must be linked only into the isolated B42.20 user-data tree:

`/home/cjstorrs/games/Project Zomboid Linux 42.20.0/user-data/Zomboid/mods/cjsLinuxPathCompat`

Build and test with:

```bash
./tools/build_jar.sh
./tools/test.sh
```
