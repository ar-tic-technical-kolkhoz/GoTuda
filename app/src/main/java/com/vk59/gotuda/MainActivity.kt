package com.vk59.gotuda

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.vk59.gotuda.core.commitWithAnimation

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    supportFragmentManager.commitWithAnimation {
      replace(R.id.fragment_container, MainFragment(), "start")
    }
    setContentView(R.layout.activity_main)
  }
}