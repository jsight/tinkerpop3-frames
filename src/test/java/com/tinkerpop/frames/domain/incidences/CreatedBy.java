package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.domain.classes.Project;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface CreatedBy
{

    @Domain
    Project getDomain();

    @Property("weight")
    Float getWeight();
}
