package com.sibedge.corutinesdemo.list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.sibedge.corutinesdemo.User
import com.sibedge.corutinesdemo.databinding.ActivityListBinding
import com.sibedge.corutinesdemo.log
import com.sibedge.corutinesdemo.recyclerview.UserAdapter

class UsersLoadActivity : AppCompatActivity() {

    private val usersViewModel: UsersViewModel by viewModels()
    private lateinit var binding: ActivityListBinding
    private val userAdapter: UserAdapter = UserAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.usersList.adapter = userAdapter

        usersViewModel.getData().observe(this) {
            log("users = $it")
            userAdapter.update(it)
        }

        binding.reload.setOnClickListener {
            usersViewModel.reloadData()
        }

        binding.cancelGeneralJob.setOnClickListener {
            usersViewModel.cancelGeneralJob()
        }

        binding.cancelPostJob.setOnClickListener {
            usersViewModel.cancelPostJob()
        }

        binding.cancelCompanyJob.setOnClickListener {
            usersViewModel.cancelCompanyJob()
        }

        binding.exception.setOnClickListener {
            usersViewModel.throwException()
        }
    }
}