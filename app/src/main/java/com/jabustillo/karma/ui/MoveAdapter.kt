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
import com.jabustillo.karma.model.Move
import com.jabustillo.karma.util.PreferenceProvider
import kotlinx.android.synthetic.main.favor_item.view.*
import kotlinx.android.synthetic.main.move_item.view.*

class MoveAdapter(moves: ArrayList<Move>): RecyclerView.Adapter<MoveAdapter.ViewHolder>() {
    var moves : ArrayList<Move>? = null
    var viewHolder: ViewHolder? = null

    init {
        this.moves = moves
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.move_item, parent, false)
        viewHolder = ViewHolder(view)
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: MoveAdapter.ViewHolder, position: Int) {
        val favor = moves?.get(position)
        holder.id?.text = "Id: " + favor?.id
        holder.type?.text = "Type: " + favor?.type
        holder.favor?.text = "Favor: " + favor?.favor
    }

    override fun getItemCount(): Int {
        return this.moves?.count()!!
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var id: TextView? = null
        var type: TextView? = null
        var favor: TextView? = null

        init {
            id = view.moveId
            type = view.typeId
            favor = view.moveFavorId
        }
    }
}