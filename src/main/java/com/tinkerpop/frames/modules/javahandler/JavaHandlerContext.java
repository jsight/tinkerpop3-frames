package com.tinkerpop.frames.modules.javahandler;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.tinkerpop.frames.FramedGraph;

/**
 * By implementing this interface your Java handler implementation can access the underlying graph and the framed vertex/edge.
 * 
 * @author Bryn Cooke
 *
 * @param <C>
 */
public interface JavaHandlerContext<C extends Element>
{
    /**
     * @return The framed graph
     */
    FramedGraph<?> g();

    /**
     * @return The element that was framed
     */
    C it();

    /**
     * Frame a vertex using the return type of the method
     * 
     * @param vertex The vertex to frame
     * @return The framed vertex
     */
    <T> T frame(Vertex vertex);

    /**
     * Frame a vertex using an explicit kind of frame
     * 
     * @param vertex The vertex to frame
     * @param kind The type of frame
     * @return The framed vertex
     */
    <T> T frame(Vertex vertex, Class<T> kind);

    /**
     * Frame an edge using the return type of the method
     * 
     * @param edge The edge to frame
     * @return The framed edge
     */
    <T> T frame(Edge edge);

    /**
     * Frame an edge using an explicit kind of frame
     * 
     * @param edge The edge to frame
     * @param kind The type of frame
     * @return The framed edge
     */
    <T> T frame(Edge edge, Class<T> kind);

    /**
     * Frame an edge using the return type of the method
     * 
     * @param edge The edge to frame
     * @param direction The direction of the edge
     * @return The framed edge
     */
    <T> T frame(Edge edge, Direction direction);

    /**
     * Frame an edge using an explicit kind of frame
     * 
     * @param edge The edge to frame
     * @param direction The direction of the edge
     * @param kind The type of frame
     * @return The framed edge
     */
    <T> T frame(Edge edge, Direction direction, Class<T> kind);

    /**
     * Frame some vertices using the return type of the method
     * 
     * @param vertices The vertices to frame
     * @return The framed vertices
     */
    <T> Iterable<T> frameVertices(Iterable<Vertex> vertices);

    /**
     * Frame some vertices using an explicit kind of frame
     * 
     * @param vertices The vertices to frame
     * @param kind The kind of frame
     * @return The framed vertices
     */
    <T> Iterable<T> frameVertices(Iterable<Vertex> vertices, Class<T> kind);

    /**
     * Frame some edges using the return type of the method
     * 
     * @param edges the edges to frame
     * @return The framed edges
     */
    <T> Iterable<T> frameEdges(Iterable<Edge> edges);

    /**
     * Frame some edges using an explicit kind of frame
     * 
     * @param edges the edges to frame
     * @param kind The kind of frame
     * @return The framed edges
     */
    <T> Iterable<T> frameEdges(Iterable<Edge> edges, Class<T> kind);

    /**
     * Frame some edges using the return type of the method
     * 
     * @param edges the edges to frame
     * @param direction The direction of the edges
     * @return The framed edges
     */
    <T> Iterable<T> frameEdges(Iterable<Edge> edges, Direction direction);

    /**
     * Frame some edges using an explicit kind of frame
     * 
     * @param edges the edges to frame
     * @param direction The direction of the edges
     * @param kind The kind of frame
     * @return The framed edges
     */
    <T> Iterable<T> frameEdges(Iterable<Edge> edges, Direction direction, Class<T> kind);

}
