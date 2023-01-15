package com.cleanup.todoc.repository;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.model.Project;

import java.util.List;

public class ProjectRepository {

    private ProjectDao projectDao;

    public ProjectRepository(ProjectDao projectDao){
        this.projectDao=projectDao;
    }

    // GET PROJECT
    //On récupère tous les projects
    public LiveData<List<Project>> getProjects(){
        return this.projectDao.getProjects();
    }

    //CREATE
    public void createProject(Project project){ projectDao.createProject(project);
    }


}
