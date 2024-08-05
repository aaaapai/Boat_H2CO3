package org.koishi.launcher.h2co3.core.download;

import static org.koishi.launcher.h2co3.core.utils.Logging.LOG;

import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Level;

public class H2CO3GameRepository extends DefaultGameRepository {
    private static final String PROFILE = "{\"profiles\": {\"(Default)\": {\"name\": \"(Default)\"}},\"clientToken\": \"88888888-8888-8888-8888-888888888888\"}";

    public H2CO3GameRepository(File baseDirectory) {
        super(baseDirectory);
        this.setBaseDirectory(new File(H2CO3GameHelper.getGameDirectory()));
        this.versions = new TreeMap<>();
    }

    @Override
    protected void refreshVersionsImpl() {
        super.refreshVersionsImpl();
        System.out.println(versions.toString());
        try {
            File file = new File(getBaseDirectory(), "launcher_profiles.json");
            if (!file.exists() && !versions.isEmpty())
                FileTools.writeText(file, PROFILE);
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Unable to create launcher_profiles.json, Forge/LiteLoader installer will not work.", ex);
        }
    }
}
