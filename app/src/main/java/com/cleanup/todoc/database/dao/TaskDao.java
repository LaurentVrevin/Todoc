package com.cleanup.todoc.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cleanup.todoc.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    //Récupère toutes les tasks via projectId
    @Query("SELECT * FROM Task WHERE projectId =:projectId")
    LiveData<List<Task>> getTaskByProject(long projectId);

    @Query("SELECT * FROM Task")
    LiveData<List<Task>> getTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

   @Delete
    void deleteTask(Task task);
}
