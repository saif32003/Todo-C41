package com.route.todoc41.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.route.todoc41.R
import com.route.todoc41.database.entity.Task
import com.route.todoc41.databinding.ActivityHomeBinding
import com.route.todoc41.ui.home.fragments.AddTaskFragment
import com.route.todoc41.ui.home.fragments.SettingsFragment
import com.route.todoc41.ui.home.fragments.tasks_fragment.TasksFragment

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private var tasksFragment:TasksFragment?=null
    private var currentFragmentTag:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tasksFragment = supportFragmentManager.findFragmentByTag("TASKS_FRAGMENT") as? TasksFragment ?: TasksFragment()
        setNavigation()
        setOnFabClick()
        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString("CURRENT_FRAGMENT")
            if (currentFragmentTag != null){
                val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
                if (fragment != null){
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,fragment,currentFragmentTag)
                        .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
                        .commit()
                }
            }
        } else {
            binding.bottomNavigationView.selectedItemId = R.id.tasks

        }

    }

    private fun setNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem->
            if (menuItem.itemId ==R.id.tasks){
                showFragment(tasksFragment!!,"TASKS_FRAGMENT")
                binding.title.text = getString(R.string.to_do_list)
            }else if (menuItem.itemId ==R.id.settings){
                showFragment(SettingsFragment(),"SETTINGS_FRAGMENT")
                binding.title.text = getString(R.string.settings)
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("CURRENT_FRAGMENT" , currentFragmentTag)
    }

    private fun showFragment(fragment: Fragment,tag:String) {
        currentFragmentTag = tag
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment,tag)
            .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
            .commit()
    }


    private fun setOnFabClick() {
        binding.fabAddTask.setOnClickListener {
            val bottomSheet = AddTaskFragment()
            bottomSheet.show(supportFragmentManager,"")
            bottomSheet.onTaskAdded = AddTaskFragment.OnTaskAdded { task: Task ->
                //reload data in recyclerview in TasksFragment
                tasksFragment?.loadAllTasksOfDate(task.date)
            }
        }

    }
}