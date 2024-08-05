package org.koishi.launcher.h2co3.core.fakefx.property.adapter;

import java.util.EventListener;

public interface VetoableChangeListener extends EventListener {
    void vetoableChange(PropertyChangeEvent var1) throws PropertyVetoException;
}