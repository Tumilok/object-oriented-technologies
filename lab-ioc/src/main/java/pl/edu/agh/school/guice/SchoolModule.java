package pl.edu.agh.school.guice;

import com.google.inject.AbstractModule;
import pl.edu.agh.school.persistence.PersistenceManager;
import pl.edu.agh.school.persistence.SerializablePersistenceManager;

public class SchoolModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PersistenceManager.class).to(SerializablePersistenceManager.class);
    }
}
