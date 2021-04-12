package com.example.trash.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.trash.R
import kotlinx.android.synthetic.main.activity_chatting.*
import com.example.trash.navigation.model.ChattingDTO
import com.example.trash.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

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
                firestore?.collection("chatrooms")?.document(chatRoomUid!!)?.collection("comments")?.document()?.set(comment)
            }


        }
        checkroom()
    }

    fun checkroom(){
        firestore?.collection("chatrooms")?.whereEqualTo("userSender",uid)?.whereEqualTo("userReceiver",destinationUID)?.get()
            ?.addOnSuccessListener { documents ->
                for (document in documents) {
                    var item = document.toObject(ChattingDTO::class.java)
                    if(item.users.containsKey(destinationUID)){
                        chatRoomUid = document.id
                    }
                }
            }
            ?.addOnFailureListener { }
    }
}