package com.tinkerpop.frames.core;

import java.util.Iterator;
import java.util.function.Predicate;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphQuery;
import com.tinkerpop.frames.structures.FramedEdgeIterable;
import com.tinkerpop.frames.structures.FramedVertexIterable;

public class FramedGraphQueryImpl implements FramedGraphQuery
{
    private FramedGraph<?> graph;
    private GraphTraversal<?, ?> traversal;

    public FramedGraphQueryImpl(FramedGraph<?> graph)
    {
        this.graph = graph;
        this.traversal = new DefaultGraphTraversal<Object, Object>(graph);
    }

    public FramedGraphQuery has(String key)
    {
        this.traversal = this.traversal.V().has(key);
        return this;
    }

    public FramedGraphQuery hasNot(String key)
    {
        this.traversal = this.traversal.V().hasNot(key);
        return this;
    }

    public FramedGraphQuery has(String key, Object value)
    {
        this.traversal = this.traversal.V().has(key, value);
        return this;
    }

    public FramedGraphQuery has(String key, Predicate predicate)
    {
        this.traversal = this.traversal.V().has(key, predicate);
        return this;
    }

    public FramedGraphQuery limit(int limit)
    {
        this.traversal = this.traversal.V().limit(limit);
        return this;
    }

    @Override
    public <T> Iterable<T> edges(Class<T> kind)
    {
        Iterable<Edge> edgesIterable = new Iterable<Edge>()
        {
            @Override
            public Iterator<Edge> iterator()
            {
                return graph.edges();
            }
        };

        return new FramedEdgeIterable<T>(graph, edgesIterable, kind);
    }

    @Override
    public <T> Iterable<T> vertices(Class<T> kind)
    {
        Iterable<Vertex> verticesIterable = new Iterable<Vertex>()
        {
            @Override
            public Iterator<Vertex> iterator()
            {
                return graph.vertices();
            }
        };

        return new FramedVertexIterable<T>(graph, verticesIterable, kind);
    }

}
