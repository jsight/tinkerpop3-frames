package com.tinkerpop.frames.modules.typedgraph;

import junit.framework.TestCase;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public class TypedGraphModuleTest extends TestCase
{
    public static @TypeField("type") interface Base
    {
        @Property("label")
        String getLabel();
    }

    public static @TypeValue("A") interface A extends Base
    {
    }

    public static @TypeValue("B") interface B extends Base
    {
    }

    public static @TypeValue("C") interface C extends B
    {
        @Property("label")
        void setLabel(String label);

        @InVertex
        <T extends Base> T getInVertex();
    }

    public void testSerializeVertexType()
    {
        TinkerGraph graph = TinkerGraph.open();
        FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
                    .withClass(C.class).build());
        FramedGraph<TinkerGraph> framedGraph = factory.create(graph);
        A a = framedGraph.addVertex(A.class);
        C c = framedGraph.addVertex(C.class);
        assertEquals("A", ((VertexFrame) a).asVertex().property("type").value());
        assertEquals("C", ((VertexFrame) c).asVertex().property("type").value());
    }

    public void testDeserializeVertexType()
    {
        TinkerGraph graph = TinkerGraph.open();
        FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
                    .withClass(C.class).build());
        FramedGraph<TinkerGraph> framedGraph = factory.create(graph);
        Vertex cV = graph.addVertex();
        cV.property("type", "C");
        cV.property("label", "C Label");

        Base c = framedGraph.getVertex(cV.id(), Base.class);
        assertTrue(c instanceof C);
        assertEquals("C Label", c.getLabel());
        ((C) c).setLabel("new label");
        assertEquals("new label", cV.property("label").value());
    }

    public void testSerializeEdgeType()
    {
        TinkerGraph graph = TinkerGraph.open();
        FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
                    .withClass(C.class).build());
        FramedGraph<TinkerGraph> framedGraph = factory.create(graph);
        Vertex v1 = graph.addVertex();
        Vertex v2 = graph.addVertex();
        A a = framedGraph.addEdge(v1, v2, "label", A.class);
        C c = framedGraph.addEdge(v1, v2, "label", C.class);
        assertEquals("A", ((EdgeFrame) a).asEdge().property("type").value());
        assertEquals("C", ((EdgeFrame) c).asEdge().property("type").value());
    }

    public void testDeserializeEdgeType()
    {
        TinkerGraph graph = TinkerGraph.open();
        FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
                    .withClass(C.class).build());
        FramedGraph<TinkerGraph> framedGraph = factory.create(graph);
        Vertex v1 = graph.addVertex();
        Vertex v2 = graph.addVertex();
        Edge cE = v1.addEdge("label", v2);
        cE.property("type", "C");
        Base c = framedGraph.getEdge(cE.id(), Base.class);
        assertTrue(c instanceof C);
    }

    public void testWildcard()
    {
        TinkerGraph graph = TinkerGraph.open();
        FramedGraphFactory factory = new FramedGraphFactory(new TypedGraphModuleBuilder().withClass(A.class).withClass(B.class)
                    .withClass(C.class).build());
        FramedGraph<TinkerGraph> framedGraph = factory.create(graph);
        Vertex v1 = graph.addVertex();

        Vertex v2 = graph.addVertex();
        v2.property("type", "A");
        Edge cE = v1.addEdge("label", v2);
        cE.property("type", "C");
        Base c = framedGraph.getEdge(cE.id(), Base.class);
        assertTrue(c instanceof C);
        assertTrue(((C) c).getInVertex() instanceof A);

    }
}
