package com.raxdenstudios.navigation.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.raxdenstudios.navigation.NavigationManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        first_button_view.setOnClickListener { openSimpleActivity() }
        second_button_view.setOnClickListener { openTwoActivities() }
        third_button_view.setOnClickListener { openThreeActivities() }
    }

    private fun openSimpleActivity() {
        NavigationManager.Builder(this)
                .navigateToKClass(FirstActivity::class)
                .launch()
    }

    private fun openTwoActivities() {
        NavigationManager.Builder(this)
                .navigateToKClass(listOf(FirstActivity::class, SecondActivity::class))
                .launch()
    }

    private fun openThreeActivities() {
        NavigationManager.Builder(this)
                .navigateToKClass(listOf(FirstActivity::class, SecondActivity::class, ThirdActivity::class))
                .launch()
    }

}
