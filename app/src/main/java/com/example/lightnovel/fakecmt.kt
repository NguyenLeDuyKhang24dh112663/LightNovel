package com.example.lightnovel


object fakecmt {
    val comments = mutableListOf<Comment>()
    init {
        comments.add(Comment("Bond Phạm", "main gà vl", "21/3/2026"))
        comments.add(Comment("Chí Kiệt", "Ừ", "21/3/2026"))
        comments.add(Comment("ThinnhNgo", "Cũng được mà", "21/3/2026"))
    }
}

data class Comment(val name: String, val content: String, val day: String)