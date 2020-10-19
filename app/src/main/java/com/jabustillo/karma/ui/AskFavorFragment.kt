package com.jabustillo.karma.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.jabustillo.karma.R

class AskFavorFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ask_favor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        // navigate to the view of each favor
        view.findViewById<Button>(R.id.navToPhotoFavor).setOnClickListener {
            navController.navigate(R.id.photocopyFavorFragment)
        }

        view.findViewById<Button>(R.id.navToKm5Favor).setOnClickListener {
            navController.navigate(R.id.km5FavorFragment)
        }

        view.findViewById<Button>(R.id.navToDeliveryFavor).setOnClickListener {
            navController.navigate(R.id.deliveryFavorFragment)
        }
    }
}