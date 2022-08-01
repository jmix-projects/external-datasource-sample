**ATTENTION: this repository is deprecated in favor of https://github.com/jmix-framework/jmix-samples**

# External Data Source Sample

The project demonstrates how to display and manage data received from the external data source using standard Jmix CRUD screens.

In the current example, external data source is a REST service provided by the separate application:

* `GET /tasks` returns a list of tasks
* `POST /tasks` creates a new task
* `POST /tasks/{id}` updates a task with a given id

This application may be found here: https://github.com/jmix-projects/external-datasource-boot-app

By default, it runs on port 18080, so the tasks URL will be `http://localhost:18080/tasks`.

## Loader Delegate and Commit Delegate

Jmix project contains `Task` DTO entity and its browser and editor screens.

The [TaskService.java](src/main/java/com/company/sample/app/TaskService.java) class makes HTTP requests to external API.

DTO list loading is implemented using the data loader delegate:

```java
@UiController("Task_.browse")
@UiDescriptor("task-browse.xml")
@LookupComponent("tasksTable")
public class TaskBrowse extends StandardLookup<Task> {

    @Autowired
    private TaskService taskService;

    @Install(to = "tasksDl", target = Target.DATA_LOADER)
    private List<Task> tasksDlLoadDelegate(LoadContext<Task> loadContext) {
        return taskService.loadTasks();
    }

    //...
}
```

DTO entity saving is implemented using the commit delegate in entity editor:

```java
@UiController("Task_.edit")
@UiDescriptor("task-edit.xml")
@EditedEntityContainer("taskDc")
public class TaskEdit extends StandardEditor<Task> {

    @Autowired
    private TaskService taskService;

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> commitDelegate(SaveContext saveContext) {
        Task task = getEditedEntity();
        Task savedTask = taskService.saveTask(task);
        return Set.of(savedTask);
    }
}
```

## Custom Data Store

Another approach for working with external datasources is to create a [custom data store](https://docs.jmix.io/jmix/data-model/data-stores.html#custom) for working with DTO entity. In this case, each time you load or save the DTO entity associated with the custom data store using the `DataManager` the entity will be read/saved by REST services instead of writing and reading from the database.

[ProjectDataStore.java](src/main/java/com/company/sample/datastore/ProjectDataStore.java) is the custom datastore that saves and loads projects using the REST service:

```java
@Component("sample_ProjectDataStore")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ProjectDataStore implements DataStore {

    @Autowired
    private ProjectService projectService;
    
    //...
    
    @Override
    public List<Object> loadList(LoadContext<?> context) {
        return new ArrayList<>(projectService.loadAll());
    }


    @Override
    public Set<?> save(SaveContext context) {
        Set<Project> savedEntities = context.getEntitiesToSave().stream()
                .map(entity -> projectService.save((Project) entity))
                .collect(Collectors.toSet());
        context.getEntitiesToRemove()
                .forEach(entity -> projectService.delete((Project) entity));
        return savedEntities;
    }
}
```

[ProjectDataStoreDescriptor.java](src/main/java/com/company/sample/datastore/ProjectDataStoreDescriptor.java) is the data store descriptor.

The data store must be declared in the `application.properties`:

```properties
jmix.core.additional-stores=projectds
jmix.core.store-descriptor_projectds = sample_ProjectDataStoreDescriptor
```

The [Project.java](src/main/java/com/company/sample/entity/Project.java) entity is associated with this `projectds` data store:

```java
@Store(name = "projectds")
@JmixEntity
public class Project {
    
    //...

}
```

Each time you work the `Project` entity using the `DataManager` the actual job will be done by `ProjectDataStore`:

```java
@UiController("Project.browse")
@UiDescriptor("project-browse.xml")
@LookupComponent("projectsTable")
public class ProjectBrowse extends StandardLookup<Project> {

    @Autowired
    private DataManager dataManager;

    @Install(to = "projectsDl", target = Target.DATA_LOADER)
    private List<Project> projectsDlLoadDelegate(LoadContext<Project> loadContext) {
        return dataManager.load(Project.class)
                .all()
                .list();
    }
}
```
