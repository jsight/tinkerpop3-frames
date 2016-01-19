package com.tinkerpop.frames.annotations;

import java.lang.reflect.Method;

import org.apache.tinkerpop.gremlin.structure.Element;

import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.MethodHandler;

public class PropertyMethodHandler implements MethodHandler<Property>
{

    @Override
    public Class<Property> getAnnotationType()
    {
        return Property.class;
    }

    @Override
    public Object processElement(Object frame, Method method,
                Object[] arguments, Property annotation,
                FramedGraph<?> framedGraph, Element element)
    {
        if (ClassUtilities.isGetMethod(method))
        {
            Object value = element.property(annotation.value()).value();
            if (method.getReturnType().isEnum())
                return getValueAsEnum(method, value);
            else
                return value;
        }
        else if (ClassUtilities.isSetMethod(method))
        {
            Object value = arguments[0];
            if (null == value)
            {
                element.property(annotation.value()).remove();
            }
            else
            {
                if (value.getClass().isEnum())
                {
                    element.property(annotation.value(), ((Enum<?>) value).name());
                }
                else
                {
                    element.property(annotation.value(), value);
                }
            }
            if (method.getReturnType().isAssignableFrom(frame.getClass()))
                return frame;
        }
        else if (ClassUtilities.isRemoveMethod(method))
        {
            element.property(annotation.value()).remove();
            return null;
        }

        return null;
    }

    private Enum getValueAsEnum(final Method method, final Object value)
    {
        Class<Enum> en = (Class<Enum>) method.getReturnType();
        if (value != null)
            return Enum.valueOf(en, value.toString());

        return null;
    }
}
