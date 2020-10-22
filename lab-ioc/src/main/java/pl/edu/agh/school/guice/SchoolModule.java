package pl.edu.agh.school.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import pl.edu.agh.school.persistence.PersistenceManager;
import pl.edu.agh.school.persistence.SerializablePersistenceManager;

public class SchoolModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PersistenceManager.class).to(SerializablePersistenceManager.class);
        bind(String.class).annotatedWith(Names.named("file_teachers"))
                .toInstance("file1.dat");
        bind(String.class).annotatedWith(Names.named("file_classes"))
                .toInstance("file2.dat");
    }
}
