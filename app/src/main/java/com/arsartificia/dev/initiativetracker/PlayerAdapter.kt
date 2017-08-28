package com.arsartificia.dev.initiativetracker

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


class PlayerAdapter(var playerList: ArrayList<Player>, private val ma: MainActivity) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(view: View) :  RecyclerView.ViewHolder(view) {
        var name = view.findViewById<View>(R.id.text_name) as TextView
        var initiative = view.findViewById<View>(R.id.text_initiavite) as TextView
        var btn_minus = view.findViewById<Button>(R.id.btn_minus) as Button
        var btn_minus5 = view.findViewById<Button>(R.id.btn_minus5) as Button
        var btn_plus = view.findViewById<Button>(R.id.btn_plus) as Button
        var btn_plus5 = view.findViewById<Button>(R.id.btn_plus5) as Button
        var btn_turn = view.findViewById<Button>(R.id.btn_turn) as Button
        var card_layout = view.findViewById<View>(R.id.cardlayout) as CardView
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PlayerViewHolder{
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.player_list_row, parent, false)
        return PlayerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, i: Int) {
        val player = playerList[i]
        holder.name.text = player.name
        holder.initiative.text = player.initiative.toString()

        holder.btn_minus.setOnClickListener { modifyInitiative(holder, player, {it - 1}, i) }
        holder.btn_minus5.setOnClickListener { modifyInitiative(holder, player, {it - 5}, i) }
        holder.btn_plus.setOnClickListener { modifyInitiative(holder, player, {it + 1}, i) }
        holder.btn_plus5.setOnClickListener { modifyInitiative(holder, player, {it + 5}, i) }
        holder.btn_turn.setOnClickListener { onTurnButton(holder, player, i) }
        if (player.turn) {
            holder.card_layout.setCardBackgroundColor(ContextCompat.getColor(ma.applicationContext, R.color.colorAccentLight))
        } else {
            holder.card_layout.setCardBackgroundColor(ContextCompat.getColor(ma.applicationContext, R.color.colorBackground))
        }
    }

    private fun onTurnButton(holder: PlayerViewHolder, player: Player, pos: Int) {
        if (player.turn) {
            Snackbar.make(ma.coordinator_layout, R.string.turn_twice, Snackbar.LENGTH_SHORT).show()
        } else {
            player.turn = true
            modifyInitiative(holder, player, {it - 10}, pos)
        }
    }

    private fun modifyInitiative(holder: PlayerViewHolder, player: Player, mod: (Int) -> Int, pos: Int) {
        player.initiative = mod(player.initiative)
        sortPlayerList()
        if (playerList.all { it.turn }) {
            Snackbar.make(ma.coordinator_layout, R.string.turn_done, Snackbar.LENGTH_SHORT).show()
            playerList.forEach { it.turn = false }
            ma.recycler_view.childCount.rangeTo(0)
            for (i in 0..ma.recycler_view.childCount - 1) {
                val child = ma.recycler_view.getChildAt(i)
                //In case you need to access ViewHolder:
                val vh = ma.recycler_view.getChildViewHolder(child) as PlayerViewHolder
                vh.card_layout.setCardBackgroundColor(ContextCompat.getColor(ma.applicationContext, R.color.colorBackground))
            }
        }
    }

    fun sortPlayerList(notify: Boolean = true) {
        playerList.sortBy { it.initiative }
        playerList.reverse()
        if (notify) {
            notifyDataSetChanged()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}