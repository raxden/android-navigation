package com.raxdenstudios.navigation.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)

        intent.extras

    }

}