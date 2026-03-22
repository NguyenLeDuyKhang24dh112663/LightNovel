package com.example.lightnovel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileSectionFragment : Fragment() {

    private lateinit var db: AccountDatabaseHelper
    private var currentAccount: Account? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        return if (isLoggedIn) {
            inflater.inflate(R.layout.fragment_profile, container, false)
        } else {
            inflater.inflate(R.layout.fragment_not_logged_in, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val username = sharedPref.getString("username", "") ?: ""
            db = AccountDatabaseHelper(requireContext())
            loadProfileData(view, username)

            view.findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
                val editProfileFragment = EditProfileFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.flSectionsLayout, editProfileFragment)
                    .addToBackStack(null)
                    .commit()
            }

            view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", false)
                    remove("username")
                    apply()
                }
                requireActivity().recreate()
            }
        } else {
            view.findViewById<Button>(R.id.button).setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
            view.findViewById<Button>(R.id.button4).setOnClickListener {
                startActivity(Intent(requireContext(), SigninActivity::class.java))
            }
        }
    }

    private fun loadProfileData(view: View, username: String) {
        currentAccount = db.getAccountByUsername(username)
        currentAccount?.let {
            view.findViewById<TextView>(R.id.profSurname).text = it.surname
            view.findViewById<TextView>(R.id.prof1stName).text = it.firstName
            view.findViewById<TextView>(R.id.profUser).text = it.username
            view.findViewById<TextView>(R.id.profDoB).text = it.dob
            view.findViewById<TextView>(R.id.profEmail).text = it.email
        }
    }
}
