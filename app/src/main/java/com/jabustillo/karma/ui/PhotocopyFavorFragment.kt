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
import kotlinx.android.synthetic.main.fragment_photocopy_favor.*

class PhotocopyFavorFragment : Fragment() {
    val favorViewModel: FavorViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photocopy_favor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        view.findViewById<Button>(R.id.photocopySubmitButton).setOnClickListener{
            val photocopyCode = photocopyCodeTxt.text.toString()
            val place = photocopyPlaceTxT.text.toString()

            if (photocopyCode != "" && place != "") {
                favorViewModel.askFavor(place, "photocopy", photocopyCode, "") // Ask a photocopy favor
                navController.navigate(R.id.homeFragment)
            }
        }
    }
}