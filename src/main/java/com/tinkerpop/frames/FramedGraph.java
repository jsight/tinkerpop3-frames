package com.tinkerpop.frames;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedGraph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.tinkerpop.frames.annotations.AdjacencyAnnotationHandler;
import com.tinkerpop.frames.annotations.DomainAnnotationHandler;
import com.tinkerpop.frames.annotations.InVertexAnnotationHandler;
import com.tinkerpop.frames.annotations.IncidenceAnnotationHandler;
import com.tinkerpop.frames.annotations.OutVertexAnnotationHandler;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovyAnnotationHandler;
import com.tinkerpop.frames.modules.TypeResolver;
import com.tinkerpop.frames.structures.FramedEdgeIterable;
import com.tinkerpop.frames.structures.FramedVertexIterable;

/**
 * The primary class for interpreting/framing elements of a graph in terms of particulate annotated interfaces. This is a wrapper graph in that it
 * requires an underlying graph from which to add functionality. The standard Blueprints graph methods are exposed along with extra methods to make
 * framing easy.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedGraph<T extends TinkerGraph> implements Graph, WrappedGraph<T>
{

    protected final T baseGraph;

    private FramedGraphConfiguration config;
    private boolean configViaFactory;

    /**
     * @param baseGraph The original graph being framed.
     * @param config The configuration for the framed graph.
     */
    protected FramedGraph(T baseGraph, FramedGraphConfiguration config)
    {
        this.config = config;
        this.baseGraph = baseGraph;
        configViaFactory = true;
    }

    /**
     * Construct a FramedGraph that will frame the elements of the underlying graph.
     *
     * @param baseGraph the graph whose elements to frame
     * @deprecated Use {@link FramedGraphFactory}.
     */
    public FramedGraph(final T baseGraph)
    {
        this.baseGraph = baseGraph;
        config = new FramedGraphConfiguration();
        config.setConfiguredGraph(baseGraph);
        configViaFactory = false;
        this.config.addAnnotationHandler(new AdjacencyAnnotationHandler());
        this.config.addAnnotationHandler(new IncidenceAnnotationHandler());
        this.config.addAnnotationHandler(new DomainAnnotationHandler());
        this.config.addAnnotationHandler(new InVertexAnnotationHandler());
        this.config.addAnnotationHandler(new OutVertexAnnotationHandler());
        this.config.addAnnotationHandler(new GremlinGroovyAnnotationHandler());
    }

    /**
     * A helper method for framing a vertex. Note that all framed vertices implement {@link VertexFrame} to allow access to the underlying element
     *
     * @param vertex the vertex to frame
     * @param kind the default annotated interface to frame the vertex as
     * @param <F> the default type of the annotated interface
     * @return a proxy objects backed by a vertex and interpreted from the perspective of the annotate interface or null if the vertex parameter was
     *         null
     */
    public <F> F frame(final Vertex vertex, final Class<F> kind)
    {
        if (vertex == null)
        {
            return null;
        }

        Collection<Class<?>> resolvedTypes = new HashSet<Class<?>>();
        resolvedTypes.add(VertexFrame.class);
        resolvedTypes.add(kind);
        for (TypeResolver typeResolver : config.getTypeResolvers())
        {
            resolvedTypes.addAll(Arrays.asList(typeResolver.resolveTypes(
                        vertex, kind)));
        }
        return (F) Proxy.newProxyInstance(config.getFrameClassLoaderResolver().resolveClassLoader(kind),
                    resolvedTypes.toArray(new Class[resolvedTypes.size()]),
                    new FramedElement(this, vertex));
    }

    /**
     * A helper method for framing an edge. Note that all framed edges implement {@link EdgeFrame} to allow access to the underlying element.
     *
     * @param edge the edge to frame
     * @param kind the default annotated interface to frame the edges as
     * @param <F> the default type of the annotated interface
     * @return a proxy objects backed by an edge and interpreted from the perspective of the annotate interface or null if the edge paramenter was
     *         null
     */
    public <F> F frame(final Edge edge, final Class<F> kind)
    {
        if (edge == null)
        {
            return null;
        }

        Collection<Class<?>> resolvedTypes = new HashSet<Class<?>>();
        resolvedTypes.add(VertexFrame.class);
        resolvedTypes.add(kind);
        for (TypeResolver typeResolver : config.getTypeResolvers())
        {
            resolvedTypes.addAll(Arrays.asList(typeResolver.resolveTypes(
                        edge, kind)));
        }
        return (F) Proxy.newProxyInstance(config.getFrameClassLoaderResolver().resolveClassLoader(kind),
                    resolvedTypes.toArray(new Class[resolvedTypes.size()]),
                    new FramedElement(this, edge));
    }

    /**
     * A helper method for framing an edge. Note that all framed edges implement {@link EdgeFrame} to allow access to the underlying element
     *
     * @param edge the edge to frame
     * @param direction the direction of the edges
     * @param kind the default annotated interface to frame the edges as
     * @param <F> the default type of the annotated interface
     * @return a proxy objects backed by an edge and interpreted from the perspective of the annotate interface or null if the edge paramenter was
     *         null
     *
     * @deprecated Use {@link #frame(Edge, Class)}, in combination with {@link InVertex} and {@link OutVertex}.
     */
    public <F> F frame(final Edge edge, final Direction direction,
                final Class<F> kind)
    {
        if (edge == null)
        {
            return null;
        }

        Collection<Class<?>> resolvedTypes = new HashSet<Class<?>>();
        resolvedTypes.add(EdgeFrame.class);
        resolvedTypes.add(kind);
        for (TypeResolver typeResolver : config.getTypeResolvers())
        {
            resolvedTypes.addAll(Arrays.asList(typeResolver.resolveTypes(edge,
                        kind)));
        }
        return (F) Proxy.newProxyInstance(config.getFrameClassLoaderResolver().resolveClassLoader(kind),
                    resolvedTypes.toArray(new Class[resolvedTypes.size()]),
                    new FramedElement(this, edge, direction));
    }

    /**
     * A helper method for framing an iterable of edges.
     *
     * @param edges the edges to frame
     * @param direction the direction of the edges
     * @param kind the default annotated interface to frame the edges as
     * @param <F> the default type of the annotated interface
     * @return an iterable of proxy objects backed by an edge and interpreted from the perspective of the annotate interface
     *
     * @deprecated Use {@link #frameEdges(Iterable, Class)}, in combination with {@link InVertex} and {@link OutVertex}.
     */
    public <F> Iterable<F> frameEdges(final Iterable<Edge> edges,
                final Direction direction, final Class<F> kind)
    {
        return new FramedEdgeIterable<F>(this, edges, direction, kind);
    }

    /**
     * A helper method for framing an iterable of vertices.
     *
     * @param vertices the vertices to frame
     * @param kind the default annotated interface to frame the vertices as
     * @param <F> the default type of the annotated interface
     * @return an iterable of proxy objects backed by a vertex and interpreted from the perspective of the annotate interface
     */
    public <F> Iterable<F> frameVertices(final Iterable<Vertex> vertices,
                final Class<F> kind)
    {
        return new FramedVertexIterable<F>(this, vertices, kind);
    }

    /**
     * A helper method for framing an iterable of edges.
     *
     * @param edges the edges to frame
     * @param kind the default annotated interface to frame the edges as
     * @param <F> the default type of the annotated interface
     * @return an iterable of proxy objects backed by an edge and interpreted from the perspective of the annotate interface
     */
    public <F> Iterable<F> frameEdges(final Iterable<Edge> edges,
                final Class<F> kind)
    {
        return new FramedEdgeIterable<F>(this, edges, kind);
    }

    /**
     * Frame a vertex according to a particular kind of annotated interface.
     *
     * @param id the id of the vertex
     * @param kind the default annotated interface to frame the vertex as
     * @param <F> the default type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <F> F getVertex(final Object id, final Class<F> kind)
    {
        return this.frame(getBaseGraph().vertices(id).next(), kind);
    }

    /**
     * Add an edge to the underlying graph and return it as a framed edge.
     *
     * @param kind the default annotated interface to frame the vertex as
     * @param <E> the default type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <E> E addEdge(Vertex outVertex, Vertex inVertex, String label, final Class<E> kind)
    {
        Edge edge = outVertex.addEdge(label, inVertex);
        for (FrameInitializer initializer : config.getFrameInitializers())
        {
            initializer.initElement(kind, this, edge);
        }
        return this.frame(edge, kind);
    }

    /**
     * Add a vertex to the underlying graph and return it as a framed vertex.
     *
     * @param kind the default annotated interface to frame the vertex as
     * @param <F> the default type of the annotated interface
     * @return a proxy object backed by the vertex and interpreted from the perspective of the annotate interface
     */
    public <F> F addVertex(final Class<F> kind, Object... keyValues)
    {
        Vertex vertex = getBaseGraph().addVertex(keyValues);
        for (FrameInitializer initializer : config.getFrameInitializers())
        {
            initializer.initElement(kind, this, vertex);
        }
        return this.frame(vertex, kind);
    }

    /**
     * Frame an edge according to a particular kind of annotated interface.
     *
     * @param id the id of the edge
     * @param kind the default annotated interface to frame the edge as
     * @param <F> the default type of the annotated interface
     * @return a proxy object backed by the edge and interpreted from the perspective of the annotate interface
     */
    public <F> F getEdge(final Object id, final Class<F> kind)
    {
        return this.frame(getBaseGraph().edges(id).next(), kind);
    }

    public Features getFeatures()
    {
        return config.getConfiguredGraph().features();
    }

    public T getBaseGraph()
    {
        return this.baseGraph;
    }

    public String toString()
    {
        return StringFactory.graphString(this, this.baseGraph.toString());
    }

    private void checkFactoryConfig()
    {
        if (configViaFactory)
        {
            throw new UnsupportedOperationException("Unsupported for FramedGraph configured by factory");
        }
    }

    @Override
    public Vertex addVertex(Object... keyValues)
    {
        return getBaseGraph().addVertex(keyValues);
    }

    @Override
    public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException
    {
        return getBaseGraph().compute(graphComputerClass);
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException
    {
        return getBaseGraph().compute();
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds)
    {
        return getBaseGraph().vertices(vertexIds);
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds)
    {
        return getBaseGraph().edges(edgeIds);
    }

    @Override
    public Transaction tx()
    {
        return getBaseGraph().tx();
    }

    @Override
    public void close()
    {
        getBaseGraph().close();
    }

    @Override
    public Variables variables()
    {
        return getBaseGraph().variables();
    }

    @Override
    public Configuration configuration()
    {
        return getBaseGraph().configuration();
    }

    public FramedGraphConfiguration getConfig()
    {
        return config;
    }
}
