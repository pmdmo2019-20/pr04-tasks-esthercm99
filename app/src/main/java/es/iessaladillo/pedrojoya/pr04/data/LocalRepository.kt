package es.iessaladillo.pedrojoya.pr04.data

import android.widget.CheckBox
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.text.SimpleDateFormat
import java.util.*

// TODO: Crea una clase llamada LocalRepository que implemente la interfaz Repository
//  usando una lista mutable para almacenar las tareas.
//  Los id de las tareas se irán generando secuencialmente a partir del valor 1 conforme
//  se van agregando tareas (add).

object LocalRepository : Repository {
    private var countId: Long = 0
    private val tasks: MutableList<Task> = mutableListOf()

    override fun queryAllTasks(): List<Task> = tasks

    override fun queryCompletedTasks(): List<Task> {
        val tasksCompleted: MutableList<Task> = mutableListOf()
        for(i in 0 until tasks.size) {
            if (tasks[i].completed) {
                tasksCompleted.add(tasks[i])
                println(tasks[i].completed)
            }
        }

        return tasksCompleted
    }

    override fun queryPendingTasks(): List<Task> {
        val tasksPending: MutableList<Task> = mutableListOf()

        for(i in 0 until tasks.size) {
            if (!tasks[i].completed) {
                tasksPending.add(tasks[i])
            }
        }

        return tasksPending
    }

    override fun addTask(concept: String) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        insertTask(Task(countId, concept, dateFormat.format(date)))
        countId++
    }

    override fun insertTask(task: Task) {
        tasks.add(task)
    }

    override fun deleteTask(taskId: Long) {
        for(i in 0..tasks.size) {
            if (tasks[i].id == taskId) {
                tasks.removeAt(i)
                break
            }
        }
    }

    override fun deleteTasks() {
        while (tasks.isNotEmpty()) {
            tasks.removeAt(0)
        }
    }

    override fun markTaskAsCompleted(taskId: Long) {
        for(i in 0..tasks.size) {
            if (tasks[i].id == taskId) {
                tasks[i].completed = true
                break
            }
        }

    }

    override fun markTasksAsCompleted() {
        for(i in 0 until tasks.size) {
            tasks[i].completed = true
        }
    }

    override fun markTaskAsPending(taskId: Long) {
        for(i in 0 until tasks.size) {
            if (tasks[i].id == taskId) {
                tasks[i].completed = false
                tasks[i].completedAt = ""
                break
            }
        }
    }

    override fun markTasksAsPending() {
        for(i in 0 until tasks.size) {
            tasks[i].completed = false
            tasks[i].completedAt = ""
        }
    }
}

