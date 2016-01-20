package com.tinkerpop.frames;

/**
 * GraphQuery that allows framing of results.
 * 
 * @author Bryn Cooke
 *
 */
public interface FramedGraphQuery
{
    FramedGraphQuery has(String key);

    FramedGraphQuery hasNot(String key);

    FramedGraphQuery has(String key, Object value);

    FramedGraphQuery limit(int limit);

    /**
     * Execute the query and return the matching edges.
     *
     * @param kind the default annotated interface to frame the edge as
     * @return the unfiltered incident edges
     */
    <T> Iterable<T> edges(Class<T> kind);

    /**
     * Execute the query and return the vertices on the other end of the matching edges.
     *
     * @param kind the default annotated interface to frame the vertex as
     * @return the unfiltered adjacent vertices
     */
    <T> Iterable<T> vertices(Class<T> kind);
}
