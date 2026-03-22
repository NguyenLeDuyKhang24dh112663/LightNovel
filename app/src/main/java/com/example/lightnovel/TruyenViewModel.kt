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
        _novels.value = fullList
    }

    fun filterByGenre(genreName: String) {
        val username = sharedPref.getString("username", null)
        if (genreName == "Tất cả") {
            refreshFromDb()
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

        val currentList = _novels.value ?: return
        val updatedList = currentList.map {
            if (it.id == productId) {
                val newFavoriteStatus = !it.isFavorite
                db.setFavorite(username, productId, newFavoriteStatus)
                val updatedNovel = it.copy(isFavorite = newFavoriteStatus)
                updatedNovel
            } else it
        }
        _novels.value = updatedList
        
        // Update fullList as well to keep state consistent after search clears
        fullList = fullList.map {
            if (it.id == productId) it.copy(isFavorite = !it.isFavorite) else it
        }

        _selected.value = _selected.value?.let { sel ->
            if (sel.id == productId) sel.copy(isFavorite = !sel.isFavorite) else sel
        }
    }
}
