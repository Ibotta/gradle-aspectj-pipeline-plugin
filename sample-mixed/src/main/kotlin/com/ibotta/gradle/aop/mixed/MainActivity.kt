package com.ibotta.gradle.aop.mixed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.b_button_java
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

        b_button_java.setOnClickListener {
            tv_message.text = ""
            JavaTargetExample().demonstrateJavaAOP(this)
        }
    }

    override fun onMessage(message: String, callerType: CallerType) {
        tv_message.append("$message\n")
    }
}