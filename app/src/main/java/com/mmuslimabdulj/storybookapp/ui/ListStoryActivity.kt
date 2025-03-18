package com.mmuslimabdulj.storybookapp.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.mmuslimabdulj.storybookapp.R
import com.mmuslimabdulj.storybookapp.adapter.ListStoryAdapter
import com.mmuslimabdulj.storybookapp.databinding.ActivityListStoryBinding
import com.mmuslimabdulj.storybookapp.model.Story
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding

    private var storyList: ArrayList<Story> = ArrayList()
    private var storyListAdapter: ListStoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "List of Children Stories"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.rvStoryList.setHasFixedSize(true)
        binding.rvStoryList.layoutManager = GridLayoutManager(this, 2)
        loadData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadJSONFromAsset(): String? {
        return try {
            val inputStream = assets.open("data.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    private fun loadData() {
        try {
            val jsonObject = JSONObject(loadJSONFromAsset()!!)
            val jsonArray = jsonObject.getJSONArray("Data")
            for (i in 0 until jsonArray.length()) {
                val jsonArrayOfObject = jsonArray.getJSONObject(i)
                val story = Story(
                    title = jsonArrayOfObject.getString("title"),
                    storyText = jsonArrayOfObject.getString("story_text"),
                    audio = jsonArrayOfObject.getString("audio"),
                    image = jsonArrayOfObject.getString("image")
                )
                storyList.add(story)
            }
            storyListAdapter = ListStoryAdapter(storyList, this)
            binding.rvStoryList.adapter = storyListAdapter
        } catch (e: JSONException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}