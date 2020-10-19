package com.jabustillo.karma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.jabustillo.karma.R
import com.jabustillo.karma.model.Favor
import com.jabustillo.karma.util.PreferenceProvider
import com.jabustillo.karma.viewmodel.FavorViewModel
import kotlinx.android.synthetic.main.fragment_my_favors.*
import kotlinx.android.synthetic.main.fragment_my_favors.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyFavorsFragment : Fragment() {
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private val favorViewModel: FavorViewModel by activityViewModels()

    private val adapter = FavorAdapter(ArrayList()) // favor adapter for the recycle view
    var favors: MutableList<Favor> = mutableListOf() // list of all the favors
    var favorsFiltered: MutableList<Favor> = mutableListOf() // list of favors when you filter them

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceProvider.setValue("favors", "favors")
        favors = favorViewModel.getFavors() // get all the favors
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_favors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // favor recycle view
        requireView().favorsRecycle.adapter = adapter
        requireView().favorsRecycle.layoutManager = LinearLayoutManager(requireContext())
        // set to view all the favors by default
        allCategoryButton.isChecked = true
        everyoneFavors.isChecked = true

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("User")

        val user: FirebaseUser? = auth.currentUser
        val id = user!!.uid
        var karma = 0

        dbReference.child(id).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    karma = snapshot.child("Karma").value.toString().toInt() // get the karma of the current user
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(1000)
            // sort the favors by thee karma
            favors.sortBy { Favor -> Favor.karma }
            favors.reverse()
            // set the favors in the recycleview
            adapter.favors?.clear()
            adapter.favors?.addAll(favors)
            adapter.notifyDataSetChanged()
            // set to view all the favors by default
            allCategoryButton.isChecked = true
            everyoneFavors.isChecked = true

            categoryGroup.setOnCheckedChangeListener { group, checkedId ->
                favorsFiltered.clear()
                when(checkedId) {
                    R.id.allCategoryButton -> { // show all the favors
                        favors.forEach{
                            if(myFavorsButton.isChecked) {
                                if (it.user == id) {
                                    favorsFiltered.add(favorsFiltered.size, it)
                                }
                            } else {
                                favorsFiltered.add(favorsFiltered.size, it)
                            }
                        }
                    }
                    R.id.photocopyCategoryButton -> { // show the photocooy favors
                        favors.forEach{
                            if (it.type == "photocopy") {
                                if(myFavorsButton.isChecked) {
                                    if (it.user == id) {
                                        favorsFiltered.add(favorsFiltered.size, it)
                                    }
                                } else {
                                    favorsFiltered.add(favorsFiltered.size, it)
                                }
                            }
                        }
                    }
                    R.id.km5CategoryButton -> { // show the km5 favors
                        favors.forEach{
                            if (it.type == "km5") {
                                if(myFavorsButton.isChecked) {
                                    if (it.user == id) {
                                        favorsFiltered.add(favorsFiltered.size, it)
                                    }
                                } else {
                                    favorsFiltered.add(favorsFiltered.size, it)
                                }
                            }
                        }
                    }
                    R.id.deliveryCategoryButton -> { // show the delivery favors
                        favors.forEach{
                            if (it.type == "delivery") {
                                if(myFavorsButton.isChecked) {
                                    if (it.user == id) {
                                        favorsFiltered.add(favorsFiltered.size, it)
                                    }
                                } else {
                                    favorsFiltered.add(favorsFiltered.size, it)
                                }
                            }
                        }
                    }
                }
                // set the filtered favors in the recycleview
                adapter.favors?.clear()
                adapter.favors?.addAll(favorsFiltered)
                adapter.notifyDataSetChanged()
            }

            favorsGroup.setOnCheckedChangeListener { group, checkedId ->
                favorsFiltered.clear()
                when(checkedId) {
                    R.id.everyoneFavors -> { // show every user's favors but check if another filter is set
                        favors.forEach{
                            if(photocopyCategoryButton.isChecked) {
                                if (it.type == "photocopy") {
                                    favorsFiltered.add(favorsFiltered.size, it)
                                }
                            } else if (km5CategoryButton.isChecked) {
                                if (it.type == "km5") {
                                    favorsFiltered.add(favorsFiltered.size, it)
                                }
                            } else if (deliveryCategoryButton.isChecked) {
                                if (it.type == "delivery") {
                                    favorsFiltered.add(favorsFiltered.size, it)
                                }
                            } else {
                                favorsFiltered.add(favorsFiltered.size, it)
                            }
                        }
                    }
                    R.id.myFavorsButton -> {// show the current user's favors but check if another filter is set
                        favors.forEach{
                            if (it.user == id) {
                                if(photocopyCategoryButton.isChecked) {
                                    if (it.type == "photocopy") {
                                        favorsFiltered.add(favorsFiltered.size, it)
                                    }
                                } else if (km5CategoryButton.isChecked) {
                                    if (it.type == "km5") {
                                        favorsFiltered.add(favorsFiltered.size, it)
                                    }
                                } else if (deliveryCategoryButton.isChecked) {
                                    if (it.type == "delivery") {
                                        favorsFiltered.add(favorsFiltered.size, it)
                                    }
                                } else {
                                    favorsFiltered.add(favorsFiltered.size, it)
                                }
                            }
                        }
                    }
                }
                // set the filtered favors in the recycleview
                adapter.favors?.clear()
                adapter.favors?.addAll(favorsFiltered)
                adapter.notifyDataSetChanged()
            }
        }

        val navController = findNavController()

        view.findViewById<Button>(R.id.NavToAskFavorButton).setOnClickListener{
            if(karma >= 2) { // check if the user's karma is 2 or above to ask a favor
                PreferenceProvider.setValue("favors", "")
                navController.navigate(R.id.askFavorFragment)
            } else {
                Toast.makeText(this.requireContext(), "You don't have enough karma to ask a favor", Toast.LENGTH_LONG).show()
            }
        }
        // Go to home fragment
        view.findViewById<Button>(R.id.navToHome).setOnClickListener{
            navController.navigate(R.id.homeFragment)
        }
    }
}