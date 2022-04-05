package com.sibedge.corutinesdemo.list

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.sibedge.corutinesdemo.FunctionType
import com.sibedge.corutinesdemo.MainViewModel
import com.sibedge.corutinesdemo.databinding.ActivityListBinding
import com.sibedge.corutinesdemo.databinding.ActivityMainBinding
import com.sibedge.corutinesdemo.log

class UsersLoadActivity : AppCompatActivity() {

    private val usersViewModel: UsersViewModel by viewModels()
    private lateinit var binding: ActivityListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        usersViewModel.getData().observe(this) {
            log("users = $it")
        }

//        binding.usersList.adapter
    }
}