package com.example.musicstream

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.adapter.CategoryAdapter
import com.example.musicstream.adapter.SectionSongListAdapter
import com.example.musicstream.databinding.ActivityMainBinding
import com.example.musicstream.models.CategoryModel
import com.example.musicstream.models.SongModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var categoryAdapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        installSplashScreen()
//        Thread.sleep(1000)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.parseColor("#010B28")
        getCategories() // we will pass
        setUpSection("section_1",binding.section1MainLayout,binding.section1Title,binding.section1RecyclerView)                 // we will pass
        setUpSection("section_2",binding.section2MainLayout,binding.section2Title,binding.section2RecyclerView)
        setUpSection("section_3",binding.section3MainLayout,binding.section3Title,binding.section3RecyclerView)
        setUpMostlyPlayedSection("mostly_played",binding.mostlyPlayedMainLayout,binding.mostlyPlayedTitle,binding.mostlyPlayedRecyclerView)

        binding.optionBtn.setOnClickListener{
            showPopupMenu()
        }
    }

     fun showPopupMenu() {  // this will show popupmenu when clicked on optionbtn  in menu
                            // we implement many option so that go to res-new-android resfile-option_menu res type menu ....
           val popupMenu = PopupMenu(this,binding.optionBtn)
         val inflator = popupMenu.menuInflater
         inflator.inflate(R.menu.option_menu,popupMenu.menu)
         popupMenu.show()
         popupMenu.setOnMenuItemClickListener {
             when(it.itemId){
                 R.id.logout->{
                     logout()
                 }
             }
             false
         }

    }

     fun logout() {
        FirebaseAuth.getInstance().signOut()
         startActivity(Intent(this,SignInActivity::class.java))
         finish()
    }

    // Categories.............
    fun getCategories() {
        FirebaseFirestore.getInstance().collection("category")
            .get().addOnSuccessListener {
                val categoryList = it.toObjects(CategoryModel::class.java)
                setUpCategoryRecyclerView(categoryList)
            }

    }

    override fun onResume() {
        super.onResume()
        showPlayerView()
    }
    fun showPlayerView(){
        binding.playerViewhome.setOnClickListener{
            startActivity(Intent(this,PlayerActivity::class.java))
          //  finish()
        }
        MyExoPlayer.getCurrentSong()?.let {
            binding.playerViewhome.visibility =View.VISIBLE
           binding.songTitleTextView.text = "Now Playing : " + it.title
            Glide.with(binding.songCoverImageView).load(it.coverUrl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                ).into(binding.songCoverImageView)
        }?:run {
            binding.playerViewhome.visibility = View.GONE
        }
    }




    fun setUpCategoryRecyclerView(categoryList: List<CategoryModel>) {
        categoryAdapter = CategoryAdapter(categoryList)
        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }

    // Sections..............
    // first of all we will get the data from firebase so we have to do ...
    // we will make this dynamic ...and if the data  found in firebase then it will be show here automatically...
    fun setUpSection(id:String,mainLayout:RelativeLayout, titleView:TextView,recyclerView:RecyclerView) {  // give this id to fetch acc to data
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get()
            .addOnSuccessListener {
                // this doc have coverUrl ,name ,songs as same as  category model so we convert that....
                val section = it.toObject(CategoryModel::class.java)
                // and we can assign to the recycler view
                section?.apply {
                    mainLayout.visibility = View.VISIBLE  //section layout will be also dynamic
                    titleView.text = section.name
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                    recyclerView.adapter = SectionSongListAdapter(songs)
                    mainLayout.setOnClickListener {
                        SongsListActivity.category = section
                        startActivity(Intent(this@MainActivity,SongsListActivity::class.java))
                    }
                }
            }
    }
    fun setUpMostlyPlayedSection(id:String,mainLayout:RelativeLayout, titleView:TextView,recyclerView:RecyclerView) {  // give this id to fetch acc to data
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get()
            .addOnSuccessListener {
                // get mostly played songs so we have to do
                FirebaseFirestore.getInstance().collection("songs")
                    .orderBy("count",Query.Direction.DESCENDING)
                    .limit(5)
                    .get().addOnSuccessListener{songListSnapshot->
                        // then we get this song into object so we convert it from below code
                        val songsModelsList = songListSnapshot.toObjects<SongModel>()
                        val songsIdList = songsModelsList.map{
                            it.id
                    }.toList()
                        // this doc have coverUrl ,name ,songs as same as  category model so we convert that....
                        val section = it.toObject(CategoryModel::class.java)
                        // and we can assign to the recycler view
                        section?.apply {
                            section?.songs = songsIdList
                            mainLayout.visibility = View.VISIBLE  //section layout will be also dynamic
                            titleView.text = section.name
                            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                            recyclerView.adapter = SectionSongListAdapter(songs)
                            mainLayout.setOnClickListener {
                                SongsListActivity.category = section
                                startActivity(Intent(this@MainActivity,SongsListActivity::class.java))
                            }
                        }
                    }
            }


                }

}