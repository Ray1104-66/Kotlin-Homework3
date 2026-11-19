package com.example.lab9_2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    // 宣告畫面元件
    private lateinit var btnCalculate: Button
    private lateinit var edHeight: EditText
    private lateinit var edWeight: EditText
    private lateinit var edAge: EditText

    private lateinit var tvWeightResult: TextView
    private lateinit var tvFatResult: TextView
    private lateinit var tvBmiResult: TextView
    private lateinit var tvProgress: TextView

    private lateinit var progressBar: ProgressBar
    private lateinit var llProgress: LinearLayout
    private lateinit var btnBoy: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 避免畫面被系統狀態列與導覽列遮住
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
        btnCalculate = findViewById(R.id.btnCalculate)
        edHeight = findViewById(R.id.edHeight)
        edWeight = findViewById(R.id.edWeight)
        edAge = findViewById(R.id.edAge)

        tvWeightResult = findViewById(R.id.tvWeightResult)
        tvFatResult = findViewById(R.id.tvFatResult)
        tvBmiResult = findViewById(R.id.tvBmiResult)
        tvProgress = findViewById(R.id.tvProgress)

        progressBar = findViewById(R.id.progressBar)
        llProgress = findViewById(R.id.llProgress)
        btnBoy = findViewById(R.id.btnBoy)

        // 初始化進度條
        progressBar.max = 100
        progressBar.progress = 0
        tvProgress.text = "0%"
        llProgress.visibility = View.GONE

        // 設定計算按鈕事件
        btnCalculate.setOnClickListener {
            checkInputAndCalculate()
        }
    }

    // 檢查輸入資料
    private fun checkInputAndCalculate() {

        val height = edHeight.text.toString()
            .trim().toDoubleOrNull()

        if (height == null || !height.isFinite() || height <= 0) {
            showToast("請輸入有效的身高（公分）")
            return
        }

        val weight = edWeight.text.toString()
            .trim().toDoubleOrNull()

        if (weight == null || !weight.isFinite() || weight <= 0) {
            showToast("請輸入有效的體重（公斤）")
            return
        }

        val age = edAge.text.toString()
            .trim().toDoubleOrNull()

        if (age == null || !age.isFinite() || age <= 0) {
            showToast("請輸入有效的年齡")
            return
        }

        // 保存按下按鈕時的性別選項
        val isBoy = btnBoy.isChecked

        // 通過檢查後，三個數值都確定不是 null
        runDetection(height, weight, age, isBoy)
    }

    // 使用協程模擬檢測過程
    private fun runDetection(
        height: Double,
        weight: Double,
        age: Double,
        isBoy: Boolean
    ) {

        // 檢測期間停用按鈕，避免重複啟動
        btnCalculate.isEnabled = false

        // 清除上一筆結果
        tvWeightResult.text = "標準體重\n無"
        tvFatResult.text = "體脂肪\n無"
        tvBmiResult.text = "BMI\n無"

        // 初始化並顯示進度條
        progressBar.progress = 0
        tvProgress.text = "0%"
        llProgress.visibility = View.VISIBLE

        // 在主執行緒啟動協程
        lifecycleScope.launch {

            try {
                // 每次等待 50ms，共執行 100 次
                for (progress in 1..100) {

                    // 暫停協程，不阻塞主執行緒
                    delay(50)

                    // 更新進度條與百分比
                    progressBar.progress = progress
                    tvProgress.text = "$progress%"
                }

                // 計算 BMI，將身高由公分換成公尺
                val bmi = weight / (height / 100).pow(2)

                // 依照原本公式計算標準體重與估計體脂率
                val (standWeight, bodyFat) = if (isBoy) {
                    Pair(
                        (height - 80) * 0.7,
                        1.39 * bmi + 0.16 * age - 19.34
                    )
                } else {
                    Pair(
                        (height - 70) * 0.6,
                        1.39 * bmi + 0.16 * age - 9
                    )
                }

                // 顯示結果，保留兩位小數
                tvWeightResult.text =
                    "標準體重\n${String.format("%.2f", standWeight)}"

                tvFatResult.text =
                    "體脂肪\n${String.format("%.2f", bodyFat)}"

                tvBmiResult.text =
                    "BMI\n${String.format("%.2f", bmi)}"

            } finally {
                // 完成或取消時，隱藏進度條並恢復按鈕
                llProgress.visibility = View.GONE
                btnCalculate.isEnabled = true
            }
        }
    }

    // 顯示提示訊息
    private fun showToast(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}