package com.sdzshn3.android.websocketpractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.sdzshn3.android.websocketpractice.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.doneButton.setOnClickListener {
            val username = binding.usernameET.text.toString().trim()
            if (username.isBlank()) {
                Toast.makeText(this, "username cannot be blank", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }
}