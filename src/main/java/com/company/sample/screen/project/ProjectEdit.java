package com.company.sample.screen.project;

import io.jmix.ui.screen.*;
import com.company.sample.entity.Project;

@UiController("Project.edit")
@UiDescriptor("project-edit.xml")
@EditedEntityContainer("projectDc")
public class ProjectEdit extends StandardEditor<Project> {
}