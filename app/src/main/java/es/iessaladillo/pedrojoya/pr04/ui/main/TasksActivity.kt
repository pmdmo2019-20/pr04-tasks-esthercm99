package es.iessaladillo.pedrojoya.pr04.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.invisibleUnless
import kotlinx.android.synthetic.main.tasks_activity.*
import android.view.MotionEvent
import android.graphics.PorterDuff
//import android.R
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.utils.hideKeyboard
import es.iessaladillo.pedrojoya.pr04.utils.setOnSwipeListener


class TasksActivity : AppCompatActivity() {

    private var mnuFilter: MenuItem? = null
    private val viewModel: TasksActivityViewModel by viewModels {
        TasksActivityViewModelFactory(LocalRepository, application)
    }
    private val listAdapter : TasksActivityAdapter = TasksActivityAdapter().also {
        it.onCheckListener = { observeTasks() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        mnuFilter = menu.findItem(R.id.mnuFilter)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuShare -> viewModel.shareTasks()
            R.id.mnuDelete -> viewModel.deleteTasks(lstTasks)
            R.id.mnuComplete -> viewModel.markTasksAsCompleted(lstTasks)
            R.id.mnuPending -> viewModel.markTasksAsPending(lstTasks)
            R.id.mnuFilterAll -> viewModel.filterAll()
            R.id.mnuFilterPending -> viewModel.filterPending()
            R.id.mnuFilterCompleted -> viewModel.filterCompleted()
            else -> return super.onOptionsItemSelected(item)
        }
        observeTasks()
        checkMenuItem(item.itemId)
        return true
    }

    private fun checkMenuItem(@MenuRes menuItemId: Int) {
        lstTasks.post {
            val item = mnuFilter?.subMenu?.findItem(menuItemId)

            item?.let { menuItem ->
                menuItem.isChecked = true
            }
        }
    }

    private fun showTasks(tasks: List<Task>) {
        lstTasks.post {
            listAdapter.submitList(tasks)
            lblEmptyView.invisibleUnless(tasks.isEmpty())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setupViews()
    }

    private fun updateList(newList: List<Task>) {
        lstTasks.post {
            listAdapter.submitList(newList)
            lblEmptyView.visibility = if (newList.isEmpty()) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun refreshList(newList: List<Task>) {
        lstTasks.post {
            listAdapter.submitList(emptyList())
            listAdapter.submitList(newList)

            lblEmptyView.visibility = View.INVISIBLE
            lblEmptyView.visibility = View.VISIBLE

        }
    }
    private fun setupViews() {
        setupRecyclerView()
        observeTasks()
        addTask()
    }
    private fun setupRecyclerView() {
        lstTasks.run {
            setHasFixedSize(true)
            adapter = listAdapter
            layoutManager = LinearLayoutManager(this@TasksActivity)
            addItemDecoration(DividerItemDecoration(this@TasksActivity, RecyclerView.VERTICAL))
            itemAnimator = DefaultItemAnimator()

            setOnSwipeListener{ viewHolder, _ ->
                val task: Task = listAdapter.currentList(viewHolder.adapterPosition)

                viewModel.deleteTask(task)

                val snackbar = Snackbar.make(
                    lstTasks,
                    getString(R.string.tasks_task_deleted, task.concept),
                    Snackbar.LENGTH_LONG)
                snackbar.setAction(R.string.tasks_recreate) {
                    viewModel.insertTask(task)
                }
                snackbar.show()

                observeTasks()
            }
        }

    }
    private fun observeTasks() {
        viewModel.tasks.observe(this) {
            updateList(it)
        }
    }

    private fun addTask() {
        imgAddTask.setOnClickListener {
            viewModel.addTask(txtConcept.text.toString())
            txtConcept.setText("")
        }

        imgAddTask.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val view = v as ImageView
                    // Overlay is black with transparency of 0x77 (119)
                    view.drawable.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                    view.invalidate()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val view = v as ImageView
                    // Clear the overlay
                    view.drawable.clearColorFilter()
                    view.invalidate()
                }
            }

            // Se cierra el teclado al añadir la tarea:
            v.hideKeyboard()

            false
        }
    }

}

