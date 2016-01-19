package com.tinkerpop.frames.modules;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Allows dynamic resolution of interfaces that a frame will implement when framing an element. For instance the java type information may retrieved
 * from a property on the element. Instances of this class should be threadsafe.
 * 
 * @author Bryn Cooke
 */
public interface TypeResolver
{

    /**
     * @param v The vertex being framed.
     * @param defaultType The default as passed in to the framing method.
     * @return Any additional interfaces that the frame should implement.
     */
    Class<?>[] resolveTypes(Vertex v, Class<?> defaultType);

    /**
     * @param e The edge being framed.
     * @param defaultType The default as passed in to the framing method.
     * @return Any additional interfaces that the frame should implement.
     */
    Class<?>[] resolveTypes(Edge e, Class<?> defaultType);
}