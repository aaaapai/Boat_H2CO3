package org.koishi.launcher.h2co3.core.fakefx.property.adapter;

import org.koishi.launcher.h2co3.core.fakefx.reflect.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 *
 */
public class JavaBeanPropertyBuilderHelper {

    private static final String IS_PREFIX = "is";
    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";

    private String propertyName;
    private Class<?> beanClass;
    private Object bean;
    private String getterName;
    private String setterName;
    private Method getter;
    private Method setter;
    private PropertyDescriptor descriptor;

    public void name(String propertyName) {
        if (!Objects.equals(propertyName, this.propertyName)) {
            this.propertyName = propertyName;
            this.descriptor = null;
        }
    }

    public void beanClass(Class<?> beanClass) {
        if (!Objects.equals(beanClass, this.beanClass)) {
            ReflectUtil.checkPackageAccess(beanClass);
            this.beanClass = beanClass;
            this.descriptor = null;
        }
    }

    public void bean(Object bean) {
        this.bean = bean;
        if (bean != null) {
            Class<?> newClass = bean.getClass();
            if ((beanClass == null) || !beanClass.isAssignableFrom(newClass)) {
                ReflectUtil.checkPackageAccess(newClass);
                this.beanClass = newClass;
                this.descriptor = null;
            }
        }
    }

    public Object getBean() {
        return bean;
    }

    public void getterName(String getterName) {
        if (!Objects.equals(getterName, this.getterName)) {
            this.getterName = getterName;
            this.descriptor = null;
        }
    }

    public void setterName(String setterName) {
        if (!Objects.equals(setterName, this.setterName)) {
            this.setterName = setterName;
            this.descriptor = null;
        }
    }

    public void getter(Method getter) {
        if (!Objects.equals(getter, this.getter)) {
            this.getter = getter;
            this.descriptor = null;
        }
    }

    public void setter(Method setter) {
        if (!Objects.equals(setter, this.setter)) {
            this.setter = setter;
            this.descriptor = null;
        }
    }

    public PropertyDescriptor getDescriptor() throws NoSuchMethodException {
        if (descriptor == null) {
            if (propertyName == null) {
                throw new NullPointerException("Property name has to be specified");
            }
            if (propertyName.isEmpty()) {
                throw new IllegalArgumentException("Property name cannot be empty");
            }
            final String capitalizedName = ReadOnlyPropertyDescriptor.capitalizedName(propertyName);
            Method getterMethod = getter;
            if (getterMethod == null) {
                if ((getterName != null) && !getterName.isEmpty()) {
                    getterMethod = beanClass.getMethod(getterName);
                } else {
                    try {
                        getterMethod = beanClass.getMethod(IS_PREFIX + capitalizedName);
                    } catch (NoSuchMethodException e) {
                        getterMethod = beanClass.getMethod(GET_PREFIX + capitalizedName);
                    }
                }
            }
            Method setterMethod = setter;
            if (setterMethod == null) {
                final Class<?> type = getterMethod.getReturnType();
                if ((setterName != null) && !setterName.isEmpty()) {
                    setterMethod = beanClass.getMethod(setterName, type);
                } else {
                    setterMethod = beanClass.getMethod(SET_PREFIX + capitalizedName, type);
                }
            }
            descriptor = new PropertyDescriptor(propertyName, beanClass, getterMethod, setterMethod);
        }
        return descriptor;
    }

}
