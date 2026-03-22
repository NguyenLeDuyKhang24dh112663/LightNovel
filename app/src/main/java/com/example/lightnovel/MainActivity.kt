package com.example.lightnovel

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TruyenViewModel
    private lateinit var edtSearch: EditText
    private lateinit var tvLogo: TextView
    private lateinit var ibSearch: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this).get(TruyenViewModel::class.java)

        val btnFav = findViewById<ImageButton>(R.id.btnFav)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        ibSearch = findViewById(R.id.ibSearch)
        edtSearch = findViewById(R.id.edtSearch)
        tvLogo = findViewById(R.id.textView)

        val favSecFragment = FavoriteSectionFragment()
        val profSecFragment = ProfileSectionFragment()
        val homeFragment = HomeFragment()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.flSectionsLayout, homeFragment).commit()
        }

        ibSearch.setOnClickListener {
            if (edtSearch.visibility == View.GONE) {
                tvLogo.visibility = View.GONE
                edtSearch.visibility = View.VISIBLE
                edtSearch.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT)
            } else {
                val query = edtSearch.text.toString()
                viewModel.search(query)
                hideKeyboard()
            }
        }

        edtSearch.addTextChangedListener {
            viewModel.search(it.toString())
        }

        btnFav.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.flSectionsLayout, favSecFragment).addToBackStack(null).commit()
            hideSearchUI()
        }

        btnProfile.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.flSectionsLayout, profSecFragment).addToBackStack(null).commit()
            hideSearchUI()
        }

        btnHome.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.flSectionsLayout, homeFragment).addToBackStack(null).commit()
            hideSearchUI()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText && edtSearch.visibility == View.VISIBLE) {
                val outRect = Rect()
                edtSearch.getGlobalVisibleRect(outRect)
                val ibRect = Rect()
                ibSearch.getGlobalVisibleRect(ibRect)
                
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()) && 
                    !ibRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideSearchUI()
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideSearchUI() {
        tvLogo.visibility = View.VISIBLE
        edtSearch.visibility = View.GONE
        // Optional: clear search when hiding
        // edtSearch.setText("")
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(edtSearch.windowToken, 0)
        edtSearch.clearFocus()
    }
}
