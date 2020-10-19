package com.jabustillo.karma.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.jabustillo.karma.R
import com.jabustillo.karma.model.Favor
import com.jabustillo.karma.model.Move
import com.jabustillo.karma.viewmodel.AuthViewModel
import com.jabustillo.karma.viewmodel.FavorViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_my_favors.view.*
import kotlinx.coroutines.*

class HomeFragment : Fragment() {
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    val authViewModel: AuthViewModel by activityViewModels()
    private val favorViewModel: FavorViewModel by activityViewModels()

    private val adapter = MoveAdapter(ArrayList()) // adapter to the movement list
    var moves: MutableList<Move> = mutableListOf() // list of movements

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        moves.clear()
        moves = favorViewModel.getMoves() // get the user's movements
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbReference = database.reference.child("User")

        val user: FirebaseUser? = auth.currentUser

        var name = MutableLiveData<String>()
        var karma = MutableLiveData<String>()

        name.value = ""
        karma.value = ""
        // get the user's name and karma
        dbReference.child(user!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    name.value = snapshot.child("Name").value.toString()
                    karma.value = snapshot.child("Karma").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        // recycle adapter (movements)
        requireView().movesRecycle.adapter = adapter
        requireView().movesRecycle.layoutManager = LinearLayoutManager(requireContext())
        // observe the name and karma values
        name.observe(viewLifecycleOwner, Observer{ n ->
            nameHomeText.text = name.value
        })

        karma.observe(viewLifecycleOwner, Observer{ n ->
            karmaPointTexT.text = karma.value
        })

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(2000)
            // set the movements in the recycle
            adapter.moves?.clear()
            adapter.moves?.addAll(moves)
            adapter.notifyDataSetChanged()
        }
        // If user log out, go back to the login fragment
        authViewModel.logged().observe(viewLifecycleOwner, Observer { logged ->
            if (logged == "") {
                navController.navigate(R.id.loginFragment)
            }
        })
        // Go to the favors fragment
        view.findViewById<Button>(R.id.navToFavorsButton).setOnClickListener {
            navController.navigate(R.id.myFavorsFragment)
        }
        // Logout function
        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            authViewModel.logOut()
        }
    }
}