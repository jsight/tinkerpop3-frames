package com.tinkerpop.frames;

import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * An interface for Vertex-based frames which provides access to the underlying Vertex.
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public interface VertexFrame {
    Vertex asVertex();
}
