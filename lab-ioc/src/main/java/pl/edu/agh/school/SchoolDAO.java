package pl.edu.agh.school;

import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.persistence.PersistenceManager;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class SchoolDAO {

    public static final Logger log = Logger.getInstance();

    private final List<Teacher> teachers;

    private final List<SchoolClass> classes;

    private final PersistenceManager manager;

    @Inject
    public SchoolDAO(PersistenceManager persistenceManager) {
        manager = persistenceManager;
        teachers = manager.loadTeachers();
        classes = manager.loadClasses();
    }

    public void addTeacher(Teacher teacher) {
        if (!teachers.contains(teacher)) {
            teachers.add(teacher);
            manager.saveTeachers(teachers);
            log.log("Added " + teacher.toString());
        }
    }

    public void addClass(SchoolClass newClass) {
        if (!classes.contains(newClass)) {
            classes.add(newClass);
            manager.saveClasses(classes);
            log.log("Added " + newClass.toString());
        }
    }

    public List<SchoolClass> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    public List<Teacher> getTeachers() {
        return Collections.unmodifiableList(teachers);
    }
}
