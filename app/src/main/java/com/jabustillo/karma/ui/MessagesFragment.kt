package com.jabustillo.karma.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jabustillo.karma.R
import com.jabustillo.karma.model.Message
import com.jabustillo.karma.viewmodel.AuthViewModel
import com.jabustillo.karma.viewmodel.MessageViewModel
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.fragment_message.view.*



class MessagesFragment : Fragment(R.layout.fragment_message) {

    val firebaseAuthViewModel: AuthViewModel by activityViewModels()
    val firebaseRealTimeDBViewModelViewModel : MessageViewModel by activityViewModels()
    private val adapter = MessagesAdapter(ArrayList())
    private var numMessagges = 0
    var userUid : String = "_"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyOut","MessagesFragment onViewCreated")

        requireView().message_recycler.adapter = adapter
        requireView().message_recycler.layoutManager = LinearLayoutManager(requireContext())

        firebaseAuthViewModel.logged().observe(getViewLifecycleOwner(), Observer { uid ->
            Log.d("MyOut","MessagesFragment logged with "+uid)
            userUid = uid
            adapter.uid = uid

        })

        firebaseRealTimeDBViewModelViewModel.ldMessageList.observe(getViewLifecycleOwner(), Observer { lista ->
            Log.d("MyOut","Número de mensajes "+lista.size)
            adapter.posts.clear()
            adapter.posts.addAll(lista)
            adapter.notifyDataSetChanged()
            numMessagges = lista.size + 1
            message_recycler.scrollToPosition(lista.size -1)
        })

        messageId.setOnClickListener {
            userUid = firebaseAuthViewModel.logged().value!!
            Log.d("MyOut","Writing message for user <"+userUid+">")
            firebaseRealTimeDBViewModelViewModel.writeNewMessage(
                Message(numMessagges,messageId.text.toString(), userUid)
            )
            messageId.text.clear()
        }

        backId.setOnClickListener {
            findNavController().navigate(R.id.myFavorsFragment)
        }
    }
}