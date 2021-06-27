package com.kontrakanprojects.tyreom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kontrakanprojects.tyreom.databinding.ActivityAuthBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}