package com.cleanup.todoc.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.cleanup.todoc.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    //Récupère toutes les tasks via projectId
    @Query("SELECT * FROM Task WHERE projectId =:projectId")
    LiveData<List<Task>>getTasks(long projectId);

    @Insert
    long insertTask(Task task);

    @Query("DELETE FROM Task WHERE id =:taskId")
    int deleteTask(long taskId);
}
