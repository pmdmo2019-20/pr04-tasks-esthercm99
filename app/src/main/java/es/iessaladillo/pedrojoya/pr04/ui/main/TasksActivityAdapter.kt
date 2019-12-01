package es.iessaladillo.pedrojoya.pr04.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.strikeThrough
import java.text.SimpleDateFormat
import java.util.*

//  Crea una clase TasksActivityAdapter que actúe como adaptador del RecyclerView
//  y que trabaje con una lista de tareas.
//  Cuando se haga click sobre un elemento se debe cambiar el estado de completitud
//  de la tarea, pasando de completada a pendiente o viceversa.
//  La barra de cada elemento tiene un color distinto dependiendo de si la tarea está
//  completada o no.
//  Debajo del concepto se muestra cuando fue creada la tarea, si la tarea está pendiente,
//  o cuando fue completada si la tarea ya ha sido completada.
//  Si la tarea está completada, el checkBox estará chequeado y el concepto estará tachado.

class TasksActivityAdapter() : RecyclerView.Adapter<TasksActivityAdapter.ViewHolder>() {

    private var data: List<Task> = emptyList()
    var onCheckListener: ((Int) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    fun currentList(position: Int): Task {
        return data[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.tasks_activity_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = data[position]
        holder.bind(task)
    }

    fun submitList(newList: List<Task>) {
        data = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lblConcept: TextView = itemView.findViewById(R.id.lblConcept)
        private val lblCompleted: TextView = itemView.findViewById(R.id.lblCompleted)
        private val chkCompleted: CheckBox = itemView.findViewById(R.id.chkCompleted)
        private val viewBar: View = itemView.findViewById(R.id.viewBar)

        init {
            chkCompleted.setOnClickListener {
                onCheckListener?.invoke(adapterPosition)
            }
        }

        private fun completed(task: Task) {
            task.run {
                completed = true
                chkCompleted.isChecked = completed

                // Si la cadena viene vacía se le da la fecha en la que se ha chequeado:
                if (completedAt.trim().isEmpty()) {
                    completedAt = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date())
                }

                // Modificaciones del Item:
                lblConcept.strikeThrough(true)
                viewBar.setBackgroundResource(R.color.colorCompletedTask)
                lblCompleted.text = String.format("Completed at %s", completedAt)

            }
        }
        private fun pending(task: Task) {
            task.run {
                completed = false
                completedAt = ""
                chkCompleted.isChecked = completed
                lblConcept.strikeThrough(false)
                lblCompleted.text = String.format("Created at %s", createdAt)
                viewBar.setBackgroundResource(R.color.colorPendingTask)
            }
        }
        private fun initial(task: Task) {
            task.run {
                lblConcept.strikeThrough(false)
                lblCompleted.text = String.format("Created at %s", createdAt)
                viewBar.setBackgroundResource(R.color.colorPendingTask)
            }
        }

        fun bind(task: Task) {
            task.run {
                lblConcept.text = concept

                // Cuando se añade una nueva tarea:
                initial(task)

                // Cuando se hace click sobre el checkbox:
                chkCompleted.setOnClickListener {
                    if(!chkCompleted.isChecked) {
                        pending(task)
                    } else if(chkCompleted.isChecked) {
                        completed(task)
                    }
                }

                // Cuando no se hace click en los checkbox:
                if(!task.completed) {
                    pending(task)
                } else {
                    completed(task)
                }

            }
        }
    }
}

