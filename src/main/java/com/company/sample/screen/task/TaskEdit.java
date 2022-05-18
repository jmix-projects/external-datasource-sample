package com.company.sample.screen.task;

import com.company.sample.app.TaskService;
import io.jmix.core.SaveContext;
import io.jmix.ui.screen.*;
import com.company.sample.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@UiController("Task_.edit")
@UiDescriptor("task-edit.xml")
@EditedEntityContainer("taskDc")
public class TaskEdit extends StandardEditor<Task> {

    @Autowired
    private TaskService taskService;

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> commitDelegate(SaveContext saveContext) {
        Task savedTask = taskService.saveTask(getEditedEntity());
        return Set.of(savedTask);
    }
}