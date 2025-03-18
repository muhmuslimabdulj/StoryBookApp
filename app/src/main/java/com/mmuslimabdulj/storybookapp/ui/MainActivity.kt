package com.mmuslimabdulj.storybookapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mmuslimabdulj.storybookapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardStartTelling.setOnClickListener {
            val intent = Intent(this, ListStoryActivity::class.java)
            startActivity(intent)
        }
        binding.cardShareApp.setOnClickListener {
            Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show()
        }
        binding.cardLeaveReview.setOnClickListener {
            Toast.makeText(this, "Rate App", Toast.LENGTH_SHORT).show()
        }
    }
}