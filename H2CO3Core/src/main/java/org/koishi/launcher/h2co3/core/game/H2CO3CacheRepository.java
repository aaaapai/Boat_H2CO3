package org.koishi.launcher.h2co3.core.game;

import org.koishi.launcher.h2co3.core.download.DefaultCacheRepository;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.StringProperty;

import java.nio.file.Paths;

public class H2CO3CacheRepository extends DefaultCacheRepository {

    public static final H2CO3CacheRepository REPOSITORY = new H2CO3CacheRepository();
    private final StringProperty directory = new SimpleStringProperty();

    public H2CO3CacheRepository() {
        directory.addListener((observable, oldValue, newValue) -> {
            System.out.println(observable + " Changed " + oldValue + " to " + newValue);
            changeDirectory(Paths.get(newValue));
            System.out.println(indexFile);
        });
    }

    public String getDirectory() {
        return directory.get();
    }

    public void setDirectory(String directory) {
        this.directory.set(directory);
    }

    public StringProperty directoryProperty() {
        return directory;
    }
}
