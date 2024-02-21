package com.example.musicstream.models
// in this data class we have created model .
data class CategoryModel(
    val name:String,
    val coverUrl:String,
    var songs :List<String>   // list of String
)
{
    constructor():this ("","", listOf())
}
