package com.tinkerpop.frames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.incidences.Knows;
import com.tinkerpop.frames.modules.AbstractModule;

/**
 * @author Bryn Cooke
 */
public class FrameInitializerTest
{

    private FramedGraph<TinkerGraph> framedGraph;

    @Before
    public void setup()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        FramedGraphFactory factory = new FramedGraphFactory(new AbstractModule()
        {
            @Override
            protected void doConfigure(FramedGraphConfiguration config)
            {
                config.addFrameInitializer(nameDefaulter);
                config.addFrameInitializer(weightDefaulter);
            }
        });
        this.framedGraph = factory.create(graph);
    }

    @Test
    public void testVertexInitialization()
    {
        Person person = framedGraph.addVertex(Person.class);
        assertEquals("Defaulted", person.getName());
    }

    @Test
    public void testEdgeInitialization()
    {
        Person person1 = framedGraph.addVertex(Person.class);
        Person person2 = framedGraph.addVertex(Person.class);
        person1.addKnows(person2);
        assertEquals(Float.valueOf(1.0f), person1.getKnows().iterator().next().getWeight());
    }

    public static FrameInitializer nameDefaulter = new FrameInitializer()
    {

        @Override
        public void initElement(Class<?> kind, FramedGraph<?> framedGraph, Element element)
        {
            if (kind == Person.class)
            {
                assertNotNull(framedGraph);
                element.property("name", "Defaulted");
            }
        }
    };

    public static FrameInitializer weightDefaulter = new FrameInitializer()
    {

        @Override
        public void initElement(Class<?> kind, FramedGraph<?> framedGraph, Element element)
        {
            assertNotNull(framedGraph);
            if (kind == Knows.class)
            {
                element.property("weight", 1.0f);
            }
        }
    };

}
