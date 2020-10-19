package com.jabustillo.karma.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jabustillo.karma.R
import com.jabustillo.karma.viewmodel.FavorViewModel
import kotlinx.android.synthetic.main.fragment_km5_favor.*

class Km5FavorFragment : Fragment() {
    val favorViewModel: FavorViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_km5_favor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        view.findViewById<Button>(R.id.km5SubmitButton).setOnClickListener{
            val km5Product = km5ProductTxt.text.toString()
            val km5Amount = km5AmountTxt.text.toString()
            val place = km5PlaceTxT.text.toString()

            if (km5Product != "" && km5Amount != "" && place != "") {
                favorViewModel.askFavor(place, "km5", km5Amount, km5Product) // Ask a km5 favor
                navController.navigate(R.id.homeFragment)
            }
        }
    }
}