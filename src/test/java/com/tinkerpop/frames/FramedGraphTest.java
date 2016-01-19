package com.tinkerpop.frames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import junit.framework.Assert;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.GraphTest;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.tinkerpop.frames.annotations.AdjacencyAnnotationHandler;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.frames.annotations.DomainAnnotationHandler;
import com.tinkerpop.frames.annotations.InVertexAnnotationHandler;
import com.tinkerpop.frames.annotations.IncidenceAnnotationHandler;
import com.tinkerpop.frames.annotations.OutVertexAnnotationHandler;
import com.tinkerpop.frames.annotations.PropertyMethodHandler;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovyAnnotationHandler;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Knows;
import com.tinkerpop.frames.modules.MethodHandler;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedGraphTest extends GraphTest
{

    public void testDeprecatedAnnotationUnregister()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        FramedGraph<TinkerGraph> framedGraph = new FramedGraph<>(graph);

        int counter = framedGraph.getConfig().getAnnotationHandlers().size();
        for (AnnotationHandler a : new HashSet<AnnotationHandler>(framedGraph.getConfig().getAnnotationHandlers().values()))
        {
            assertTrue(framedGraph.getConfig().hasAnnotationHandler(a.getAnnotationType()));
            counter--;
            framedGraph.getConfig().unregisterAnnotationHandler(a.getAnnotationType());
            assertEquals(framedGraph.getConfig().getAnnotationHandlers().size(), counter);

        }
        assertEquals(framedGraph.getConfig().getAnnotationHandlers().size(), 0);
    }

    public void testDeprecatedConfigContainsCoreAnnotationHandlers()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        FramedGraph<TinkerGraph> framedGraph = new FramedGraph<>(graph);
        Collection<Class<?>> collections = Collections2.transform(
                    framedGraph.getConfig().getAnnotationHandlers().values(),
                    new Function<AnnotationHandler<?>, Class<?>>()
                    {
                        @Override
                        public Class<?> apply(AnnotationHandler<?> handler)
                        {
                            return handler.getClass();
                        }
                    });
        Assert.assertTrue(collections.containsAll(Arrays.asList(
                    AdjacencyAnnotationHandler.class,
                    IncidenceAnnotationHandler.class,
                    DomainAnnotationHandler.class,
                    InVertexAnnotationHandler.class,
                    OutVertexAnnotationHandler.class,
                    GremlinGroovyAnnotationHandler.class)));
    }

    public void testDeprecatedConfigRegisterAnnotationHandlers()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        FramedGraph<TinkerGraph> framedGraph = new FramedGraph<TinkerGraph>(graph);
        framedGraph.getConfig().getAnnotationHandlers().clear();
        MethodHandler<?> handler = new PropertyMethodHandler();
        framedGraph.getConfig().addMethodHandler(handler);
        Assert.assertEquals(1, framedGraph.getConfig().getMethodHandlers().size());
        Assert.assertTrue(framedGraph.getConfig().getMethodHandlers().containsValue(handler));

    }

    public void testFrameEquality()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        FramedGraph<TinkerGraph> framedGraph = new FramedGraphFactory().create(graph);

        assertEquals(framedGraph.frame(graph.vertices(1).next(), Person.class), framedGraph.getVertex(1, Person.class));
        assertEquals(framedGraph.frame(graph.edges(7).next(), Knows.class), framedGraph.getEdge(7, Knows.class));
    }

    public void testFrameVertices()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        FramedGraph<TinkerGraph> framedGraph = new FramedGraphFactory().create(graph);

        int counter = 0;
        GraphTraversal<Vertex, Vertex> markoTraversal = framedGraph.traversal().V().has("name", "marko");

        while (markoTraversal.hasNext())
        {
            Person person = framedGraph.frame(markoTraversal.next(), Person.class);
            counter++;
            assertEquals(person.getName(), "marko");
        }
        assertEquals(counter, 1);

        GraphTraversal<Vertex, Vertex> langJavaTraversal = framedGraph.traversal().V().has("lang", "java");
        counter = 0;
        while (langJavaTraversal.hasNext())
        {
            Project project = framedGraph.frame(langJavaTraversal.next(), Project.class);
            counter++;
            assertTrue(project.getName().equals("lop") || project.getName().equals("ripple"));
        }
        assertEquals(counter, 2);

    }

    public void testCreateFrame()
    {
        FramedGraph<TinkerGraph> framedGraph = generateGraph();
        Person person = framedGraph.addVertex(Person.class);
        assertEquals(person.asVertex(), graph.vertices().next());
        int counter = 0;

        Iterable<Vertex> vertexIterable = () -> graph.vertices();
        for (Vertex v : vertexIterable)
        {
            counter++;
        }
        assertEquals(counter, 1);
        counter = 0;
        Iterable<Edge> edgeIterable = () -> graph.edges();
        for (Edge e : edgeIterable)
        {
            counter++;
        }
        assertEquals(counter, 0);
        Person person2 = framedGraph.frame(framedGraph.addVertex("aPerson"), Person.class);
        assertEquals(person2.asVertex().id(), "aPerson");
        counter = 0;

        vertexIterable = () -> graph.vertices();
        for (Vertex v : vertexIterable)
        {
            counter++;
        }
        assertEquals(counter, 2);
    }

    public void testCreateFrameForNonexistantElements()
    {
        FramedGraph<TinkerGraph> framedGraph = generateGraph();
        Person vertex = framedGraph.getVertex(-1, Person.class);
        Assert.assertNull(vertex);
        vertex = framedGraph.frame((Vertex) null, Person.class);
        Assert.assertNull(vertex);

        Knows edge = framedGraph.getEdge(-1, Knows.class);
        Assert.assertNull(edge);
        edge = framedGraph.frame((Edge) null, Knows.class);
        Assert.assertNull(edge);

    }

    public FramedGraph<TinkerGraph> generateGraph()
    {
        final Configuration conf = new BaseConfiguration();
        conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_ID_MANAGER, TinkerGraph.DefaultIdManager.INTEGER.name());
        conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_EDGE_ID_MANAGER, TinkerGraph.DefaultIdManager.INTEGER.name());
        conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_PROPERTY_ID_MANAGER, TinkerGraph.DefaultIdManager.INTEGER.name());

        final TinkerGraph baseGraph = TinkerGraph.open(conf);

        return new FramedGraph<>(baseGraph);
    }

    public Graph generateGraph(final String directory)
    {
        return this.generateGraph();
    }

    private Iterable<Vertex> unwrapVertices(Iterable<VertexFrame> it)
    {
        return Iterables.transform(it, new Function<VertexFrame, Vertex>()
        {
            @Override
            public Vertex apply(VertexFrame input)
            {
                return input.asVertex();
            }
        });
    }

    private Iterable<Edge> unwrapEdges(Iterable<EdgeFrame> it)
    {
        return Iterables.transform(it, new Function<EdgeFrame, Edge>()
        {
            @Override
            public Edge apply(EdgeFrame input)
            {
                return input.asEdge();
            }
        });
    }

}
