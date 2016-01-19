package com.tinkerpop.frames;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.tinkerpop.gremlin.structure.Direction;

/**
 * Incidences annotate getters and adders to represent a Vertex incident to an Edge.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Incidence
{
    /**
     * The labels of the edges that are incident to the vertex.
     *
     * @return the edge label
     */
    String label();

    /**
     * The direction of the edges.
     *
     * @return the edge direction
     */
    Direction direction() default Direction.OUT;
}
