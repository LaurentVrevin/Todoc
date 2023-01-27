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

    //FOR DATA
    private TodocDatabase todocDatabase;

    //DATA SET FOR TEST
    //set data for test

    private static final long  PROJECT_ID = 1;
    private static final Project PROJECT_DEMO = new Project(PROJECT_ID, "Project test",0xFFEADAD1);
    private static final Task TASK_DEMO_01 = new Task(1, PROJECT_ID,"Task test 01", new Date().getTime());
    private static final Task TASK_DEMO_02 = new Task(2, PROJECT_ID,"Task test 02", new Date().getTime());

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

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
        // Insère un projet dans la base de données en appelant la méthode createProject de la classe ProjectDao
        // Les valeurs que j'ajoute : "1, "Project test",0xFFEADAD1"
        this.todocDatabase.projectDao().createProject(PROJECT_DEMO);
        // Récupère la liste des projets de la base de données en utilisant la méthode getProject de la classe ProjectDao
        List<Project> projects = LiveDataTestUtil.getValue(this.todocDatabase.projectDao().getProjects());

        Project project = projects.get(0);

        //Vérifie maintenant si le nom du projet et son Id correspondent, alors renvoie true.
        assertTrue( project.getName().equals(PROJECT_DEMO.getName()) && project.getId() == PROJECT_ID);
    }

    @Test
    public void insertAndGetTasks() throws Exception {
        List<Task> tasks = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTasks());
        assertEquals(0, tasks.size());
        //insère un projet dans la bdd
        this.todocDatabase.projectDao().createProject(PROJECT_DEMO);
        //insère une tâche dans la bdd avec insertTask pour la classe TaskDao
        this.todocDatabase.taskDao().insertTask(TASK_DEMO_01);
        this.todocDatabase.taskDao().insertTask(TASK_DEMO_02);
        // Récupère la liste des tâches de la base de données en utilisant getTask de la classe TaskDao
        List<Task> taskList = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTasks());
        //Confirme qu'il y a bien une tâche dans la liste
        assertEquals(2, taskList.size());
    }

    @Test
    public void insertAndDeleteTask() throws InterruptedException {
        //Insérer un projet
        this.todocDatabase.projectDao().createProject(PROJECT_DEMO);
        //Insérer une tâche
        this.todocDatabase.taskDao().insertTask(TASK_DEMO_01);
        //Je récupère la liste des tâches via la bdd  en utilisant getTask de la classe TaskDao
        List<Task> taskadded = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTaskByProject(PROJECT_ID));
        assertTrue(taskadded.size() == 1);
        //supprimer la tâche
        this.todocDatabase.taskDao().deleteTask(TASK_DEMO_01);
        List<Task> taskdeleted= LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTaskByProject(PROJECT_ID));
        //Vérifier que la tâche a bien été supprimée
        assertTrue(taskdeleted.size()==0);
    }


}
