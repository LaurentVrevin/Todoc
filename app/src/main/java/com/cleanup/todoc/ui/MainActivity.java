package com.cleanup.todoc.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.RoomDatabase;

import com.cleanup.todoc.R;
import com.cleanup.todoc.database.TodocDatabase;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.repository.ProjectRepository;
import com.cleanup.todoc.repository.TaskRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * <p>Home activity of the application which is displayed when the user opens the app.</p>
 * <p>Displays the list of tasks.</p>
 *
 * @author Gaëtan HERFRAY
 */
public class MainActivity extends AppCompatActivity implements TasksAdapter.DeleteTaskListener {


    /**
     * List of all current tasks of the application
     */
    @NonNull
    private final ArrayList<Task> tasks = new ArrayList<>();
    /**
     * The adapter which handles the list of tasks
     */
    private final TasksAdapter adapter = new TasksAdapter(tasks, this);
    /**
     * List of all projects available in the application
     */
    //private final Project[] allProjects = Project.getAllProjects();
    /**
     * Dialog to create a new task
     */
    @Nullable
    public AlertDialog dialog = null;
    //ViewModel
    private TaskViewModel taskViewModel;
    /**
     * The sort method to be used to display tasks
     */
    @NonNull
    private SortMethod sortMethod = SortMethod.NONE;
    /**
     * EditText that allows user to set the name of a task
     */
    @Nullable
    private EditText dialogEditText = null;

    /**
     * Spinner that allows the user to associate a project to a task
     */
    @Nullable
    private Spinner dialogSpinner = null;

    /**
     * The RecyclerView which displays the list of tasks
     */
    // Suppress warning is safe because variable is initialized in onCreate
    @SuppressWarnings("NullableProblems")
    @NonNull
    private RecyclerView listTasks;

    /**
     * The TextView displaying the empty state
     */
    // Suppress warning is safe because variable is initialized in onCreate
    @SuppressWarnings("NullableProblems")
    @NonNull
    private TextView lblNoTasks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listTasks = findViewById(R.id.list_tasks);
        lblNoTasks = findViewById(R.id.lbl_no_task);

        listTasks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listTasks.setAdapter(adapter);

        findViewById(R.id.fab_add_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog();
            }
        });
        configureViewModel();
        updateTasks();
    }

    private void configureViewModel() {

        // On crée une instance de la base de données en utilisant la méthode getInstance pour obtenir une référence unique
        TodocDatabase db = TodocDatabase.getInstance(this);

        // On crée une instance du ViewModel en lui passant les repositories pour les tâches et les projets, ainsi qu'un thread d'exécution
        taskViewModel = new TaskViewModel(new TaskRepository(db.taskDao()), new ProjectRepository(db.projectDao()), Executors.newSingleThreadExecutor());

        // On appelle la méthode init pour initialiser le ViewModel
        taskViewModel.init();

        // On observe les tâches pour mettre à jour l'adapteur de la liste de tâches
        taskViewModel.getTasks().observe(this, tasks -> adapter.updateTasks(tasks));
        taskViewModel.getProjects().observe(this, projects -> adapter.updateProjects(projects));



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_alphabetical) {
            sortMethod = SortMethod.ALPHABETICAL;
        } else if (id == R.id.filter_alphabetical_inverted) {
            sortMethod = SortMethod.ALPHABETICAL_INVERTED;
        } else if (id == R.id.filter_oldest_first) {
            sortMethod = SortMethod.OLD_FIRST;
        } else if (id == R.id.filter_recent_first) {
            sortMethod = SortMethod.RECENT_FIRST;
        }
        updateTasks();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteTask(Task task) {
        taskViewModel.deleteTask(task);
        updateTasks();
    }

    /**
     * Called when the user clicks on the positive button of the Create Task Dialog.
     *
     * @param dialogInterface the current displayed dialog
     */
    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        // If dialog is open
        if (dialogEditText != null && dialogSpinner != null) {
            // Get the name of the task
            String taskName = dialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }

            // If a name has not been set
            if (taskName.trim().isEmpty()) {
                dialogEditText.setError(getString(R.string.empty_task_name));
            }
            // If both project and name of the task have been set
            else if (taskProject != null) {

                long id = (long) (Math.random() * 50000);


                Task task = new Task(
                        id,
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()
                );

                addTask(task);

                dialogInterface.dismiss();
            }
            // If name has been set, but project has not been set (this should never occur)
            else {
                dialogInterface.dismiss();
            }
        }
        // If dialog is already closed
        else {
            dialogInterface.dismiss();
        }
    }

    /**
     * Shows the Dialog for adding a Task
     */
    private void showAddTaskDialog() {
        final AlertDialog dialog = getAddTaskDialog();

        dialog.show();

        dialogEditText = dialog.findViewById(R.id.txt_task_name);
        dialogSpinner = dialog.findViewById(R.id.project_spinner);

        populateDialogSpinner();
    }

    /**
     * Adds the given task to the list of created tasks.
     *
     * @param task the task to be added to the list
     */
    private void addTask(@NonNull Task task) {
        taskViewModel.createTask(task);
        updateTasks();
    }

    private void updateProjects(){
        taskViewModel.getProjects().observe(this, projects -> adapter.updateTasks(tasks));
    }

    /**
     * Updates the list of tasks in the UI
     */
    private void updateTasks() {
        taskViewModel.getTasks().observe(this, tasks -> {
            // update UI
            if (tasks.size() == 0) {
                lblNoTasks.setVisibility(View.VISIBLE);
                listTasks.setVisibility(View.GONE);
            } else {
                lblNoTasks.setVisibility(View.GONE);
                listTasks.setVisibility(View.VISIBLE);
                switch (sortMethod) {
                    case ALPHABETICAL:
                        Collections.sort(tasks, new Task.TaskAZComparator());
                        break;
                    case ALPHABETICAL_INVERTED:
                        Collections.sort(tasks, new Task.TaskZAComparator());
                        break;
                    case RECENT_FIRST:
                        Collections.sort(tasks, new Task.TaskRecentComparator());
                        break;
                    case OLD_FIRST:
                    case NONE:
                        Collections.sort(tasks, new Task.TaskOldComparator());
                        break;

                }
                adapter.updateTasks(tasks);
            }
        });
    }

    /**
     * Returns the dialog allowing the user to create a new task.
     *
     * @return the dialog allowing the user to create a new task
     */
    @NonNull
    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(R.layout.dialog_add_task);
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogEditText = null;
                dialogSpinner = null;
                dialog = null;
            }
        });

        dialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onPositiveButtonClick(dialog);
                    }
                });
            }
        });

        return dialog;
    }

    /**
     * Sets the data of the Spinner with projects to associate to a new task
     */
    private void populateDialogSpinner() {
        final LiveData<List<Project>> projects = taskViewModel.getProjects();
        projects.observe(this, new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projects) {
                final ArrayAdapter<Project> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, projects);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                if (dialogSpinner != null) {
                    dialogSpinner.setAdapter(adapter);
                }
            }
        });
    }

    /**
     * List of all possible sort methods for task
     */
    private enum SortMethod {
        /**
         * Sort alphabetical by name
         */
        ALPHABETICAL,
        /**
         * Inverted sort alphabetical by name
         */
        ALPHABETICAL_INVERTED,
        /**
         * Lastly created first
         */
        RECENT_FIRST,
        /**
         * First created first
         */
        OLD_FIRST,
        /**
         * No sort
         */
        NONE
    }
}
