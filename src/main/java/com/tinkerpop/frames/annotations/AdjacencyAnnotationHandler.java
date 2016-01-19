package com.tinkerpop.frames.annotations;

import java.lang.reflect.Method;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.structures.FramedVertexIterable;

public class AdjacencyAnnotationHandler implements AnnotationHandler<Adjacency>
{

    @Override
    public Class<Adjacency> getAnnotationType()
    {
        return Adjacency.class;
    }

    @Override
    public Object processElement(final Adjacency annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph,
                final Element element, final Direction direction)
    {
        if (element instanceof Vertex)
        {
            return processVertex(annotation, method, arguments, framedGraph, (Vertex) element);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    public Object processVertex(final Adjacency adjacency, final Method method, final Object[] arguments, final FramedGraph framedGraph,
                final Vertex vertex)
    {
        if (ClassUtilities.isGetMethod(method))
        {
            Iterable<Vertex> verticesIterable = () -> vertex.vertices(adjacency.direction(), adjacency.label());
            final FramedVertexIterable r = new FramedVertexIterable(framedGraph, verticesIterable,
                        ClassUtilities.getGenericClass(method));
            if (ClassUtilities.returnsIterable(method))
            {
                return r;
            }
            else
            {
                return r.iterator().hasNext() ? r.iterator().next() : null;
            }
        }
        else if (ClassUtilities.isAddMethod(method))
        {
            Class<?> returnType = method.getReturnType();
            Vertex newVertex;
            Object returnValue = null;
            if (arguments == null)
            {
                // Use this method to get the vertex so that the vertex
                // initializer is called.
                returnValue = framedGraph.addVertex(returnType);
                newVertex = ((VertexFrame) returnValue).asVertex();
            }
            else
            {
                if (arguments[0] == null)
                    throw new IllegalArgumentException("null passed to @Adjacency " + method.getName() + " labelled " + adjacency.label());
                newVertex = ((VertexFrame) arguments[0]).asVertex();
            }
            addEdges(adjacency, framedGraph, vertex, newVertex);

            if (returnType.isPrimitive())
            {
                return null;
            }
            else
            {
                return returnValue;
            }

        }
        else if (ClassUtilities.isRemoveMethod(method))
        {
            removeEdges(adjacency.direction(), adjacency.label(), vertex, ((VertexFrame) arguments[0]).asVertex(), framedGraph);
            return null;
        }
        else if (ClassUtilities.isSetMethod(method))
        {
            removeEdges(adjacency.direction(), adjacency.label(), vertex, null, framedGraph);
            if (ClassUtilities.acceptsIterable(method))
            {
                for (Object o : (Iterable) arguments[0])
                {
                    Vertex v = ((VertexFrame) o).asVertex();
                    addEdges(adjacency, framedGraph, vertex, v);
                }
                return null;
            }
            else
            {
                if (null != arguments[0])
                {
                    Vertex newVertex = ((VertexFrame) arguments[0]).asVertex();
                    addEdges(adjacency, framedGraph, vertex, newVertex);
                }
                return null;
            }
        }

        return null;
    }

    private void addEdges(final Adjacency adjacency, final FramedGraph framedGraph, final Vertex vertex, Vertex newVertex)
    {
        switch (adjacency.direction())
        {
        case OUT:
            vertex.addEdge(adjacency.label(), newVertex);
            // framedGraph.addEdge(null, vertex, newVertex, adjacency.label());
            break;
        case IN:
            newVertex.addEdge(adjacency.label(), vertex);
            // framedGraph.addEdge(null, newVertex, vertex, adjacency.label());
            break;
        case BOTH:
            throw new UnsupportedOperationException("Direction.BOTH it not supported on 'add' or 'set' methods");
        }
    }

    private void removeEdges(final Direction direction, final String label, final Vertex element, final Vertex otherVertex,
                final FramedGraph framedGraph)
    {
        Iterable<Edge> edgeIterable = () -> element.edges(direction, label);
        for (final Edge edge : edgeIterable)
        {
            if (null == otherVertex || edge.vertices(direction.opposite()).next().equals(otherVertex))
            {
                edge.remove();
            }
        }
    }
}