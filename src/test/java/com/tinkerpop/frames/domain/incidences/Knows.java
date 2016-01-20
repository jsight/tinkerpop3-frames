package com.tinkerpop.frames.domain.incidences;

import org.apache.tinkerpop.gremlin.structure.Edge;

import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.OutVertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Knows
{
    @Property("weight")
    Float getWeight();

    @Property("weight")
    Float setWeight(float weight);

    @OutVertex
    Person getOut();

    @InVertex
    Person getIn();

    abstract class Impl implements Knows, JavaHandlerContext<Edge>
    {
    }

}
