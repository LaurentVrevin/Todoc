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

    public LiveData<List<Project>>getAllProject(){
        return this.projectDao.getAllProjects();
    }

    public LiveData<List<Project>> getProject(long id){
        return this.projectDao.getProject(id);
    }
}
