package com.company.sample.screen.project;

import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.ui.screen.*;
import com.company.sample.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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