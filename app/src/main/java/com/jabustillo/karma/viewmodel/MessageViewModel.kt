package com.jabustillo.karma.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener

import com.jabustillo.karma.model.Message

class MessageViewModel : ViewModel(){
//    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
//    private lateinit var auth: FirebaseAuth


    var ldMessageList = MutableLiveData<List<Message>>()
    val messageList = mutableListOf<Message>()

    init{
        getValues()
    }

    fun writeNewMessage(message: Message){
        database = FirebaseDatabase.getInstance()
        database.reference.child("messages").push().setValue(message)
    }

    fun getValues(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear()
                for (childDataSnapshot in dataSnapshot.children) {
                    val message: Message = childDataSnapshot.getValue(Message::class.java)!!
                    //Log.v("MyOut", "" + childDataSnapshot.getKey()); //displays the key for the node
                    //Log.v("MyOut", "" + message.id);
                    messageList.add(message)
                }
                ldMessageList.value = messageList

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("MyOut", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        database = FirebaseDatabase.getInstance()
        database.reference.child("messages").addValueEventListener(postListener)

    }


}