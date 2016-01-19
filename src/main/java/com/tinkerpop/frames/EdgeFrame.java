package com.tinkerpop.frames;

import org.apache.tinkerpop.gremlin.structure.Edge;

/**
 * An interface for Edge-based frames which provides access to the underlying Edge.
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public interface EdgeFrame {
    Edge asEdge();
}
