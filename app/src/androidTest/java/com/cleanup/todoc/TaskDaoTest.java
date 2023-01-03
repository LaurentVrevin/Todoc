package com.cleanup.todoc;

import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;


import com.cleanup.todoc.database.dao.TodocDatabase;
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

    private static long PROJECT_ID = 1;
    private static Project PROJECT_DEMO = new Project(1, "Project test",0xFFEADAD1);
    private static Task TASK_DEMO = new Task(PROJECT_DEMO.getId(), PROJECT_ID,"Task test", new Date().getTime());

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before

    public void initDB() throws Exception {
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
        // Les valeurs que j'ajoute : 1, "Project test",0xFFEADAD1
        this.todocDatabase.projectDao().createProject(PROJECT_DEMO);
        // Récupère la liste des projets de la base de données en utilisant la méthode getProject de la classe ProjectDao
        List<Project> projects = LiveDataTestUtil.getValue(this.todocDatabase.projectDao().getProject(PROJECT_ID));

        Project project = null;
        // Si la liste de projets n'est pas vide, récupère le premier élément de la liste et le stocke dans la variable project
        if (!projects.isEmpty()) {
            project = projects.get(0);
        }
        //Vérifie maintenant si project n'est pas null et que le nom et son Id correspondent, alors renvoie true.
        assertTrue(project != null && project.getName().equals(PROJECT_DEMO.getName()) && project.getId() == PROJECT_ID);
    }
    @Test
    public void insertAndGetTasks() throws Exception {
        //insère un projet dans la bdd
        this.todocDatabase.projectDao().createProject(PROJECT_DEMO);
        //insère une tâche dans la bdd avec insertTask pour la classe TaskDao
        this.todocDatabase.taskDao().insertTask(TASK_DEMO);
        // Récupère la liste des tâches de la base de données en utilisant getTask de la classe TaskDao
        List<Task> tasks = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTasks(PROJECT_ID));
        //Confirme qu'il y a bien une tâche dans la liste
        assertTrue(tasks.size() == 1);
    }

    @Test
    public void insertAndDeleteTask() throws InterruptedException {
        //Insérer un projet
        this.todocDatabase.projectDao().createProject(PROJECT_DEMO);
        //Insérer une tâche
        this.todocDatabase.taskDao().insertTask(TASK_DEMO);
        List<Task> taskadded = LiveDataTestUtil.getValue(this.todocDatabase.taskDao().getTasks(PROJECT_ID));
        //supprimer la tâche
        this.todocDatabase.taskDao().deleteTask(TASK_DEMO.getId());

        //Vérifier que la tâche a bien été supprimée

    }

}
