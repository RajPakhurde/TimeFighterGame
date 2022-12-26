package com.rajpakhurde.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvHeart: TextView
    private lateinit var btnTapMe: Button

    private var gameStart = false
    private var initialCountDown: Long = 60000
    private var countDownInterval: Long = 1000
    private lateinit var countDownTimer: CountDownTimer
    private var timeLeft = 60

    private var score = 0

    companion object {
        private val SCORE_KEY = "SCORE_KEY"
        private val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvScore = findViewById(R.id.tvScore)
        tvTimer = findViewById(R.id.tvTimer)
        tvHeart = findViewById(R.id.tvheart)
        btnTapMe = findViewById(R.id.btnTapMe)

        btnTapMe.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this,R.anim.bounce)
            tvHeart.startAnimation(bounceAnimation)
            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                countDownTimer.cancel()
                btnTapMe.isEnabled = true
                resetGame()
            }
            R.id.mi_info -> {
                val title = getString(R.string.dialog_title,BuildConfig.VERSION_NAME)
                val message = getString(R.string.dialog_messege)
                AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .create()
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun incrementScore() {
        if (!gameStart) {
            startGame()
        }
        score++

        val newScore = getString(R.string.your_score, score)
        tvScore.text = newScore

    }

    private fun resetGame() {
        score = 0

        val initialScore = getString(R.string.your_score, score)
        tvScore.text = initialScore

        val initialTimeLeft = getString(R.string.time_left, 60)
        tvTimer.text = initialTimeLeft

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(p0: Long) {
                timeLeft = p0.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)
                tvTimer.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }

        }
        gameStart = false
    }

    private fun startGame() {
        countDownTimer.start()
        gameStart = true
    }

    private fun endGame() {
        btnTapMe.isEnabled = false
        Toast.makeText(this, getString(R.string.game_over_message, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

    private fun restoreGame() {
        val restoreScore = getString(R.string.your_score, score)
        tvScore.text = restoreScore

        val restoreTimeLeft = getString(R.string.time_left, timeLeft)
        tvTimer.text = restoreTimeLeft

        countDownTimer = object : CountDownTimer((timeLeft * 1000).toLong(), countDownInterval) {
            override fun onTick(p0: Long) {
                timeLeft = p0.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)
                tvTimer.text = timeLeftString
            }
            override fun onFinish() {
                btnTapMe.isEnabled = true
                endGame()
            }
        }
        countDownTimer.start()
        gameStart = true
    }
}