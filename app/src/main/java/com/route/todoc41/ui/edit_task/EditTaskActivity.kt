package com.route.todoc41.ui.edit_task

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.intIntMapOf
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.route.todoc41.R
import com.route.todoc41.database.MyDatabase
import com.route.todoc41.database.dao.TasksDao
import com.route.todoc41.database.entity.Task
import com.route.todoc41.databinding.ActivityEditTaskBinding
import com.route.todoc41.databinding.ActivityHomeBinding
import com.route.todoc41.ui.util.Constants
import com.route.todoc41.ui.util.clearDate
import com.route.todoc41.ui.util.clearSeconds
import com.route.todoc41.ui.util.clearTime
import com.route.todoc41.ui.util.getFormattedTime
import com.route.todoc41.ui.util.showDatePickerDialog
import com.route.todoc41.ui.util.showTimePickerDialog
import java.util.Calendar

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var intentTask: Task
    private lateinit var newTask: Task
    private lateinit var dao: TasksDao
    private var dateCalendar = Calendar.getInstance()
    private var timeCalendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intentTask =
            IntentCompat.getParcelableExtra(intent, Constants.TASK_KEY, Task::class.java) as Task
        newTask = intentTask.copy()
        dao = MyDatabase.getInstance().tasksDao()
        setUpToolBar()
        initViews()
        onSelectDateClick()
        onSelectTimeClick()
        onSaveClick()

    }

    private fun onSaveClick() {
        binding.content.btnSave.setOnClickListener {
            if (!validateInput()) {
                return@setOnClickListener
            }
            updateAndFinish()
        }
    }

    private fun updateAndFinish() {
        newTask.apply {
            description = binding.content.description.text.toString()
            title = binding.content.title.text.toString()
        }
        dao.updateTask(newTask)
        finish()
    }

    fun validateInput(): Boolean {
        var isValid = true
        if (binding.content.title.text.isNullOrBlank()) {
            isValid = false
            binding.content.titleTil.error = getString(R.string.required_field)
        } else {
            binding.content.titleTil.error = null

        }
        return isValid
    }

    private fun onSelectTimeClick() {
        binding.content.selectTimeTv.setOnClickListener {
            val calendar = Calendar.getInstance()
            showTimePickerDialog(
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                "Select Time:",
                supportFragmentManager
            ) { hour, minute ->
                binding.content.selectTimeTv.text = getFormattedTime(hour, minute)
                timeCalendar.set(Calendar.HOUR, hour)
                timeCalendar.set(Calendar.MINUTE, minute)
                timeCalendar.clearDate()
                timeCalendar.clearSeconds()
                newTask.time = timeCalendar.timeInMillis

            }
        }
    }

    private fun onSelectDateClick() {
        binding.content.selectDateTv.setOnClickListener {
            showDatePickerDialog(this) { date, calendar ->
                binding.content.selectDateTv.text = date
                dateCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                dateCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                dateCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                dateCalendar.clearTime()
                newTask.date = dateCalendar.timeInMillis

            }
        }
    }

    private fun initViews() {
        binding.content.title.setText(intentTask.title)
        binding.content.description.setText(intentTask.description)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = intentTask.time
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        binding.content.selectDateTv.text = "$day/${month + 1}/$$year"

        calendar.timeInMillis = intentTask.date
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        binding.content.selectTimeTv.text = getFormattedTime(hour, minute)


    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}