package com.jabustillo.karma.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jabustillo.karma.R
import com.jabustillo.karma.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {
    private lateinit var nameTxt: EditText
    private lateinit var emailTxt: EditText
    private lateinit var passwordTxt: EditText
    private lateinit var confirmPasswordTxt: EditText

    val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // register inputs
        nameTxt = view.findViewById(R.id.nameTxt)
        emailTxt = view.findViewById(R.id.emailTxt)
        passwordTxt = view.findViewById(R.id.passwordTxt)
        confirmPasswordTxt = view.findViewById(R.id.confirmPasswordTxt)

        val navController = findNavController()

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            val name: String = nameTxt.text.toString()
            val email: String = emailTxt.text.toString()
            val password: String = passwordTxt.text.toString()
            val confirmPassword: String = confirmPasswordTxt.text.toString()
            // check if the inputs are empty
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                if(password == confirmPassword) {
                    authViewModel.register(name, email, password, navController.navigate(R.id.homeFragment)) // register with firebase and navigate to the home if you log in
                } else {
                    Toast.makeText(getActivity(), "Passwords don't match", Toast.LENGTH_SHORT).show() // show a toast with the error
                }
            }
        }
        // go to the login fragment
        view.findViewById<Button>(R.id.NavToLoginbutton).setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }
    }
}