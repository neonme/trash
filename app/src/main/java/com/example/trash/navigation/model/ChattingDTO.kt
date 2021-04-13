package com.example.trash.navigation.model

data class ChattingDTO (var userSender : String? = null,
                        var userReceiver : String? = null,
                        var users : MutableMap<String,Boolean> = HashMap(),
                        var comments : Map<String,Comment> = HashMap()){
    data class Comment(
        var userId : String? = null,
        var message : String? = null,
        var timestamp : Long? = null)
}