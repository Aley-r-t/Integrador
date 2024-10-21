package com.kotlin.integrador.ui.newendpoint

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kotlin.integrador.R
import com.kotlin.integrador.databinding.ActivityMainBinding
import com.kotlin.integrador.databinding.ActivityNewEndpointBinding

class NewEndpoint : AppCompatActivity() {

    lateinit var binding: ActivityNewEndpointBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding =  ActivityNewEndpointBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        setContentView(binding.root)

        setContentView(R.layout.activity_new_endpoint)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}