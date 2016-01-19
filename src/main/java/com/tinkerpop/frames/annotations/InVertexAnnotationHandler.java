package com.tinkerpop.frames.annotations;

import java.lang.reflect.Method;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.InVertex;

public class InVertexAnnotationHandler implements AnnotationHandler<InVertex>
{
    @Override
    public Class<InVertex> getAnnotationType()
    {
        return InVertex.class;
    }

    @Override
    public Object processElement(final InVertex annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph,
                final Element element, final Direction direction)
    {
        if (element instanceof Edge)
        {
            Edge edge = (Edge) element;
            Vertex vertex = edge.inVertex();
            return framedGraph.frame(vertex, method.getReturnType());
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
}
