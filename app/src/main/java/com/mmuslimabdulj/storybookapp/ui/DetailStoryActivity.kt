package com.mmuslimabdulj.storybookapp.ui

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mmuslimabdulj.storybookapp.R
import com.mmuslimabdulj.storybookapp.databinding.ActivityDetailStoryBinding
import com.mmuslimabdulj.storybookapp.util.Constants
import com.squareup.picasso.Picasso
import java.util.Locale

class DetailStoryActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityDetailStoryBinding
    private var isPlaying = false
    private var textToSpeech: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textToSpeech = TextToSpeech(this, this)
        binding.fbPlay.isEnabled = false

        Picasso.get().load(Constants.image).into(binding.ivCover)
        binding.tvTitle.text = Constants.title
        binding.tvStoryText.text = Constants.storyText

        binding.fbPlay.setOnClickListener { handlePlayButtonClick() }
    }

    private fun handlePlayButtonClick() {
        if (Constants.audio.equals("GT", ignoreCase = true)) {
            handleTextToSpeech()
        } else {
            handleMediaPlayer()
        }
    }

    private fun handleTextToSpeech() {
        val storyText = Constants.storyText
        if (storyText.isNullOrEmpty()) {
            Toast.makeText(this, "No story text to read", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPlaying) {
            isPlaying = true
            textToSpeech?.speak(storyText, TextToSpeech.QUEUE_FLUSH, null, "storyId")
            binding.fbPlay.setImageResource(R.drawable.ic_pause)
        } else {
            isPlaying = false
            textToSpeech?.stop()
            binding.fbPlay.setImageResource(R.drawable.ic_play)
        }
    }

    private fun handleMediaPlayer() {
        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    if (Constants.audio!!.startsWith("http") || Constants!!.audio!!.startsWith("https")) {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource(Constants.audio)
                    } else {
                        val assetPath = Constants!!.audio.toString()
                        val afd = assets.openFd(assetPath)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    }
                    setOnPreparedListener {
                        it.start()
                        this@DetailStoryActivity.isPlaying = true
                        binding.fbPlay.setImageResource(R.drawable.ic_pause)
                    }
                    setOnCompletionListener {
                        this@DetailStoryActivity.isPlaying = false
                        binding.fbPlay.setImageResource(R.drawable.ic_play)
                        release()
                        mediaPlayer = null
                    }
                    setOnErrorListener { _, what, extra ->
                        Toast.makeText(
                            this@DetailStoryActivity,
                            "MediaPlayer Error: $what, $extra",
                            Toast.LENGTH_SHORT
                        ).show()
                        this@DetailStoryActivity.isPlaying = false
                        binding.fbPlay.setImageResource(R.drawable.ic_play)
                        release()
                        mediaPlayer = null
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
                isPlaying = false
                binding.fbPlay.setImageResource(R.drawable.ic_play)
            }
        } else {
            if (isPlaying) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                isPlaying = false
                binding.fbPlay.setImageResource(R.drawable.ic_play)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                binding.fbPlay.isEnabled = false
            } else {
                binding.fbPlay.isEnabled = true
                textToSpeech?.setOnUtteranceProgressListener(object :
                    android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        runOnUiThread {
                            isPlaying = false
                            binding.fbPlay.setImageResource(R.drawable.ic_play)
                        }
                    }
                    override fun onError(utteranceId: String?) {
                        runOnUiThread {
                            isPlaying = false
                            binding.fbPlay.setImageResource(R.drawable.ic_play)
                            Toast.makeText(
                                this@DetailStoryActivity,
                                "Error while speaking",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }
        } else {
            Toast.makeText(this, "Initialization failed", Toast.LENGTH_SHORT).show()
            binding.fbPlay.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
