package com.cleanup.todoc.ui;

import androidx.annotation.Nullable;
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
    private LiveData<List<Project>> currentProject;


    public TaskViewModel(TaskRepository taskDataSource, ProjectRepository projectDataSource, Executor executor) {
        this.taskDataSource = taskDataSource;
        this.projectDataSource = projectDataSource;
        this.executor = executor;

    }

    //initialise le viewmodel dès que l'activité est créée.
    //Vérification si le project existe déjà dans le viewmodel (rotation, etc..)
    public void init(){
        if(currentProject==null){
            currentProject= projectDataSource.getProjects();
        }
    }

    @Nullable
    public LiveData<List<Project>> getProjects() {
        return currentProject;
    }

    public LiveData<List<Task>> getTasks() {
        return taskDataSource.getTasks();
    }

    public void createTask(Task task) {
        executor.execute(() -> taskDataSource.createTask(task));
    }

    public void deleteTask(Task task) {
        executor.execute(() -> taskDataSource.deleteTask(task));
    }
}


