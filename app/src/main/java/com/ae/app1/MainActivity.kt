package com.ae.app1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ae.app1.Constants.Companion.EXT_PNG
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mFirestoreDb by lazy { FirebaseFirestore.getInstance() }
    private val mCollectionRef by lazy { mFirestoreDb.collection(Constants.ITEMS) }

    private val mStorage by lazy { FirebaseStorage.getInstance() }
    private val mStorageRef by lazy { mStorage.getReference(Constants.ITEMS) }

    //private val mUser by lazy { Firebase.auth.currentUser }

    private lateinit var mItemAdapter: ItemAdapter

    // -------- Methods - Overridden  --------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Helper.log(Constants.TAG_ACTIVITY_MAIN, "onCreate")
        Helper.showToast(this, "onCreate")

        btnScan.setOnClickListener {
            Helper.openActivity(this, ScanQrActivity::class.java)
        }

        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        Helper.log(Constants.TAG_ACTIVITY_MAIN, "onStart")
        mItemAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        Helper.log(Constants.TAG_ACTIVITY_MAIN, "onStop")
        mItemAdapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        Helper.log(Constants.TAG_ACTIVITY_MAIN, "onDestroy")
    }


    // -------- Methods - User Defined --------

    private fun openScreen(cls: Class<*>, item: Item?) {
        val extras = Bundle()
        extras.putSerializable(Constants.ITEM, item)
        val intent = Intent(this@MainActivity, cls)
        intent.putExtras(extras)
        startActivity(intent)
    }

    private fun initRecyclerView() {

        val query = mCollectionRef.orderBy("name", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Item>()
            .setQuery(query, Item::class.java).build()

        mItemAdapter = ItemAdapter(this, options)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mItemAdapter

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                mItemAdapter.deleteItem(viewHolder.adapterPosition)
                deleteImageFile(mItemAdapter.getItemIdAt(viewHolder.adapterPosition))
                Helper.showToast(this@MainActivity, "Item delete successful")
            }

        }).attachToRecyclerView(recyclerView)

        // Unanimous inner class
        mItemAdapter.setOnItemClickListener(object : ItemAdapter.OnItemClickListener {
            override fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
                val id = documentSnapshot.id
                val path = documentSnapshot.reference.path
                Helper.log(Constants.TAG_ACTIVITY_MAIN, "Position=$position\nId=$id\nPath=$path\n")
                val document = documentSnapshot.toObject(Item::class.java)
                openScreen(CreateQrActivity::class.java, document)
            }
        })
    }

    /**
     * deleteImageFile: TODO: Update this method
     */
    private fun deleteImageFile(id: String) {
        val imageFile = mStorageRef.child("${id}${EXT_PNG}")
        imageFile.delete().addOnSuccessListener {
            Helper.log(Constants.TAG_ACTIVITY_MAIN, "Item Image delete successful")
        }.addOnFailureListener {
            Helper.log(Constants.TAG_ACTIVITY_MAIN, "Item Image delete fail. ${it.message}")
        }
    }
}