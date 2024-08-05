/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.koishi.launcher.h2co3.core.utils.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.core.download.Version;
import org.koishi.launcher.h2co3.core.game.GameRepository;

/**
 * This event gets fired when a minecraft version has been loaded.
 * <br>
 * This event is fired on the {@link EventBus#EVENT_BUS}
 */
public final class LoadedOneVersionEvent extends Event {

    private final Version version;

    /**
     * @param source  {@link GameRepository}
     * @param version the version id.
     */
    public LoadedOneVersionEvent(Object source, Version version) {
        super(source);
        this.version = version;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public @NotNull String toString() {
        return new ToStringBuilder(this)
                .append("source", source)
                .append("version", version)
                .toString();
    }
}
