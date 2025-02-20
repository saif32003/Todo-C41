package com.route.todoc41.ui.home.fragments.tasks_fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.route.todoc41.R
import com.route.todoc41.database.entity.Task
import com.route.todoc41.databinding.ItemTaskBinding
import com.route.todoc41.ui.util.getFormattedTime
import com.zerobranch.layout.SwipeLayout
import com.zerobranch.layout.SwipeLayout.SwipeActionsListener
import java.util.Calendar

class TasksAdapter:RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {
    private var tasksList = mutableListOf<Task>()

    @SuppressLint("NotifyDataSetChanged")
    fun setTasksList(tasks:MutableList<Task>){
        tasksList = tasks
        notifyDataSetChanged()
    }

fun deleteTask(position: Int, task: Task) {
    if (position in tasksList.indices) {
        tasksList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasksList.size - position)
    }
}
    fun updateTask(position: Int, task: Task) {
        if (position in tasksList.indices) {
            tasksList[position] = task
            notifyItemChanged(position)
        }
}
    class TaskViewHolder(val binding: ItemTaskBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.title.text = task.title
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.time
            val hr = calendar.get(Calendar.HOUR)
            val minutes = calendar.get(Calendar.MINUTE)
            binding.time.text = getFormattedTime(hr, minutes)
        }

        fun changeToIsDone(isDone: Boolean) {
            if (isDone) {
                binding.draggingBar.setImageResource(R.drawable.dragging_bar_done)
                binding.title.setTextColor(Color.GREEN)
                binding.btnTaskIsDone.setBackgroundResource(R.drawable.done)
            } else {
                binding.draggingBar.setImageResource(R.drawable.button_bg)
                val blue = ContextCompat.getColor(itemView.context, R.color.blue)
                binding.title.setTextColor(blue)
                binding.btnTaskIsDone.setBackgroundResource(R.drawable.check_mark)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    override fun getItemCount(): Int = tasksList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasksList[position]
        holder.bind(task)
        holder.changeToIsDone(task.isDone)
        holder.binding.leftView.isClickable = true
        onDeleteBtnClickListener ?.let {
            holder.binding.swipeLayout.setOnActionsListener(object :SwipeActionsListener{
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    if (direction == SwipeLayout.RIGHT) {
                        holder.binding.leftView.setOnClickListener{
                            onDeleteBtnClickListener?.onClick(holder.adapterPosition, task)
                        }
                    }
                }

                override fun onClose() {
                    holder.binding.leftView.isClickable = false
                }

            })
        }
        onDoneBtnClickListener ?.let {
            holder.binding.btnTaskIsDone.setOnClickListener {
                onDoneBtnClickListener?.onClick(position, task)
            }
        }
        onItemClickListener?.let {
            holder.binding.dragItem.setOnClickListener{
                onItemClickListener?.onClick(position, task)
            }
        }
    }
    var onItemClickListener:OnTaskClickListener ?= null
    var onDeleteBtnClickListener:OnTaskClickListener ?= null
    var onDoneBtnClickListener:OnTaskClickListener ?= null
    fun interface OnTaskClickListener{
        fun onClick(position: Int, task: Task)
    }
}