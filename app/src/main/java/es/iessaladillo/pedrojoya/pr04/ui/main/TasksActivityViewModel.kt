package es.iessaladillo.pedrojoya.pr04.ui.main

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.Event
import es.iessaladillo.pedrojoya.pr04.data.Repository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.text.SimpleDateFormat
import java.util.*
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.content.pm.PackageManager








class TasksActivityViewModel(private val repository: Repository,
                             private val application: Application) : ViewModel() {

    var listTask: MutableList<Task> = repository.queryAllTasks().toMutableList()

    // Estado de la interfaz
    private val _tasks: MutableLiveData<List<Task>> = MutableLiveData()
    val tasks: LiveData<List<Task>>
        get() = _tasks

    private val _currentFilter: MutableLiveData<TasksActivityFilter> =
        MutableLiveData(TasksActivityFilter.ALL)

    private val _currentFilterMenuItemId: MutableLiveData<Int> =
        MutableLiveData(R.id.mnuFilterAll)
    val currentFilterMenuItemId: LiveData<Int>
        get() = _currentFilterMenuItemId

    private val _activityTitle: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_title_all))
    val activityTitle: LiveData<String>
        get() = _activityTitle

    private val _lblEmptyViewText: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_no_tasks_yet))
    val lblEmptyViewText: LiveData<String>
        get() = _lblEmptyViewText

    // Eventos de comunicación con la actividad
    private val _onStartActivity: MutableLiveData<Event<Intent>> = MutableLiveData()
    val onStartActivity: LiveData<Event<Intent>>
        get() = _onStartActivity

    private val _onShowMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val onShowMessage: LiveData<Event<String>>
        get() = _onShowMessage

    private val _onShowTaskDeleted: MutableLiveData<Event<Task>> = MutableLiveData()
    val onShowTaskDeleted: LiveData<Event<Task>>
        get() = _onShowTaskDeleted

    init {
        _tasks.value = listTask
    }

    // ACTION METHODS

    // Hace que se muestre en el RecyclerView todas las tareas.
    fun filterAll() {
        _currentFilterMenuItemId.value = R.id.mnuFilterAll
        _activityTitle.value = application.getString(R.string.tasks_title_all)
        queryTasks(TasksActivityFilter.ALL)
    }
    // Hace que se muestre en el RecyclerView sólo las tareas completadas.
    fun filterCompleted() {
        _currentFilterMenuItemId.value = R.id.mnuFilterCompleted
        _activityTitle.value = application.getString(R.string.tasks_title_completed)
        queryTasks(TasksActivityFilter.COMPLETED)
    }
    // Hace que se muestre en el RecyclerView sólo las tareas pendientes.
    fun filterPending() {
        _currentFilterMenuItemId.value = R.id.mnuFilterPending
        _activityTitle.value = application.getString(R.string.tasks_title_pending)
        queryTasks(TasksActivityFilter.PENDING)
    }

    // Agrega una nueva tarea con dicho concepto. Si la se estaba mostrando
    // la lista de solo las tareas completadas, una vez agregada se debe
    // mostrar en el RecyclerView la lista con todas las tareas, no sólo
    // las completadas.
    fun addTask(concept: String) {
        if(isValidConcept(concept)) {
            repository.addTask(concept)
            queryTasks( _currentFilter.value)
        }
    }

    // Agrega la tarea
    fun insertTask(task: Task) {
        repository.insertTask(task)
        queryTasks( _currentFilter.value)
    }

    // Borra la tarea
    fun deleteTask(task: Task) {
        repository.deleteTask(task.id)
        queryTasks( _currentFilter.value)
    }

    // Borra todas las tareas mostradas actualmente en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que borrar.
    fun deleteTasks(lstTasks: RecyclerView) {
        if (repository.queryAllTasks().isEmpty()) {
            Snackbar.make(lstTasks, R.string.tasks_no_tasks_to_delete, Snackbar.LENGTH_LONG).show()
        } else {
            repository.deleteTasks()
        }
    }

    // Marca como completadas todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban completadas.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como completadas.
    fun markTasksAsCompleted(lstTasks: RecyclerView) {
        if (repository.queryAllTasks().isEmpty()) {
            Snackbar.make(lstTasks, R.string.tasks_no_tasks_to_mark_as_completed, Snackbar.LENGTH_LONG).show()
        } else {
            repository.markTasksAsCompleted()
        }
    }

    // Marca como pendientes todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban pendientes.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como pendientes.
    fun markTasksAsPending(lstTasks: RecyclerView) {
        if (repository.queryAllTasks().isEmpty()) {
            Snackbar.make(lstTasks, R.string.tasks_no_tasks_to_mark_as_pending, Snackbar.LENGTH_LONG).show()
        } else {
            repository.markTasksAsPending()
        }
    }

    // Hace que se envíe un Intent con la lista de tareas mostradas actualmente
    // en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un Snackbar indicando
    // que no hay tareas que compartir.
    fun shareTasks() {
        /*
        val myList = repository.queryAllTasks()
        Intent().putExtra("mylist", myList)

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my tasks to send.")
            type = "text/plain"
        }

        // Fallo de flags:
        startActivity(application.applicationContext, Intent.createChooser(sendIntent, null), null)*/
    }


    // Retorna si el concepto recibido es válido (no es una cadena vacía o en blanco)
    private fun isValidConcept(concept: String): Boolean {
        return concept.trim().isNotEmpty()
    }

    // Pide las tareas al repositorio, atendiendo al filtro recibido
    private fun queryTasks(filter: TasksActivityFilter?) {
        if(filter == TasksActivityFilter.ALL) {
            _tasks.value = repository.queryAllTasks()
        } else if (filter == TasksActivityFilter.COMPLETED) {
            _tasks.value = repository.queryCompletedTasks()
        } else if (filter == TasksActivityFilter.PENDING) {
            _tasks.value = repository.queryPendingTasks()
        }
    }
/*
    // ---------- FUNCIONES QUE NO USO ----------

    // Actualiza el estado de completitud de la tarea recibida, atendiendo al
    // valor de isCompleted. Si es true la tarea es marcada como completada y
    // en caso contrario es marcada como pendiente.
    fun updateTaskCompletedState(task: Task, isCompleted: Boolean) {
        // TODO
    }
    fun submitTasks(newList: List<Task>) {
        _tasks.value = newList.sortedByDescending { it.id }
        listTask.clear()
        tasks.value?.forEach {
            listTask.add(it)
        }
    }
    // Marca como completada una tarea.
    fun markTaskAsCompleted(task: Task) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()

        for(i in 0..repository.queryAllTasks().size) {
            if (repository.queryAllTasks()[i].id == task.id) {
                repository.queryAllTasks()[i].completed = true
                repository.queryAllTasks()[i].completedAt = dateFormat.format(date)
            }
        }

        _tasks.value = repository.queryAllTasks()
    }
    // Marca como pendiente una tarea.
    fun markTaskAsPending(task: Task) {
        for(i in 0..repository.queryAllTasks().size) {
            if (repository.queryAllTasks()[i].id == task.id) {
                repository.queryAllTasks()[i].completed = false
                repository.queryAllTasks()[i].completedAt = ""
            }
        }

        _tasks.value = repository.queryAllTasks()
    }
*/
}

