package com.example.trash.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trash.R
import kotlinx.android.synthetic.main.activity_chatting.*
import com.example.trash.navigation.model.ChattingDTO
import com.example.trash.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_chatting.comment_recyclerview
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_message.*
import kotlinx.android.synthetic.main.item_message.view.*

class chatting : AppCompatActivity() {
    var destinationUID : String? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var chatRoomUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        destinationUID = intent.getStringExtra("destinationUid")

        messageActivity_button?.setOnClickListener{
            var chatting = ChattingDTO()
            chatting?.userSender = uid
            chatting?.userReceiver = destinationUID
            chatting?.users.put(uid!!, true)
            chatting?.users.put(destinationUID!!, true)

            if(chatRoomUid == null){
                firestore?.collection("chatrooms")?.document()?.set(chatting)?.addOnSuccessListener {
                    checkroom()
                }
            }else{
                var comment = ChattingDTO.Comment()
                comment.userId = uid
                comment.message = messageActivity_editText.text.toString()
                comment.timestamp = System.currentTimeMillis()
                firestore?.collection("chatrooms")?.document(chatRoomUid!!)?.collection("comments")?.document()?.set(comment)
            }


        }
        checkroom()
    }

    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var comments : ArrayList<ChattingDTO.Comment> = arrayListOf()
        init{
            /*firestore?.collection("chatrooms")?.document(chatRoomUid!!)?.collection("comments")?.get()?.addOnSuccessListener { documents ->
                comments.clear()
                if(documents == null) return@addOnSuccessListener

                for (document in documents) {
                    var item = document.toObject(ChattingDTO.Comment::class.java)
                    comments.add(item!!)
                }
                notifyDataSetChanged()
            }*/
            firestore?.collection("chatrooms")?.document(chatRoomUid!!)?.collection("comments")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                comments.clear()
                if (querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ChattingDTO.Comment::class.java)
                    comments.add(item!!)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message,parent,false)
            return CustomViewHolder(view)
        }
        public inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view){
            //val textView_message : TextView = messageItem_TextView_message //79와 89줄이 수정전 messageItem_TextView_message이 null이면 안된다는 error가 발생.
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView
            view.messageItem_TextView_message.text = comments[position].message
            //(holder as CustomViewHolder).textView_message.setText(comments[position].message)
        }

    }

    fun checkroom(){
        firestore?.collection("chatrooms")?.whereIn("userSender", listOf(uid,destinationUID))?.get()
                ?.addOnSuccessListener { documents ->
                    for (document in documents) {
                        var item = document.toObject(ChattingDTO::class.java)
                        if(item.users.containsKey(destinationUID)){
                            chatRoomUid = document.id


                            comment_recyclerview.adapter = CommentRecyclerviewAdapter()
                            comment_recyclerview.layoutManager = LinearLayoutManager(this)
                        }
                    }
                }
                ?.addOnFailureListener { }
        /*firestore?.collection("chatrooms")?.whereEqualTo("userSender",uid)?.whereEqualTo("userReceiver",destinationUID)?.get()
            ?.addOnSuccessListener { documents ->
                for (document in documents) {
                    var item = document.toObject(ChattingDTO::class.java)
                    if(item.users.containsKey(destinationUID)){
                        chatRoomUid = document.id


                        comment_recyclerview.adapter = CommentRecyclerviewAdapter()
                        comment_recyclerview.layoutManager = LinearLayoutManager(this)
                    }
                }
            }
            ?.addOnFailureListener { }*/
    }
}