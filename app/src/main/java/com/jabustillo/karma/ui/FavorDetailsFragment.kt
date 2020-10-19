package com.jabustillo.karma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.*
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.jabustillo.karma.R
import com.jabustillo.karma.util.PreferenceProvider
import com.jabustillo.karma.viewmodel.FavorViewModel
import kotlinx.android.synthetic.main.fragment_favor_details.*
import kotlinx.coroutines.*

class FavorDetailsFragment : Fragment() {
    private lateinit var favorId : String
    private val favorViewModel: FavorViewModel by activityViewModels()

    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favorId = PreferenceProvider.getValue("favorId") // get the id of the current favor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favor_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val navController = findNavController()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val user: FirebaseUser? = auth.currentUser

        lateinit var currentUser: String

        dbReference = database.reference.child("User")
        dbReference.child(user!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    currentUser = snapshot.child("Name").value.toString() // get the user logged right now in the app
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        dbReference = database.reference.child("Favor")
        // Declare and get the values of the favor
        val id = MutableLiveData<String>()
        val favorUser = MutableLiveData<String>()
        val name = MutableLiveData<String>()
        val type = MutableLiveData<String>()
        val code = MutableLiveData<String>()
        val place = MutableLiveData<String>()
        val state = MutableLiveData<String>()
        val completed = MutableLiveData<Boolean>()
        val acceptedBy = MutableLiveData<String>()
        val acceptedByUser = MutableLiveData<String>()
        val product = MutableLiveData<String>()

        dbReference.child(favorId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    id.value = snapshot.child("id").value.toString()
                    favorUser.value = snapshot.child("user").value.toString()
                    type.value = snapshot.child("type").value.toString()
                    code.value = snapshot.child("code").value.toString()
                    place.value = snapshot.child("place").value.toString()
                    state.value = snapshot.child("state").value.toString()
                    completed.value = snapshot.child("completed").value.toString().toBoolean()
                    acceptedBy.value = snapshot.child("acceptedBy").value.toString()

                    if (type.value == "km5") {
                        product.value = snapshot.child("product").value.toString()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(200)
            dbReference = database.reference.child("User")
            dbReference.child(favorUser.value.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        name.value = snapshot.child("Name").value.toString() // get the name of favor's owner
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            if(acceptedBy.value != "") {
                dbReference.child(acceptedBy.value.toString()).addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            acceptedByUser.value = snapshot.child("Name").value.toString() // get the name of the one who accept the favor if exists
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

            lateinit var changeState: String
            // Function to change the text in the button
            fun assignValues() {
                GlobalScope.launch(context = Dispatchers.Main) {
                    delay(500)
                    if (state.value == "completed") {
                        changeStateButton.text = "Favor completed"
                        changeStateButton.isEnabled = !completed.value.toString().toBoolean()
                    } else {
                        if (user!!.uid == favorUser.value.toString()) {
                            changeStateButton.text = "Set as completed"
                            changeStateButton.isEnabled = completed.value.toString().toBoolean()
                            changeState = "setAsCompleted"
                        } else {
                            if (state.value == "initial") {
                                changeStateButton.text = "Assing favor to me"
                                changeStateButton.isEnabled = !completed.value.toString().toBoolean()
                                changeState = "assignFavor"
                            } else if (state.value == "assigned") {
                                changeStateButton.text = "Set as completed"
                                changeState = "setAsCompletedPartial"
                                if (acceptedBy.value == user!!.uid) {
                                    changeStateButton.isEnabled =
                                        !completed.value.toString().toBoolean()
                                } else {
                                    changeStateButton.isEnabled = completed.value.toString().toBoolean()
                                }
                            }
                        }
                    }
                    // Set the value in the textviews
                    if (acceptedBy.value == "") {
                        acceptedByUser.value = "Nobody"
                    }

                    favorIdTxt.text = "Favor ID: " + id.value
                    favorUserTxt.text = "Asked by: " + name.value
                    favorTypeTxt.text = "Type: " + type.value
                    if (type.value == "km5") {
                        favorCodeTxt.text = "Product: " + product.value + ", amount: " + code.value
                    } else {
                        favorCodeTxt.text = "Code: " + code.value
                    }
                    favorPlaceTxt.text = "Place: " + place.value
                    favorStateTxt.text = "State: " + state.value
                    favorAcceptedTxt.text = "Accepted by: " + acceptedByUser.value
                }
            }

            assignValues() // Run the function declarated above

            view.findViewById<Button>(R.id.changeStateButton).setOnClickListener{
                when(changeState) { //functions to change the state of the favors
                    "assignFavor" -> favorViewModel.assignFavor(state, favorId, acceptedBy, currentUser, user)
                    "setAsCompletedPartial" -> favorViewModel.setAsCompletedPartial(completed, favorId)
                    "setAsCompleted" -> favorViewModel.setAsCompleted(state, favorId, acceptedBy)
                }

                val navController = findNavController()
                navController.navigate(R.id.favorDetailsFragment) // reset the fragment
            }

        }
        view.findViewById<Button>(R.id.chatButton).setOnClickListener{
            findNavController().navigate(R.id.messagesFragment) // Go to the chat
        }

        view.findViewById<Button>(R.id.goBackButton).setOnClickListener{
            findNavController().navigate(R.id.myFavorsFragment) // Go to the favors
        }
    }
}