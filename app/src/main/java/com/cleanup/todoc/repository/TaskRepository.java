package com.cleanup.todoc.repository;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.database.dao.TaskDao;
import com.cleanup.todoc.model.Task;

import java.util.List;

public class TaskRepository {
    private final TaskDao taskDao;

    public TaskRepository(TaskDao taskDao) { this.taskDao = taskDao; }

    //GET

    public LiveData<List<Task>> getTask(long projectId){ return this.taskDao.getTasks(projectId); }

    //CREATE

    public void createTask(Task task){ taskDao.insertTask(task); }


    //DELETE
    public void deleteTask(long taskId){taskDao.deleteTask(taskId);}


}
