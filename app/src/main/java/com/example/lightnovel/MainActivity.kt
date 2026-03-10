package com.example.lightnovel

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnFav = findViewById<ImageButton>(R.id.btnFav)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)

        val favSecFragment = FavoriteSectionFragment()
        val profSecFragment = ProfileSectionFragment()
        val homeFragment = HomeFragment()

        // Set HomeFragment as default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.flSectionsLayout, homeFragment).commit()
        }

        btnFav.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flSectionsLayout, favSecFragment)
                addToBackStack(null)
                commit()
            }
        }

        btnProfile.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flSectionsLayout, profSecFragment)
                addToBackStack(null)
                commit()
            }
        }

        btnHome.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flSectionsLayout, homeFragment)
                addToBackStack(null)
                commit()
            }
        }
    }
}
