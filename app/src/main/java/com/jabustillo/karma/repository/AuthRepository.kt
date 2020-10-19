package com.jabustillo.karma.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    var logged = MutableLiveData<String>()
    var userCreated = MutableLiveData<Boolean>()

    init {
        logged.value = ""
    }
    // Function to register a user in firebase
    fun register(name: String, email: String, password: String, func: Unit) {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbReference = database.reference.child("User") // reference to the User collection

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    // if it's successful, the user is saved in the db with the name and a default karme of 2 points
                    val userDB = dbReference.child(user!!.uid)
                    userDB.child("Name").setValue(name)
                    userDB.child("Karma").setValue(2)
                    userCreated.value = true
                    logged.value = user!!.uid // change the logged value with the id to navigate to the private view and handle it in the ui
                } else {
                    Log.d("Error", "Error in register")
                    userCreated.value = false
                }
            }
    }
    // Function to login with firebase
    fun login(email: String, password: String, context: Context) {
        auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    logged.value = user!!.uid // change the logged value with the id to navigate to the private view and handle it in the ui
                } else {
                    Log.d("Error", "Error in login")
                    Toast.makeText(
                        context, "Email or password wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    fun logOut(){
        logged.value = "" // Clean the logged value to go back to the login view
        auth = FirebaseAuth.getInstance()
        auth.signOut() // Log out with firebase
    }
}