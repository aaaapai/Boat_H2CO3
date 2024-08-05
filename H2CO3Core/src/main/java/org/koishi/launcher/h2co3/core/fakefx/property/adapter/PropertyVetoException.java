package org.koishi.launcher.h2co3.core.fakefx.property.adapter;

public class PropertyVetoException extends Exception {
    private static final long serialVersionUID = 129596057694162164L;
    private final PropertyChangeEvent evt;

    public PropertyVetoException(String mess, PropertyChangeEvent evt) {
        super(mess);
        this.evt = evt;
    }

    public PropertyChangeEvent getPropertyChangeEvent() {
        return this.evt;
    }
}