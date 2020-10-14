package com.ibotta.gradle.aop.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ibotta.gradle.aop.kotlin.R
import kotlinx.android.synthetic.main.activity_main.b_button_kotlin
import kotlinx.android.synthetic.main.activity_main.tv_message

class MainActivity : AppCompatActivity(), MessageListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        b_button_kotlin.setOnClickListener {
            tv_message.text = ""
            KotlinTargetExample().demonstrateKotlinAOP(this)
        }
    }

    override fun onMessage(message: String, callerType: CallerType) {
        tv_message.append("$message\n")
    }
}