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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jabustillo.karma.R
import com.jabustillo.karma.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var emailLoginTxt: EditText
    private lateinit var passwordLoginTxt: EditText

    val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // login inputs
        emailLoginTxt = view.findViewById(R.id.emailLoginTxt)
        passwordLoginTxt = view.findViewById(R.id.passwordLoginTxt)

        val navController = findNavController()
        // navigate to the home if you log in
        authViewModel.logged().observe(viewLifecycleOwner, Observer { logged ->
            if (logged != "") {
                navController.navigate(R.id.homeFragment)
            }
        })

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email: String = emailLoginTxt.text.toString() // get the values from the inputs
            val password: String = passwordLoginTxt.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                authViewModel.login(email, password, this.requireContext()) // login with firebase
            }
        }
        // Go to register fragment
        view.findViewById<Button>(R.id.NavToRegisterbutton).setOnClickListener {
            navController.navigate(R.id.registerFragment)
        }
    }
}