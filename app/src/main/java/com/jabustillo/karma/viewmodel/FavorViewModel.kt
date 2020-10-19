package com.jabustillo.karma.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jabustillo.karma.model.Favor
import com.jabustillo.karma.model.Move
import com.jabustillo.karma.repository.FavorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavorViewModel: ViewModel()  {
    private val repository = FavorRepository()

    private var moves: MutableList<Move> = mutableListOf()

    fun askFavor(place: String, type: String, code: String, product: String) = repository.askFavor(place, type, code, product)

    fun getFavors() = repository.getFavors()

    fun assignFavor(state: MutableLiveData<String>, favorId: String, acceptedBy: MutableLiveData<String>, currentUser: String, user: FirebaseUser) = repository.assignFavor(state, favorId, acceptedBy, currentUser, user)

    fun setAsCompletedPartial(completed: MutableLiveData<Boolean>, favorId: String) = repository.setAsCompletedPartial(completed, favorId)

    fun setAsCompleted(state: MutableLiveData<String>, favorId: String, acceptedBy: MutableLiveData<String>) = repository.setAsCompleted(state, favorId, acceptedBy)

    fun setNewMove(type: String, favorId: String,) = repository.setNewMove(type, favorId)

    fun getMoves() = repository.getMoves()
}