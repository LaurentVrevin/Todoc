package com.cleanup.todoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.cleanup.todoc.database.TodocDatabase;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {

    //DATA SET FOR TEST
    private static final Project PROJECT_01 = new Project(1, "Project test 01", 0xFFEADAD1);
    private static final Task TASK_DEMO_01 = new Task(1, 1, "Task test 01", new Date().getTime());
    private static final Task TASK_DEMO_02 = new Task(2, 1, "Task test 02", new Date().getTime());
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    //FOR DATA
    private TodocDatabase todocDatabase;

    @Before

    public void initDb() throws Exception {
        this.todocDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),
                TodocDatabase.class).allowMainThreadQueries().build();
    }

    @After
    public void closeDB() throws Exception {
        todocDatabase.close();
    }

    @Test
    public void insertAndGetProject() throws Exception {
        //Insert project in database
        //Values added :  "1, "Project test",0xFFEADAD1"
        this.todocDatabase.projectDao().createProject(PROJECT_01);
        // Récupère la liste des projets de la base de données en utilisant la méthode getProject de la classe ProjectDao
        List<Project> projects = LiveDataTestUtil.getValue(this.todocDatabase.projectDao().getProjects());
        Project project = projects.get(0);
        //Check if name of project and id of project are the same project
        assertTrue(project.getName().equals(PROJECT_01.getName()) && project.getId() == 1);
    }

    @Test
    public void insertAndGetTasks() throws Exception {
        List<Task> tasks = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTasks());
        assertEquals(0, tasks.size());
        this.todocDatabase.projectDao().createProject(PROJECT_01);
        this.todocDatabase.taskDao().insertTask(TASK_DEMO_01);
        this.todocDatabase.taskDao().insertTask(TASK_DEMO_02);
        //Get all tasks form project 01
        List<Task> taskList = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTasks());
        //assert taskList size = 2
        assertEquals(2, taskList.size());
    }

    @Test
    public void insertAndDeleteTask() throws InterruptedException {
        this.todocDatabase.projectDao().createProject(PROJECT_01);
        //Insert all tasks
        this.todocDatabase.taskDao().insertTask(TASK_DEMO_01);
        this.todocDatabase.taskDao().insertTask(TASK_DEMO_02);
        //Get tasks from project 01 and check if taskadded from PROJECT_01 = 2
        List<Task> taskProject01 = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTaskByProject(1));
        assertEquals(2, taskProject01.size());
        //Delete one Task from project01 and check taskdeleted = 1
        this.todocDatabase.taskDao().deleteTask(TASK_DEMO_01);
        List<Task> taskdeleted = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTaskByProject(1));
        assertEquals(1, taskdeleted.size());
    }


}
