package com.cleanup.todoc.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.repository.ProjectRepository;
import com.cleanup.todoc.repository.TaskRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class TaskViewModel extends ViewModel {

    //REPOSITORIES
    private final TaskRepository taskDataSource;
    private final ProjectRepository projectDataSource;
    private final Executor executor;

    //DATA
    private LiveData<List<Project>>currentProject;


    public TaskViewModel(TaskRepository taskDataSource, ProjectRepository projectDataSource, Executor executor) {
        this.taskDataSource = taskDataSource;
        this.projectDataSource = projectDataSource;
        this.executor = executor;
    }

    public void init(long projectId){
        if(this.currentProject!=null){
            return;
        }
        currentProject= projectDataSource.getProject(projectId);
    }
    //FOR PROJECTS
    public LiveData<List<Project>> getAllProjects(){
        return projectDataSource.getAllProject();
    }

    //FOR TASKS
    public LiveData<List<Task>>getAllTasks(){
        return taskDataSource.getAllTask();
    }
    public LiveData<List<Task>> getTask(long projectId){
        return taskDataSource.getTask(projectId);
    }

    public void createTask(long id, long projectId, @NonNull String name, long creationTimestamp){
        executor.execute(()->{
            taskDataSource.createTask(new Task(id, projectId, name, creationTimestamp));
        });
    }
    public void deleteTask(long taskId){
        executor.execute(() ->taskDataSource.deleteTask(taskId));
    }

}
