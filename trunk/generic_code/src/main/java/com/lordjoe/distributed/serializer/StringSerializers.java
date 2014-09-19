package com.lordjoe.distributed.serializer;

import javax.annotation.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * com.lordjoe.distributed.serializer.StringSerializers
 * User: Steve
 * Date: 9/19/2014
 */
public class StringSerializers {

    private static final Map<Class, IStringSerializer> serializers = new HashMap<Class, IStringSerializer>();

    public static IStringSerializer getSerializer(Class c) {
        IStringSerializer ret = serializers.get(c);
        if (ret != null)
            return ret;
        ret = buildSerializer(c);
        if (ret != null) {
            registerSerializer(c, ret);
            return ret;
        }
        throw new IllegalArgumentException("no serializer for class " + c);
    }

    public static final Class[] STRING_ARGS = {String.class};

    /**
     * try using string constructor and toString
     * @param pC
     * @return
     */
    private static IStringSerializer buildSerializer(final Class pC) {
        try {
            final Constructor cnst = pC.getConstructor(STRING_ARGS);
            return new IStringSerializer() {
                @Override
                public Class getSerializedClass() {
                    return pC;
                }

                @Nonnull
                @Override
                public String asString(@Nonnull final Object t) {
                    return t.toString();
                }

                @Override
                public Object fromString(@Nonnull final String t) {
                    try {
                        return cnst.newInstance(t);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
        catch (NoSuchMethodException e) {
            return null;

        }
    }

    /**
     * register a serializer for a specific class
     * @param c  target class
     * @param ser serializer
     */
    public static void registerSerializer(@Nonnull Class c, @Nonnull  IStringSerializer ser) {
        if(!c.isAssignableFrom(ser.getSerializedClass()))
            throw new IllegalArgumentException("bad serializer register");
        serializers.put(c, ser);

    }

}
