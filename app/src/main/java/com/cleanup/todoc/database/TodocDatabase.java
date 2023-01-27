package com.cleanup.todoc.database;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.database.dao.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;


@Database(entities = {Project.class, Task.class}, version = 1, exportSchema = false)
public abstract class TodocDatabase extends RoomDatabase {

    // SINGLETON
    private static volatile TodocDatabase INSTANCE;

    // INSTANCE
    public static TodocDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TodocDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TodocDatabase.class, "TodocDatabase.db")
                            .addCallback(prepopulateDatabase())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback prepopulateDatabase() {
        return new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                Project[] projects = new Project[]{
                        new Project(1L, "Projet Tartampion", 0xFFEADAD1),
                        new Project(2L, "Projet Lucidia", 0xFFB4CDBA),
                        new Project(3L, "Projet Circus", 0xFFA3CED2),
                };
                for (Project project : projects) {


                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", project.getId());
                    contentValues.put("name", project.getName());
                    contentValues.put("color", project.getColor());
                    db.insert("projects", OnConflictStrategy.REPLACE, contentValues);
                }
            }
        };
    }

    // DAO
    public abstract TaskDao taskDao();

    public abstract ProjectDao projectDao();
}
