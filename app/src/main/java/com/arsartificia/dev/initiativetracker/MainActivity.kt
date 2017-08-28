package com.arsartificia.dev.initiativetracker

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlayerAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var playerList = ArrayList<Player>()
    private val filename = "playernames.bin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toolbar.title = resources.getString(R.string.app_name_long)
        supportActionBar?.title = resources.getString(R.string.app_name_long)

        fab.setOnClickListener { _ ->
            createAddDialog()
        }

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)

        loadData()
        adapter = PlayerAdapter(playerList, this)
        layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
        val animator = recyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && fab.isShown)
                    fab.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                    val builder = android.app.AlertDialog.Builder(this@MainActivity)
                    builder.setMessage("Are you sure to delete?")

                    builder.setPositiveButton("REMOVE", DialogInterface.OnClickListener { _, _ ->
                        adapter.notifyItemRemoved(position)
                        adapter.playerList.removeAt(position)
                        return@OnClickListener
                    }).setNegativeButton("CANCEL", DialogInterface.OnClickListener { _, _->
                        adapter.notifyItemChanged(position)
                        adapter.notifyDataSetChanged()
                        return@OnClickListener
                    }).show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        loadData()
    }

    private fun createAddDialog() {
        val builder = AlertDialog.Builder(this)

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val view = layoutInflater.inflate(R.layout.dialog_add, null)
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, id ->
                    adapter.playerList.add(Player(view.findViewById<EditText>(R.id.text_playername).text.toString(), 0))
                    adapter.sortPlayerList(true)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        builder.create().show()
    }

    override fun onStop() {
        super.onStop()
        writeData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
        try {
            val fis = openFileInput(filename)
            val ois = ObjectInputStream(fis)
            @Suppress("UNCHECKED_CAST")
            playerList = ois.readObject() as ArrayList<Player>
            ois.close()
        } catch (error: FileNotFoundException) {
            // This is okay. Happens on the first run
        } catch (error: Exception) {
            Snackbar.make(findViewById(R.id.coordinator_layout), error.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    fun writeData() {
        try {
            val fos = openFileOutput(filename, Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(adapter.playerList)
            oos.close()
        } catch (error: Exception) {
            Snackbar.make(findViewById(R.id.coordinator_layout), error.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
