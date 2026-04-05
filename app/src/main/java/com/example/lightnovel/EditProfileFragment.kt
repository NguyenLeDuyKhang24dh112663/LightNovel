package com.example.lightnovel

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class EditProfileFragment : Fragment() {

    private lateinit var db: AccountDatabaseHelper
    private var currentAccount: Account? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AccountDatabaseHelper(requireContext())
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        currentAccount = db.getAccountByUsername(username)

        val editSurname = view.findViewById<TextInputEditText>(R.id.editSurname)
        val editFirstName = view.findViewById<TextInputEditText>(R.id.editFirstName)
        val editPhone = view.findViewById<TextInputEditText>(R.id.editPhone)
        val editDoB = view.findViewById<TextInputEditText>(R.id.editDoB)
        val editGender = view.findViewById<AutoCompleteTextView>(R.id.editGender)
        val editPassword = view.findViewById<TextInputEditText>(R.id.editPassword)
        val editConfirmPass = view.findViewById<TextInputEditText>(R.id.editConfirmPass)
        val editUsername = view.findViewById<TextInputEditText>(R.id.editUsername)
        val editEmail = view.findViewById<TextInputEditText>(R.id.editEmail)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val ivBack = view.findViewById<ImageView>(R.id.ivBack)

        // Nhập trước thông tin củ a tài khoản
        currentAccount?.let {
            editUsername.setText(it.username)
            editPassword.setText(it.password)
            editEmail.setText(it.email)
            editConfirmPass.setText(it.password)
            editSurname.setText(it.surname)
            editFirstName.setText(it.firstName)
            editPhone.setText(it.phone ?: "")
            editDoB.setText(it.dob)

            editGender.setText(it.gender, false)
        }

        // Bảng chọn ngày cho DoB
        editDoB.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                editDoB.setText(String.format("%02d/%02d/%d", day, month + 1, year))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // dropdown giới tính - Sử dụng layout tùy chỉnh item_dropdown_genre
        val genders = arrayOf("Nam", "Nữ", "Khác")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_genre, genders)
        editGender.setAdapter(adapter)
        
        editGender.setOnClickListener {
            editGender.showDropDown()
        }

        btnSave.setOnClickListener {
            val updatedAccount = currentAccount?.copy(
                surname = editSurname.text.toString(),
                firstName = editFirstName.text.toString(),
                phone = editPhone.text.toString(),
                dob = editDoB.text.toString(),
                gender = editGender.text.toString(),
                username = editUsername.text.toString(),
                email = editEmail.text.toString(),
                password = editPassword.text.toString()
            )

            if (updatedAccount != null) {
                val rows = db.updateAccount(updatedAccount, username)
                if (rows > 0) {
                    // Update session if username changed
                    if (updatedAccount.username != username) {
                        sharedPref.edit().putString("username", updatedAccount.username).apply()
                    }
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
