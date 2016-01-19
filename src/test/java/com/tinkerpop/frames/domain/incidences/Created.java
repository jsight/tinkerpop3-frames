package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.domain.classes.Project;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Created extends EdgeFrame
{
    // Please note: Domain and Range are incorrectly annotated here, they should be inverted if you want this interface working
    // with @Incidence annotations. See FramedEdgeTest.testGettingDomainAndRange.

    @Domain
    Project getDomain();

    @Property("weight")
    Float getWeight();

    @Property("weight")
    void setWeight(float weight);
}
