package com.example.lab9_1

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // 兔子與烏龜的進度
    private var progressRabbit = 0
    private var progressTurtle = 0

    // 記錄比賽是否結束
    private var raceFinished = false

    // 控制整場比賽的協程
    private var raceJob: Job? = null

    // 畫面元件
    private lateinit var btnStart: Button
    private lateinit var sbRabbit: SeekBar
    private lateinit var sbTurtle: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // 綁定 XML 元件
        btnStart = findViewById(R.id.btnStart)
        sbRabbit = findViewById(R.id.sbRabbit)
        sbTurtle = findViewById(R.id.sbTurtle)

        // 設定終點為 100
        sbRabbit.max = 100
        sbTurtle.max = 100

        btnStart.setOnClickListener {
            startRace()
        }
    }

    // 初始化並開始比賽
    private fun startRace() {

        // 取消上一場比賽尚未完成的工作
        raceJob?.cancel()

        progressRabbit = 0
        progressTurtle = 0
        raceFinished = false

        sbRabbit.progress = 0
        sbTurtle.progress = 0
        btnStart.isEnabled = false

        // 協程隨 Activity 生命週期管理
        raceJob = lifecycleScope.launch {

            // 啟動兔子的子協程
            launch {
                runRabbit()
            }

            // 啟動烏龜的子協程
            launch {
                runTurtle()
            }
        }
    }

    // 使用協程模擬兔子移動
    private suspend fun runRabbit() {

        while (!raceFinished) {

            // 暫停協程 0.1 秒，不阻塞主執行緒
            delay(100)

            // 兔子有三分之二的機率偷懶
            if (Random.nextInt(3) < 2) {
                delay(300)
            }

            // 每次前進三步，最多到 100
            progressRabbit =
                (progressRabbit + 3).coerceAtMost(100)

            sbRabbit.progress = progressRabbit

            if (progressRabbit >= 100) {
                finishRace("兔子")
            }
        }
    }

    // 使用協程模擬烏龜移動
    private suspend fun runTurtle() {

        while (!raceFinished) {

            delay(100)

            // 每次前進一步
            progressTurtle =
                (progressTurtle + 1).coerceAtMost(100)

            sbTurtle.progress = progressTurtle

            if (progressTurtle >= 100) {
                finishRace("烏龜")
            }
        }
    }

    // 宣布勝利者並停止整場比賽
    private fun finishRace(winner: String) {

        // 避免重複宣布結果
        if (raceFinished) return

        raceFinished = true

        Toast.makeText(
            this,
            "${winner}勝利",
            Toast.LENGTH_SHORT
        ).show()

        btnStart.isEnabled = true

        // 同時取消兔子與烏龜的子協程
        raceJob?.cancel()
    }
}