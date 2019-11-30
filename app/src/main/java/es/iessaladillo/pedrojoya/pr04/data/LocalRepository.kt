package es.iessaladillo.pedrojoya.pr04.data

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
        for(i in 0..tasks.size) {
            if (tasks[i].completed) {
                tasksCompleted.add(tasks[i])
            }
        }

        return tasksCompleted
    }

    override fun queryPendingTasks(): List<Task> {
        val tasksPending: MutableList<Task> = mutableListOf()
        for(i in 0..tasks.size) {
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

    override fun deleteTasks(taskIdList: List<Long>) {
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

    override fun markTasksAsCompleted(taskIdList: List<Long>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun markTaskAsPending(taskId: Long) {
        for(i in 0..tasks.size) {
            if (tasks[i].id == taskId) {
                tasks[i].completed = false
                break
            }
        }
    }

    override fun markTasksAsPending(taskIdList: List<Long>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

