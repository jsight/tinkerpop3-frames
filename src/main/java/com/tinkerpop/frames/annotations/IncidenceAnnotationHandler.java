package com.tinkerpop.frames.annotations;

import java.lang.reflect.Method;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.structures.FramedEdgeIterable;

public class IncidenceAnnotationHandler implements AnnotationHandler<Incidence>
{

    @Override
    public Class<Incidence> getAnnotationType()
    {
        return Incidence.class;
    }

    @Override
    public Object processElement(final Incidence annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph,
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

    public Object processVertex(final Incidence incidence, final Method method, final Object[] arguments, final FramedGraph framedGraph,
                final Vertex element)
    {
        if (ClassUtilities.isGetMethod(method))
        {
            Iterable<Edge> edgesIterable = () -> element.edges(incidence.direction(), incidence.label());

            return new FramedEdgeIterable(framedGraph, edgesIterable, incidence.direction(),
                        ClassUtilities.getGenericClass(method));
        }
        else if (ClassUtilities.isAddMethod(method))
        {
            VertexFrame vertexFrame = ((VertexFrame) arguments[0]);
            Edge result;

            switch (incidence.direction())
            {
            case OUT:
                result = element.addEdge(incidence.label(), vertexFrame.asVertex());
                return framedGraph.frame(result, method.getReturnType());
            // return framedGraph.addEdge(null, element, vertexFrame.asVertex(), incidence.label(), Direction.OUT,
            // method.getReturnType());
            case IN:
                result = vertexFrame.asVertex().addEdge(incidence.label(), element);
                return framedGraph.frame(result, method.getReturnType());
            // return framedGraph.addEdge(null, vertexFrame.asVertex(), element, incidence.label(), Direction.IN,
            // method.getReturnType());
            case BOTH:
                throw new UnsupportedOperationException("Direction.BOTH it not supported on 'add' or 'set' methods");
            }

        }
        else if (ClassUtilities.isRemoveMethod(method))
        {
            Edge edge = ((EdgeFrame) arguments[0]).asEdge();
            edge.remove();
            return null;
        }

        return null;
    }

}
