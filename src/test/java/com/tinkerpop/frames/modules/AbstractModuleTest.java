package com.tinkerpop.frames.modules;

import junit.framework.Assert;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;
import org.mockito.Mockito;

import com.tinkerpop.frames.FramedGraphConfiguration;

public class AbstractModuleTest
{

    @Test
    public void testNoWrapping()
    {
        Graph baseGraph = Mockito.mock(Graph.class);

        FramedGraphConfiguration config = new FramedGraphConfiguration();
        AbstractModule module = Mockito.mock(AbstractModule.class);
        Mockito.when(module.doConfigure(Mockito.any(TinkerGraph.class), Mockito.any(FramedGraphConfiguration.class))).thenCallRealMethod();

        Graph configuredGraph = module.configure(baseGraph, config);
        Assert.assertEquals(baseGraph, configuredGraph);
        Mockito.verify(module).doConfigure(Mockito.any(TinkerGraph.class), Mockito.any(FramedGraphConfiguration.class));
        Mockito.verify(module).doConfigure(Mockito.any(FramedGraphConfiguration.class));

        Mockito.reset(module);
    }

    @Test
    public void testWrapping()
    {
        Graph baseGraph = Mockito.mock(Graph.class);

        Graph wrappedGraph = Mockito.mock(Graph.class);

        FramedGraphConfiguration config = new FramedGraphConfiguration();
        AbstractModule module = Mockito.mock(AbstractModule.class);
        Mockito.when(module.doConfigure(Mockito.any(TinkerGraph.class), Mockito.any(FramedGraphConfiguration.class))).thenReturn(wrappedGraph);

        Graph configuredGraph = module.configure(baseGraph, config);
        Assert.assertEquals(wrappedGraph, configuredGraph);

        Mockito.reset(module);
    }
}
