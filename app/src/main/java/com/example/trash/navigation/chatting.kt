package com.example.trash.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trash.R
import kotlinx.android.synthetic.main.activity_chatting.*
import com.example.trash.navigation.model.ChattingDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class chatting : AppCompatActivity() {
    var destinationUID : String? = null
    var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)
        firestore = FirebaseFirestore.getInstance()
        destinationUID = intent.getStringExtra("destinationUid")
        messageActivity_button?.setOnClickListener{
            var chatting = ChattingDTO()
            chatting.uid = FirebaseAuth.getInstance().currentUser?.uid
            chatting.destinationUid = destinationUID

            firestore?.collection("chatrooms")?.document()?.set(chatting)


        }
    }
}