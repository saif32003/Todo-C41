package com.route.todoc41.ui.home.fragments.tasks_fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.route.todoc41.database.MyDatabase
import com.route.todoc41.database.dao.TasksDao
import com.route.todoc41.databinding.FragmentTasksBinding
import com.route.todoc41.ui.edit_task.EditTaskActivity
import com.route.todoc41.ui.util.Constants
import com.route.todoc41.ui.util.clearTime
import java.util.Calendar

class TasksFragment:Fragment() {
    private var valueBinding: FragmentTasksBinding ?= null
    private val binding get() = valueBinding!!
    private val adapter = TasksAdapter()
    private lateinit var dao: TasksDao
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        valueBinding = FragmentTasksBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = MyDatabase.getInstance().tasksDao()
        initRecyclerView()
        initCalendarView()
    }

    private fun initCalendarView() {
        binding.calendarView.selectedDate = CalendarDay.today()
        binding.calendarView.setOnDateChangedListener { _, date, selected ->
            val calendar = Calendar.getInstance()//current time
            calendar.set(Calendar.YEAR,date.year)
            calendar.set(Calendar.MONTH,date.month-1)
            calendar.set(Calendar.DAY_OF_MONTH,date.day)
            calendar.clearTime()
            if (selected){
                val tasks = dao.getAllTasksByDate(calendar.timeInMillis).toMutableList()
                Log.e("TAG", "initCalendarView: $tasks", )
                adapter.setTasksList(tasks)
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvTasks.adapter = adapter
        adapter.onDeleteBtnClickListener = TasksAdapter.OnTaskClickListener { position, task ->
            dao.deleteTask(task)
            adapter.deleteTask(position, task)
        }
        adapter.onDoneBtnClickListener = TasksAdapter.OnTaskClickListener { position, task ->
            task.isDone =!task.isDone
            dao.updateTask(task)
            adapter.updateTask(position, task)
        }
        adapter.onItemClickListener = TasksAdapter.OnTaskClickListener {position, task ->
            val intent = Intent(requireContext(), EditTaskActivity::class.java)
            intent.putExtra(Constants.TASK_KEY,task)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        loadAllTasksOfDate(getSelectedDate().timeInMillis)

    }

    fun loadAllTasksOfDate(date: Long) {
       val tasks = dao.getAllTasksByDate(date).toMutableList()
        adapter.setTasksList(tasks)
    }

    private fun getSelectedDate():Calendar{
        val calendar = Calendar.getInstance()
       if (binding.calendarView.selectedDate != null){
           calendar.set(Calendar.YEAR, binding.calendarView.selectedDate!!.year)
        }
        binding.calendarView.selectedDate?.let { date->
            calendar.set(Calendar.YEAR, date.year)
            calendar.set(Calendar.MONTH, date.month-1)
            calendar.set(Calendar.DAY_OF_MONTH, date.day)
        }
        calendar.clearTime()
        return calendar
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("TAG", "onDestroyView:",)
        valueBinding = null
        adapter.onDoneBtnClickListener = null
        adapter.onDeleteBtnClickListener = null
        adapter.onItemClickListener = null
    }
}