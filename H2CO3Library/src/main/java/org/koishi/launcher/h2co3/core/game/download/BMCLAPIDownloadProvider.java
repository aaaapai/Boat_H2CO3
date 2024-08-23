package org.koishi.launcher.h2co3.core.game.download;

import org.koishi.launcher.h2co3.core.game.download.fabric.FabricAPIVersionList;
import org.koishi.launcher.h2co3.core.game.download.fabric.FabricVersionList;
import org.koishi.launcher.h2co3.core.game.download.forge.ForgeBMCLVersionList;
import org.koishi.launcher.h2co3.core.game.download.vanilla.GameVersionList;
import org.koishi.launcher.h2co3.core.game.download.liteloader.LiteLoaderBMCLVersionList;
import org.koishi.launcher.h2co3.core.game.download.neoforge.NeoForgeBMCLVersionList;
import org.koishi.launcher.h2co3.core.game.download.optifine.OptiFineBMCLVersionList;
import org.koishi.launcher.h2co3.core.game.download.quilt.QuiltAPIVersionList;
import org.koishi.launcher.h2co3.core.game.download.quilt.QuiltVersionList;

import java.util.HashMap;
import java.util.Map;

public final class BMCLAPIDownloadProvider implements DownloadProvider {
    private static final String VERSION_MANIFEST_URL = "/mc/game/version_manifest.json";
    private static final String ASSETS_URL = "/assets/";

    private final String apiRoot;
    private final GameVersionList game;
    private final FabricVersionList fabric;
    private final FabricAPIVersionList fabricApi;
    private final ForgeBMCLVersionList forge;
    private final NeoForgeBMCLVersionList neoforge;
    private final LiteLoaderBMCLVersionList liteLoader;
    private final OptiFineBMCLVersionList optifine;
    private final QuiltVersionList quilt;
    private final QuiltAPIVersionList quiltApi;
    private final Map<String, String> replacementMap;

    public BMCLAPIDownloadProvider(String apiRoot) {
        if (apiRoot == null || apiRoot.isEmpty()) {
            throw new IllegalArgumentException("apiRoot cannot be null or empty");
        }
        this.apiRoot = apiRoot;
        this.game = new GameVersionList(this);
        this.fabric = new FabricVersionList(this);
        this.fabricApi = new FabricAPIVersionList(this);
        this.forge = new ForgeBMCLVersionList(apiRoot);
        this.neoforge = new NeoForgeBMCLVersionList(apiRoot);
        this.liteLoader = new LiteLoaderBMCLVersionList(this);
        this.optifine = new OptiFineBMCLVersionList(apiRoot);
        this.quilt = new QuiltVersionList(this);
        this.quiltApi = new QuiltAPIVersionList(this);
        this.replacementMap = new HashMap<>() {{
            put("https://bmclapi2.bangbang93.com", apiRoot);
            put("https://launchermeta.mojang.com", apiRoot);
            put("https://piston-meta.mojang.com", apiRoot);
            put("https://piston-data.mojang.com", apiRoot);
            put("https://launcher.mojang.com", apiRoot);
            put("https://libraries.minecraft.net", apiRoot + "/libraries");
            put("http://files.minecraftforge.net/maven", apiRoot + "/maven");
            put("https://files.minecraftforge.net/maven", apiRoot + "/maven");
            put("https://maven.minecraftforge.net", apiRoot + "/maven");
            put("https://maven.neoforged.net/releases/net/neoforged/forge", apiRoot + "/maven/net/neoforged/forge");
            put("http://dl.liteloader.com/versions/versions.json", apiRoot + "/maven/com/mumfrey/liteloader/versions.json");
            put("http://dl.liteloader.com/versions", apiRoot + "/maven");
            put("https://meta.fabricmc.net", apiRoot + "/fabric-meta");
            put("https://maven.fabricmc.net", apiRoot + "/maven");
            put("https://authlib-injector.yushi.moe", apiRoot + "/mirrors/authlib-injector");
            put("https://repo1.maven.org/maven2", "https://mirrors.cloud.tencent.com/nexus/repository/maven-public");
        }};
    }

    public String getApiRoot() {
        return apiRoot;
    }

    @Override
    public String getVersionListURL() {
        return apiRoot + VERSION_MANIFEST_URL;
    }

    @Override
    public String getAssetBaseURL() {
        return apiRoot + ASSETS_URL;
    }

    @Override
    public VersionList<?> getVersionListById(String id) {
        return switch (id) {
            case "game" -> game;
            case "fabric" -> fabric;
            case "fabric-api" -> fabricApi;
            case "forge" -> forge;
            case "neoforge" -> neoforge;
            case "liteloader" -> liteLoader;
            case "optifine" -> optifine;
            case "quilt" -> quilt;
            case "quilt-api" -> quiltApi;
            default -> throw new IllegalArgumentException("Unrecognized version list id: " + id);
        };
    }

    @Override
    public String injectURL(String baseURL) {
        for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
            if (baseURL.startsWith(entry.getKey())) {
                return entry.getValue() + baseURL.substring(entry.getKey().length());
            }
        }
        return baseURL;
    }

    @Override
    public int getConcurrency() {
        return Math.max(Runtime.getRuntime().availableProcessors() * 2, 6);
    }
}