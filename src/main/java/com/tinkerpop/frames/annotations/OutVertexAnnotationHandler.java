package com.tinkerpop.frames.annotations;

import java.lang.reflect.Method;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;

import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.OutVertex;

public class OutVertexAnnotationHandler implements AnnotationHandler<OutVertex>
{
    @Override
    public Class<OutVertex> getAnnotationType()
    {
        return OutVertex.class;
    }

    @Override
    public Object processElement(final OutVertex annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph,
                final Element element, final Direction direction)
    {
        if (element instanceof Edge)
        {
            return framedGraph.frame(((Edge) element).vertices(Direction.OUT).next(), method.getReturnType());
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
}
