package com.tinkerpop.frames.structures;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.domain.classes.Person;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedVertexSetTest extends TestCase
{

    public void testFramedSet()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        FramedGraph<TinkerGraph> framedGraph = new FramedGraph<>(graph);
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(graph.vertices(1).next());
        vertices.add(graph.vertices(4).next());
        vertices.add(graph.vertices(6).next());
        FramedVertexSet<Person> set = new FramedVertexSet<>(framedGraph, vertices, Person.class);
        assertEquals(set.size(), 3);
        assertTrue(set.contains(graph.vertices(1).next()));
        assertTrue(set.contains(graph.vertices(4).next()));
        assertTrue(set.contains(graph.vertices(6).next()));
        assertTrue(set.contains(framedGraph.frame(graph.vertices(1).next(), Person.class)));
        assertTrue(set.contains(framedGraph.frame(graph.vertices(4).next(), Person.class)));
        assertTrue(set.contains(framedGraph.frame(graph.vertices(6).next(), Person.class)));

        int counter = 0;
        for (Person person : set)
        {
            assertTrue(person.asVertex().id().equals("1") || person.asVertex().id().equals("4") || person.asVertex().id().equals("6"));
            counter++;
        }
        assertEquals(counter, 3);
    }
}
