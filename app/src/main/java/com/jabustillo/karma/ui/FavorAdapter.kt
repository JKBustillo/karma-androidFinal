package com.jabustillo.karma.ui

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jabustillo.karma.R
import com.jabustillo.karma.model.Favor
import com.jabustillo.karma.util.PreferenceProvider
import kotlinx.android.synthetic.main.favor_item.view.*

class FavorAdapter(favors: ArrayList<Favor>): RecyclerView.Adapter<FavorAdapter.ViewHolder>() {
    var favors : ArrayList<Favor>? = null
    var viewHolder: ViewHolder? = null

    init {
        this.favors = favors
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavorAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.favor_item, parent, false)
        viewHolder = ViewHolder(view)
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: FavorAdapter.ViewHolder, position: Int) {
        val favor = favors?.get(position)
        holder.id?.text = "Id: " + favor?.id
        holder.type?.text = "Type: " + favor?.type
        holder.state?.text = "State: " + favor?.state
        if (favor?.accepted == "") {
            holder.accepted?.text = "Accepted: No"
        } else {
            holder.accepted?.text = "Accepted: Yes"
        }
    }

    override fun getItemCount(): Int {
        return this.favors?.count()!!
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var id: TextView? = null
        var type: TextView? = null
        var state: TextView? = null
        var accepted: TextView? = null

        init {
            id = view.favorId
            type = view.favorType
            state = view.favorState
            accepted = view.favorAccepted
            view.setOnClickListener {
                val favorId = it.favorId.text.toString().substring(4, it.favorId.text.toString().length)
                PreferenceProvider.setValue("favorId", favorId)
                view.findNavController().navigate(R.id.favorDetailsFragment)
            }
        }
    }
}