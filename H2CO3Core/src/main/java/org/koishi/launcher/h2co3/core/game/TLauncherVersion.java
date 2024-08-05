package org.koishi.launcher.h2co3.core.game;

import androidx.annotation.Nullable;

import com.google.gson.JsonParseException;

import org.koishi.launcher.h2co3.core.download.DownloadInfo;
import org.koishi.launcher.h2co3.core.download.DownloadType;
import org.koishi.launcher.h2co3.core.download.GameJavaVersion;
import org.koishi.launcher.h2co3.core.download.LoggingInfo;
import org.koishi.launcher.h2co3.core.download.ReleaseType;
import org.koishi.launcher.h2co3.core.download.Version;
import org.koishi.launcher.h2co3.core.utils.Arguments;
import org.koishi.launcher.h2co3.core.utils.CompatibilityRule;
import org.koishi.launcher.h2co3.core.utils.gson.JsonMap;
import org.koishi.launcher.h2co3.core.utils.gson.tools.TolerableValidationException;
import org.koishi.launcher.h2co3.core.utils.gson.tools.Validation;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class TLauncherVersion implements Validation {

    private final String id;
    private final String minecraftArguments;
    private final Arguments arguments;
    private final String mainClass;
    private final String inheritsFrom;
    private final String jar;
    private final AssetIndexInfo assetIndex;
    private final String assets;
    private final Integer complianceLevel;
    @Nullable
    private final GameJavaVersion javaVersion;
    private final List<TLauncherLibrary> libraries;
    private final List<CompatibilityRule> compatibilityRules;
    private final JsonMap<DownloadType, DownloadInfo> downloads;
    private final JsonMap<DownloadType, LoggingInfo> logging;
    private final ReleaseType type;
    private final Instant time;
    private final Instant releaseTime;
    private final Integer minimumLauncherVersion;
    private final Integer tlauncherVersion;

    public TLauncherVersion(String id, String minecraftArguments, Arguments arguments, String mainClass, String inheritsFrom, String jar, AssetIndexInfo assetIndex, String assets, Integer complianceLevel, @Nullable GameJavaVersion javaVersion, List<TLauncherLibrary> libraries, List<CompatibilityRule> compatibilityRules, JsonMap<DownloadType, DownloadInfo> downloads, JsonMap<DownloadType, LoggingInfo> logging, ReleaseType type, Instant time, Instant releaseTime, Integer minimumLauncherVersion, Integer tlauncherVersion) {
        this.id = id;
        this.minecraftArguments = minecraftArguments;
        this.arguments = arguments;
        this.mainClass = mainClass;
        this.inheritsFrom = inheritsFrom;
        this.jar = jar;
        this.assetIndex = assetIndex;
        this.assets = assets;
        this.complianceLevel = complianceLevel;
        this.javaVersion = javaVersion;
        this.libraries = libraries;
        this.compatibilityRules = compatibilityRules;
        this.downloads = downloads;
        this.logging = logging;
        this.type = type;
        this.time = time;
        this.releaseTime = releaseTime;
        this.minimumLauncherVersion = minimumLauncherVersion;
        this.tlauncherVersion = tlauncherVersion;
    }

    @Override
    public void validate() throws JsonParseException, TolerableValidationException {
        Validation.requireNonNull(tlauncherVersion, "Not TLauncher version json format");
    }

    public Version toVersion() {
        return new Version(
                false,
                id,
                null,
                null,
                minecraftArguments,
                arguments,
                mainClass,
                inheritsFrom,
                jar,
                assetIndex,
                assets,
                complianceLevel,
                javaVersion,
                libraries == null ? null : libraries.stream().map(TLauncherLibrary::toLibrary).collect(Collectors.toList()),
                compatibilityRules,
                downloads,
                logging,
                type,
                time,
                releaseTime,
                minimumLauncherVersion,
                null,
                null,
                null
        );
    }
}
