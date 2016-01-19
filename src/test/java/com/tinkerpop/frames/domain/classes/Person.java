package com.tinkerpop.frames.domain.classes;

import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.domain.incidences.Created;
import com.tinkerpop.frames.domain.incidences.CreatedInfo;
import com.tinkerpop.frames.domain.incidences.Knows;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Person extends NamedObject
{
    enum Gender
    {
        FEMALE, MALE
    }

    @Property("age")
    Integer getAge();

    @Property("age")
    void setAge(Integer age);

    @Property("age")
    void removeAge();

    @Property("gender")
    void setGender(Gender gender);

    @Property("gender")
    Gender getGender();

    @Property("gender")
    void removeGender();

    @Incidence(label = "knows")
    Iterable<Knows> getKnows();

    @Adjacency(label = "knows")
    Iterable<Person> getKnowsPeople();

    @Adjacency(label = "knows")
    void setKnowsPeople(final Iterable<Person> knows);

    @Incidence(label = "created")
    Iterable<Created> getCreated();

    @Incidence(label = "created")
    Iterable<CreatedInfo> getCreatedInfo();

    @Adjacency(label = "created")
    Iterable<Project> getCreatedProjects();

    @Adjacency(label = "knows")
    void addKnowsPerson(final Person person);

    @Adjacency(label = "knows")
    Person addKnowsNewPerson();

    @Incidence(label = "knows")
    Knows addKnows(final Person person);

    @Adjacency(label = "created")
    void addCreatedProject(final Project project);

    @Incidence(label = "created")
    Created addCreated(final Project project);

    @Incidence(label = "created")
    CreatedInfo addCreatedInfo(final Project project);

    @Adjacency(label = "knows")
    void removeKnowsPerson(final Person person);

    @Incidence(label = "knows")
    void removeKnows(final Knows knows);

    @Adjacency(label = "latestProject")
    Project getLatestProject();

    @Adjacency(label = "latestProject")
    void setLatestProject(final Project latestProject);

    @GremlinGroovy("it.as('x').out('created').in('created').except('x')")
    Iterable<Person> getCoCreators();

    @GremlinGroovy("_().as('x').out('created').in('created').except('x').shuffle")
    Person getRandomCoCreators();

    @GremlinGroovy("_().as('x').out('created').in('created').except('x').has('age',age)")
    Person getCoCreatorOfAge(@GremlinParam("age") int age);

    @GremlinGroovy(value = "'aStringProperty'", frame = false)
    String getAStringProperty();

    @GremlinGroovy(value = "['a','b','c']", frame = false)
    Iterable<String> getListOfStrings();

    @GremlinGroovy("it.as('x').out('created').in('created').except('x').groupCount.cap.next()")
    Map<Person, Long> getRankedCoauthors();

    @GremlinGroovy("person.asVertex().in('knows')")
    Iterable<Person> getKnownRootedFromParam(@GremlinParam("person") Person person);

    @Deprecated
    @GremlinGroovy("_().out('knows')")
    Iterable<Person> getDeprecatedKnowsPeople();

    @Property("boolean")
    void setBoolean(boolean b);

    @Property("boolean")
    boolean isBooleanPrimitive();

    @Property("boolean")
    Boolean isBoolean();

    @Property("boolean")
    boolean canBooleanPrimitive();

    @Property("boolean")
    Boolean canBoolean();

    @Adjacency(label = "knows", direction = Direction.BOTH)
    void addKnowsPersonDirectionBothError(final Person person);

    @Adjacency(label = "knows", direction = Direction.BOTH)
    void setKnowsPersonDirectionBothError(final Person person);

    @Incidence(label = "created", direction = Direction.BOTH)
    Created addCreatedDirectionBothError(final Project project);

    @JavaHandler
    String getNameAndAge();

    @JavaHandler
    void notImplemented();

    abstract class Impl implements JavaHandlerContext<Vertex>, Person
    {
        @Override
        @JavaHandler
        public String getNameAndAge()
        {
            return getName() + " (" + getAge() + ")";
        }
    }

    void unhandledNoAnnotation();

    @GremlinGroovy("")
    void unhandledNoHandler();

}
