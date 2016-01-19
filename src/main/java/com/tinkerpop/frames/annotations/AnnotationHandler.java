package com.tinkerpop.frames.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Element;

import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.modules.MethodHandler;

/**
 * Allows handling of method on frames. Only the first annotation handler found is called. Instances of this class should be threadsafe.
 * 
 * @param <T> The type of annotation handled.
 * @deprecated Use {@link MethodHandler} instead
 */
public interface AnnotationHandler<T extends Annotation>
{
    /**
     * @return The annotation type that this handler responds to.
     */
    public Class<T> getAnnotationType();

    /**
     * @param annotation The annotation
     * @param method The method being called on the frame.
     * @param arguments The arguments to the method.
     * @param framedGraph The graph being called.
     * @param element The underlying element.
     * @param direction The direction of the edge.
     * @return A return value for the method.
     */
    public Object processElement(final T annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph,
                final Element element, final Direction direction);
}
