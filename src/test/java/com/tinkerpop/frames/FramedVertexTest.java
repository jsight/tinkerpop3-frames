package com.tinkerpop.frames;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tinkerpop.frames.domain.classes.NamedObject;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.incidences.Created;
import com.tinkerpop.frames.domain.incidences.CreatedBy;
import com.tinkerpop.frames.domain.incidences.CreatedInfo;
import com.tinkerpop.frames.domain.incidences.Knows;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class FramedVertexTest
{
    private Graph graph;
    private FramedGraph<TinkerGraph> framedGraph;

    private Person marko;
    private Person vadas;
    private Project lop;
    private Person josh;
    private Project ripple;
    private Person peter;

    private Knows markoKnowsVadas;
    private Knows markowKnowsJosh;

    private CreatedInfo markoCreatedLop;
    private CreatedInfo joshCreatedRipple;
    private CreatedInfo joshCreatedLop;
    private CreatedInfo peterCreatedLop;

    @Before
    public void before()
    {
        TinkerGraph graph = TinkerFactory.createClassic();
        FramedGraph<TinkerGraph> framedGraph = new FramedGraphFactory().create(graph);

        marko = framedGraph.frame(graph.vertices(1).next(), Person.class);
        vadas = framedGraph.frame(graph.vertices(2).next(), Person.class);
        lop = framedGraph.frame(graph.vertices(3).next(), Project.class);
        josh = framedGraph.frame(graph.vertices(4).next(), Person.class);
        ripple = framedGraph.frame(graph.vertices(5).next(), Project.class);
        peter = framedGraph.frame(graph.vertices(6).next(), Person.class);

        markoKnowsVadas = framedGraph.frame(graph.edges(7).next(), Knows.class);
        markowKnowsJosh = framedGraph.frame(graph.edges(8).next(), Knows.class);

        markoCreatedLop = framedGraph.frame(graph.edges(9).next(), CreatedInfo.class);
        joshCreatedRipple = framedGraph.frame(graph.edges(10).next(), CreatedInfo.class);
        joshCreatedLop = framedGraph.frame(graph.edges(11).next(), CreatedInfo.class);
        peterCreatedLop = framedGraph.frame(graph.edges(12).next(), CreatedInfo.class);
    }

    @Test
    public void testGettingAdjacencies()
    {
        assertEquals(newHashSet(lop), newHashSet(marko.getCreatedProjects()));
        assertEquals(newHashSet(josh, vadas), newHashSet(marko.getKnowsPeople()));
        assertEquals(newHashSet(josh), newHashSet(newHashSet(ripple.getCreatedByPeople())));
    }

    @Test
    public void testSettingAdjacencies()
    {
        assertEquals(0, Iterables.size(josh.getKnowsPeople()));

        HashSet<Person> people = newHashSet(peter, marko);
        josh.setKnowsPeople(people);
        assertEquals(people, newHashSet(josh.getKnowsPeople()));

        people = newHashSet(josh);
        josh.setKnowsPeople(people);
        assertEquals(people, newHashSet(josh.getKnowsPeople()));
    }

    @Test
    public void testGettingAndSettingFunctionalAdjacencies()
    {
        Project rdfAgents = framedGraph.frame(graph.addVertex(), Project.class);
        Project tinkerNotes = framedGraph.frame(graph.addVertex(), Project.class);

        assertNull(josh.getLatestProject());

        josh.setLatestProject(rdfAgents);
        assertEquals(rdfAgents, josh.getLatestProject());

        josh.setLatestProject(tinkerNotes);
        assertEquals(tinkerNotes, josh.getLatestProject());

        josh.setLatestProject(null);
        assertNull(josh.getLatestProject());

        // It's safe to set an already-null object to null.
        josh.setLatestProject(null);
        assertNull(josh.getLatestProject());
    }

    @Test(expected = NullPointerException.class)
    public void testImproperSettingAdjacencies()
    {
        josh.setKnowsPeople(null);
    }

    @Test
    public void testGettingIncidences()
    {
        final CreatedInfo created = getOnlyElement(marko.getCreatedInfo());
        assertEquals("lop", created.getProject().getName());
        assertEquals(0.4f, created.getWeight(), 0.01f);

        assertEquals(newHashSet(markowKnowsJosh, markoKnowsVadas), newHashSet(marko.getKnows()));

        assertEquals(joshCreatedRipple, getOnlyElement(ripple.getCreatedInfo()));
    }

    /**
     * Uses deprecated Domain/Range annotations
     */
    @Test
    public void testGettingIncidencesDeprecated()
    {
        assertEquals(markoCreatedLop, getOnlyElement(marko.getCreated()));
        assertEquals(joshCreatedRipple, getOnlyElement(ripple.getCreatedBy()));
    }

    @Test
    public void testAddingIncidences()
    {
        CreatedInfo markoCreatedRipple = marko.addCreatedInfo(ripple);

        assertEquals(newHashSet(markoCreatedRipple, markoCreatedLop),
                    newHashSet(marko.getCreated()));
        assertNull(markoCreatedRipple.getWeight());
        markoCreatedRipple.setWeight(0.0f);
        assertEquals(0.0f, markoCreatedRipple.getWeight(), 0.01f);

        Knows markoKnowsPeter = marko.addKnows(peter);
        assertEquals(newHashSet(markoKnowsPeter, markoKnowsVadas, markowKnowsJosh),
                    newHashSet(marko.getKnows()));
        assertNull(markoKnowsPeter.getWeight());
        markoKnowsPeter.setWeight(1.0f);
        assertEquals(1.0f, markoKnowsPeter.getWeight(), 0.01f);
    }

    /**
     * Uses deprecated Domain/Range annotations
     */
    @Test
    public void testAddingIncidencesDeprecated()
    {
        Created markoCreatedRipple = marko.addCreated(ripple);
        assertEquals(newHashSet(markoCreatedRipple, markoCreatedLop),
                    newHashSet(marko.getCreated()));
        assertNull(markoCreatedRipple.getWeight());
        markoCreatedRipple.setWeight(0.0f);
        assertEquals(0.0f, markoCreatedRipple.getWeight(), 0.01f);
    }

    @Test
    public void testAddingAdjacencies()
    {
        marko.addKnowsPerson(peter);
        Person bryn = marko.addKnowsNewPerson();
        bryn.setName("bryn");

        Knows markoKnowsBryn = framedGraph.frame(graph.edges(13).next(), Knows.class);
        Knows markoKnowsPeter = framedGraph.frame(graph.edges(0).next(), Knows.class);

        assertEquals(newHashSet(markoKnowsVadas, markowKnowsJosh, markoKnowsPeter, markoKnowsBryn),
                    newHashSet(marko.getKnows()));

        marko.addCreatedProject(ripple);
        assertEquals(newHashSet(lop, ripple), newHashSet(marko.getCreatedProjects()));
    }

    @Test
    public void testAddingTwoAdjacencies()
    {
        Person person = framedGraph.addVertex(Person.class);
        HashSet<Person> people = newHashSet(person.addKnowsNewPerson(), person.addKnowsNewPerson());
        assertEquals(people, newHashSet(person.getKnowsPeople()));
    }

    @Test
    public void testRemoveIncidences()
    {
        assertEquals(newHashSet(markoKnowsVadas, markowKnowsJosh), newHashSet(marko.getKnows()));
        marko.removeKnows(markowKnowsJosh);
        assertEquals(markoKnowsVadas, getOnlyElement(marko.getKnows()));

        HashSet<CreatedInfo> toRemove = newHashSet(joshCreatedLop, markoCreatedLop, peterCreatedLop);
        assertEquals(toRemove, newHashSet(lop.getCreatedInfo()));
        for (CreatedInfo createdBy : toRemove)
        {
            lop.removeCreatedInfo(createdBy);
        }
        assertTrue(Iterables.isEmpty(lop.getCreatedInfo()));
    }

    /**
     * Uses deprecated Domain/Range annotations
     */
    @Test
    public void testRemoveIncidencesDeprecated()
    {
        List<CreatedBy> toRemove2 = new ArrayList<CreatedBy>();
        for (CreatedBy createdBy : lop.getCreatedBy())
        {
            toRemove2.add(createdBy);
        }
        assertEquals(3, toRemove2.size());
        for (CreatedBy createdBy : toRemove2)
        {
            lop.removeCreatedBy(createdBy);
        }
        assertTrue(Iterables.isEmpty(lop.getCreatedBy()));
    }

    @Test
    public void testRemovingAdjacencies()
    {
        marko.removeKnowsPerson(vadas);
        int counter = 0;
        Iterable<Edge> edgeIterable = () -> graph.vertices(1).next().edges(Direction.OUT, "knows");
        for (Edge edge : edgeIterable)
        {
            if (edge.label().equals("knows"))
            {
                counter++;
                assertEquals("josh", edge.inVertex().property("name").value());
            }
        }
        assertEquals(1, counter);
        assertEquals(josh, getOnlyElement(marko.getKnowsPeople()));

        lop.removeCreatedByPerson(marko);
        counter = 0;

        edgeIterable = () -> graph.vertices(3).next().edges(Direction.IN, "created");
        for (Edge edge : edgeIterable)
        {
            if (edge.label().equals("created"))
            {
                counter++;
                assertTrue(edge.outVertex().property("name").value().equals("josh")
                            || edge.outVertex().property("name").value().equals("peter"));
            }
        }
        assertEquals(2, counter);
        assertEquals(newHashSet(josh, peter), newHashSet(lop.getCreatedByPeople()));

    }

    @Test
    public void testVertexEquality()
    {
        NamedObject namedMarko = framedGraph.frame(graph.vertices(1).next(), NamedObject.class);
        NamedObject namedVadas = framedGraph.frame(graph.vertices(2).next(), NamedObject.class);
        assertTrue(marko.equals(marko));
        assertFalse(marko.equals(vadas));
        // The standard equals method will not consider different
        // framed interfaces with the same underlying vertex as equal
        assertEquals(marko.asVertex(), namedMarko.asVertex());
        assertEquals(marko, namedMarko);
        assertEquals(marko.asVertex(), namedMarko.asVertex());
        assertNotSame(marko.asVertex(), vadas.asVertex());
    }

    @Test
    public void testGetGremlinGroovy()
    {
        assertEquals(newHashSet(josh, peter), newHashSet(marko.getCoCreators()));
        assertEquals("aStringProperty", marko.getAStringProperty());
        Iterator<String> itty = marko.getListOfStrings().iterator();
        assertEquals(Lists.newArrayList("a", "b", "c"), Lists.newArrayList(itty));
    }

    @Test
    public void testGetGremlinGroovySingleItem()
    {
        assertTrue(newHashSet(josh, peter).contains(marko.getRandomCoCreators()));
    }

    @Test
    public void testGetGremlinGroovyParameters()
    {
        Person coCreator = marko.getCoCreatorOfAge(32);
        assertEquals(josh, coCreator);
        coCreator = marko.getCoCreatorOfAge(35);
        assertEquals(peter, coCreator);

        assertEquals(marko, getOnlyElement(marko.getKnownRootedFromParam(josh)));
    }

    @Test
    public void testMapReturnType()
    {
        Map<Person, Long> coauthors = marko.getRankedCoauthors();
        Map<Person, Long> correct = Maps.newHashMap();
        correct.put(peter, 1l);
        correct.put(josh, 1l);
        assertEquals(correct, coauthors);
    }

    @Test
    public void testBooleanGetMethods()
    {
        Person marko = framedGraph.frame(graph.vertices(1).next(), Person.class);
        marko.setBoolean(true);
        assertTrue(marko.isBoolean());
        assertTrue(marko.isBooleanPrimitive());
        assertTrue(marko.canBoolean());
        assertTrue(marko.canBooleanPrimitive());
    }

    @Test
    public void testDeprecatedKnowsPeople()
    {
        Person marko = framedGraph.frame(graph.vertices(1).next(), Person.class);
        assertEquals(newHashSet(vadas, josh), newHashSet(marko.getDeprecatedKnowsPeople()));
    }

    @Test
    public void testFramingInterfaces()
    {
        StandalonePerson marko = framedGraph.frame(graph.vertices(1).next(), StandalonePerson.class);
        assertTrue(marko instanceof VertexFrame);
        for (Knows knows : marko.getKnows())
        {
            assertTrue(knows instanceof EdgeFrame);
        }
    }

    public static interface StandalonePerson
    {

        @Incidence(label = "knows")
        public Iterable<Knows> getKnows();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAdjacencyBothError()
    {
        marko.addKnowsPersonDirectionBothError(peter);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetAdjacencyBothError()
    {
        marko.setKnowsPersonDirectionBothError(peter);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddIncidenceBothError()
    {
        Project rdfAgents = framedGraph.frame(graph.addVertex(), Project.class);
        marko.addCreatedDirectionBothError(rdfAgents);
    }

    @Test
    public void testAddAdjacencyIn()
    {
        Project rdfAgents = framedGraph.addVertex(Project.class);
        rdfAgents.addCreatedByPersonAdjacency(marko);
        assertEquals(marko, getOnlyElement(rdfAgents.getCreatedByPeople()));
    }

    @Test
    public void testAddIncidenceIn()
    {
        Project rdfAgents = framedGraph.addVertex(Project.class);
        CreatedInfo createdInfo = rdfAgents.addCreatedByPersonInfo(marko);

        assertEquals(marko, createdInfo.getPerson());
        assertEquals(rdfAgents, createdInfo.getProject());
        assertEquals(marko, getOnlyElement(rdfAgents.getCreatedByPeople()));
    }
}
