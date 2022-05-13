package com.sibedge.corutinesdemo.list

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        usersViewModel.getState().observe(this) {
            log("state = $it")
            binding.globalJob.setTextColor(colorOnStatus(it.loadJobState))
            binding.postJob.setTextColor(colorOnStatus(it.posJobState))
            binding.companyJob.setTextColor(colorOnStatus(it.companyJobState))
            binding.addJob1.setTextColor(colorOnStatus(it.addJob1))
            binding.addJob2.setTextColor(colorOnStatus(it.addJob2))
        }

        usersViewModel.getExcInfo().observe(this) {
            binding.excText.text = it
        }

        binding.reload.setOnClickListener {
            usersViewModel.reloadData()
        }

        binding.eraseExcept.setOnClickListener {
            usersViewModel.erasExc()
            binding.excText.text = ""
            binding.globalJob.setTextColor(Color.GRAY)
            binding.postJob.setTextColor(Color.GRAY)
            binding.companyJob.setTextColor(Color.GRAY)
            binding.addJob1.setTextColor(Color.GRAY)
            binding.addJob2.setTextColor(Color.GRAY)
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

        binding.exceptionGeneral.setOnClickListener {
            usersViewModel.throwExceptionGeneral()
        }

        binding.exceptionPost.setOnClickListener {
            usersViewModel.throwExceptionPost()
        }

        binding.exceptionCompanyJob.setOnClickListener {
            usersViewModel.throwExceptionCompanyJob()
        }

        binding.exceptionAddJob.setOnClickListener {
            usersViewModel.throwExceptionAddJob()
        }

        binding.restartPostJob.setOnClickListener {
            usersViewModel.restartPostJob()
        }

    }

    private fun colorOnStatus(jobStatus: Status): Int =
        when (jobStatus) {
            Status.Undefined -> Color.GREEN
            Status.Active -> Color.BLUE
            Status.Cancelled -> Color.RED
            Status.Completed -> Color.BLACK
        }
}