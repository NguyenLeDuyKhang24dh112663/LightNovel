package com.example.lightnovel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TruyenViewModel(application: Application) : AndroidViewModel(application) {
    private val db = databaseHelper(application)
    private val _novels = MutableLiveData<List<Truyen>>()
    val products: LiveData<List<Truyen>> = _novels

    private val _allNovels = MutableLiveData<List<Truyen>>()
    val allNovels: LiveData<List<Truyen>> = _allNovels

    private val _selected = MutableLiveData<Truyen?>()
    val selected: LiveData<Truyen?> = _selected

    private val sharedPref = application.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    
    private var fullList: List<Truyen> = emptyList()

    init {
        refreshFromDb()
    }

    fun refreshFromDb() {
        val username = sharedPref.getString("username", null)
        fullList = db.getAllNovels(username)
        _allNovels.value = fullList
        _novels.value = fullList
    }

    fun filterByGenre(genreName: String) {
        val username = sharedPref.getString("username", null)
        if (genreName == "Tất cả") {
            _novels.value = fullList
        } else {
            val filtered = db.getNovelsByGenre(genreName, username)
            _novels.value = filtered
        }
    }

    fun getAllGenres(): List<String> {
        return listOf("Tất cả") + db.getAllGenres()
    }

    fun search(query: String) {
        if (query.isEmpty()) {
            _novels.value = fullList
        } else {
            val filtered = fullList.filter {
                it.title.contains(query, ignoreCase = true) || 
                it.author.contains(query, ignoreCase = true)
            }
            _novels.value = filtered
        }
    }

    fun setProducts(list: List<Truyen>) {
        _novels.value = list
    }

    fun select(novel: Truyen) {
        _selected.value = novel
    }

    fun toggleFavorite(productId: Int) {
        val username = sharedPref.getString("username", null)
        if (username == null) return

        val updateListFunc = { list: List<Truyen> ->
            list.map {
                if (it.id == productId) {
                    val newFavoriteStatus = !it.isFavorite
                    // Note: This call to db.setFavorite happens multiple times if we're not careful, 
                    // but here it's inside a map which is applied to different lists.
                    // Better to do it once outside.
                    it.copy(isFavorite = newFavoriteStatus)
                } else it
            }
        }

        // Perform DB update once
        val currentNovel = fullList.find { it.id == productId }
        currentNovel?.let {
            db.setFavorite(username, productId, !it.isFavorite)
        }

        fullList = updateListFunc(fullList)
        _allNovels.value = fullList
        
        _novels.value = _novels.value?.let { updateListFunc(it) }

        _selected.value = _selected.value?.let { sel ->
            if (sel.id == productId) sel.copy(isFavorite = !sel.isFavorite) else sel
        }
    }
}
