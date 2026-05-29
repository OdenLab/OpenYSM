package com.elfmcys.yesstevemodel;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

/** Extracts bundled model packs on Fabric using Fabric Loader's mod root API. */
public final class YesSteveModelFabricBuiltinExtractor {
    private static final String BUILTIN_ASSETS = "assets/yes_steve_model/builtin";

    private YesSteveModelFabricBuiltinExtractor() {
    }

    public static void extract(Path modelRoot, Logger logger) throws IOException {
        Path built = modelRoot.resolve("built");
        Path blacklist = modelRoot.resolve("blacklist.txt");
        resetDirectory(built);
        createDefaultBlacklist(blacklist);
        List<Pattern> blacklistRules = loadBlacklist(blacklist);

        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(YesSteveModelFabric.MOD_ID);
        if (mod.isEmpty()) {
            logger.warn("OpenYSM Fabric mod container not found; skipping builtin model extraction");
            return;
        }

        int copied = 0;
        for (Path root : mod.get().getRootPaths()) {
            Path sourceRoot = root.resolve(BUILTIN_ASSETS);
            if (!Files.isDirectory(sourceRoot)) {
                continue;
            }
            copied += copyBuiltinRoot(sourceRoot, built, blacklistRules, logger);
        }
        logger.info("OpenYSM Fabric extracted {} builtin model files to {}", copied, built.toAbsolutePath().normalize());
    }

    private static int copyBuiltinRoot(Path sourceRoot, Path built, List<Pattern> blacklistRules, Logger logger) throws IOException {
        final int[] copied = {0};
        try (Stream<Path> walker = Files.walk(sourceRoot)) {
            walker.forEach(source -> {
                try {
                    Path relative = sourceRoot.relativize(source);
                    String matchPath = BUILTIN_ASSETS + "/" + relative.toString().replace('\\', '/');
                    if (isBlacklisted(matchPath, blacklistRules)) {
                        return;
                    }

                    Path destination = built.resolve(relative.toString());
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(destination);
                    } else {
                        Files.createDirectories(destination.getParent());
                        try (InputStream in = Files.newInputStream(source)) {
                            Files.copy(in, destination);
                        }
                        copied[0]++;
                    }
                } catch (IOException e) {
                    logger.warn("Failed to extract Fabric builtin model file: {}", source, e);
                }
            });
        }
        return copied[0];
    }

    private static void resetDirectory(Path directory) throws IOException {
        if (Files.isDirectory(directory)) {
            try (Stream<Path> walker = Files.walk(directory)) {
                walker.sorted(java.util.Comparator.reverseOrder())
                        .filter(path -> !path.equals(directory))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                            }
                        });
            }
        }
        Files.createDirectories(directory);
    }

    private static boolean isBlacklisted(String matchPath, List<Pattern> blacklistRules) {
        for (Pattern rule : blacklistRules) {
            if (rule.matcher(matchPath).matches()) {
                return true;
            }
        }
        return false;
    }

    private static List<Pattern> loadBlacklist(Path blacklist) {
        List<Pattern> rules = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(blacklist, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    rules.add(Pattern.compile(line));
                } catch (PatternSyntaxException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
        return rules;
    }

    private static void createDefaultBlacklist(Path blacklist) throws IOException {
        if (Files.exists(blacklist)) {
            return;
        }
        String content = "# Yes Steve Model 模组 - 内置模型黑名单配置文件\n" +
                "# Yes Steve Model Mod - Built-in Model Blacklist Configuration File\n" +
                "# One regular expression per line, matched against assets/yes_steve_model/builtin/...\n" +
                "# Example: assets/yes_steve_model/builtin/wine_fox/.*\n";
        Files.createDirectories(blacklist.getParent());
        Files.writeString(blacklist, content, StandardCharsets.UTF_8);
    }
}
