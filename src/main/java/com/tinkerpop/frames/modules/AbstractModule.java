package com.tinkerpop.frames.modules;

import org.apache.tinkerpop.gremlin.structure.Graph;

import com.tinkerpop.frames.FramedGraphConfiguration;

/**
 * Helper base module to simplify configuring different types of graph. Override doConfigure for the appropriate type of graph.
 * 
 * @author Bryn Cooke
 *
 */
public class AbstractModule implements Module
{

    @Override
    public final Graph configure(Graph baseGraph, FramedGraphConfiguration config)
    {
        baseGraph = doConfigure(baseGraph, config);
        doConfigure(config);
        return baseGraph;
    }

    /**
     * @param baseGraph The graph being framed.
     * @param config The configuration for the new FramedGraph.
     * @return The graph being framed.
     */
    protected Graph doConfigure(Graph baseGraph, FramedGraphConfiguration config)
    {
        return baseGraph;
    }

    /**
     * Perform common configuration across all graph types.
     * 
     * @param config
     */
    protected void doConfigure(FramedGraphConfiguration config)
    {

    }

}
