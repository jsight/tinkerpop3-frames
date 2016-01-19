package com.tinkerpop.frames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.CreatedInfo;
import com.tinkerpop.frames.domain.incidences.Knows;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedEdgeTest
{

    private FramedGraph<TinkerGraph> framedGraph;
    private Person marko;
    private Person vadas;
    private Person peter;
    private Person josh;
    private Knows knows;
    private Project lop;

    @Before
    public void setup()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        framedGraph = new FramedGraphFactory().create(graph);

        marko = framedGraph.getVertex(1, Person.class);
        vadas = framedGraph.getVertex(2, Person.class);
        peter = framedGraph.getVertex(6, Person.class);
        josh = framedGraph.getVertex(4, Person.class);
        knows = framedGraph.getEdge(7, Knows.class);
        lop = framedGraph.getVertex(3, Project.class);
    }

    @Test
    public void testGettingOutAndIn()
    {

        assertEquals(marko, knows.getOut());
        assertEquals(vadas, knows.getIn());

        CreatedInfo created = lop.getCreatedInfo().iterator().next();
        assertEquals(lop, created.getProject());
        assertTrue(created.getPerson().equals(marko) || created.getPerson().equals(peter) || created.getPerson().equals(josh));

        created = marko.getCreatedInfo().iterator().next();
        assertEquals(lop, created.getProject());
        assertEquals(marko, created.getPerson());
    }

}
