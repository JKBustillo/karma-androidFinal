package com.jabustillo.karma.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.jabustillo.karma.model.Favor
import com.jabustillo.karma.model.Move
import com.jabustillo.karma.util.PreferenceProvider
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import java.time.LocalDateTime

class FavorRepository {
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    // Function to ask a favor
    fun askFavor(place: String, type: String, code: String, product: String) {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val user: FirebaseUser? = auth.currentUser
        val id = user!!.uid

        dbReference = database.reference.child("Favor") // Reference to the favor collection
        val idFavor = dbReference.push().key.toString() // Generate a random id for the favor

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(200)

            var karma = 0

            dbReference = database.reference.child("User")
            dbReference.child(user!!.uid).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        karma = snapshot.child("Karma").value.toString().toInt()-2 // Decrease the karma when user ask a favor
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            GlobalScope.launch(context = Dispatchers.Main) {
                delay(500)
                dbReference.child(user!!.uid).child("Karma").setValue(karma)
                // Store the favor in the db
                dbReference = database.reference.child("Favor")
                val favorDB = dbReference.child(idFavor)
                favorDB.child("id").setValue(idFavor)
                favorDB.child("user").setValue(id)
                favorDB.child("place").setValue(place)
                favorDB.child("date").setValue(LocalDateTime.now())
                favorDB.child("type").setValue(type)
                favorDB.child("state").setValue("initial")
                favorDB.child("acceptedBy").setValue("")
                favorDB.child("completed").setValue(false)
                favorDB.child("code").setValue(code)
                favorDB.child("karma").setValue(karma)

                if (type == "km5") {
                    favorDB.child("product").setValue(product)
                }
                // Add a movement to the user
                setNewMove("Ask favor", id)
            }
        }


    }
    //Function to get all the favors in the db
    fun getFavors(): MutableList<Favor> {
        var favors: MutableList<Favor> = mutableListOf()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbReference = database.reference.child("Favor") // Reference to the Favor collection

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if(PreferenceProvider.getValue("favors") == "favors") {
                        snapshot.children.forEach {
                            val favorId = it.child("id").value.toString()
                            val favorType = it.child("type").value.toString()
                            val favorState = it.child("state").value.toString()
                            val favorAccepted = it.child("acceptedBy").value.toString()
                            val favorUser = it.child("user").value.toString()
                            val favorKarma = it.child("karma").value.toString().toInt()
                            // get every favor and assign it in a list
                            favors.add(0, Favor(favorId, favorType, favorState, favorAccepted, favorUser, favorKarma))
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return favors // returning the list of favors
    }
    // Function to assing a favor to yourself
    fun assignFavor(state: MutableLiveData<String>, favorId: String, acceptedBy: MutableLiveData<String>, currentUser: String, user: FirebaseUser) {
        database = FirebaseDatabase.getInstance()
        state.value = "assigned"
        acceptedBy.value = currentUser
        dbReference = database.reference.child("Favor")
        dbReference.child(favorId).child("state").setValue("assigned") // change the state of the favor
        dbReference.child(favorId).child("acceptedBy").setValue(user!!.uid) // set the id who is assigning the favor
        setNewMove("Assign Favor", favorId)// Add a movement to the user
    }
    // Function to set the favor as completed waiting for the other person to confirm
    fun setAsCompletedPartial(completed: MutableLiveData<Boolean>, favorId: String) {
        database = FirebaseDatabase.getInstance()
        completed.value = true
        dbReference = database.reference.child("Favor")
        dbReference.child(favorId).child("completed").setValue(true)// change the state of the favor
        setNewMove("Set as Completed", favorId)// Add a movement to the user
    }
    // Function to confirm if the favor is cmmpleted
    fun setAsCompleted(state: MutableLiveData<String>, favorId: String, acceptedBy: MutableLiveData<String>) {
        database = FirebaseDatabase.getInstance()
        state.value = "completed"
        dbReference = database.reference.child("Favor")
        dbReference.child(favorId).child("state").setValue("completed")// change the state of the favor
        setNewMove("Set as Completed", favorId)// Add a movement to the user

        var karma = 0

        dbReference = database.reference.child("User")
        dbReference.child(acceptedBy.value.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    karma = snapshot.child("Karma").value.toString().toInt()+1 // Increase the karma when the favor is completed
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(1000)
            dbReference.child(acceptedBy.value.toString()).child("Karma").setValue(karma) // set the new karma in the db
        }
    }
    // Function to set a new movement in the user
    fun setNewMove(type: String, favorId: String) {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val user: FirebaseUser? = auth.currentUser
        val id = user!!.uid

        dbReference = database.reference.child("User").child(id).child("Moves") // get the reference to the user's movements

        val idMove = dbReference.push().key.toString() // generate a random id for the movement

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(500)
            // set the movement in the db
            val move = dbReference.child(idMove)
            move.child("id").setValue(idMove)
            move.child("favor").setValue(favorId)
            move.child("date").setValue(LocalDateTime.now())
            move.child("type").setValue(type)
        }
    }
    // Function to get the user's movements
    fun getMoves(): MutableList<Move> {
        var moves: MutableList<Move> = mutableListOf()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val user: FirebaseUser? = auth.currentUser
        val id = user!!.uid

        dbReference = database.reference.child("User").child(id).child("Moves") // get the reference to the user's list of movements

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        val moveId = it.child("id").value.toString()
                        val moveType = it.child("type").value.toString()
                        val favorId = it.child("favor").value.toString()
                        // get every movement and assign it in a list
                        moves.add(0, Move(moveId, moveType, favorId))
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        // return the list of movements
        return moves
    }
}