package com.example.lightnovel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TruyenViewModel: ViewModel() {
    private val _novels = MutableLiveData<List<Truyen>>()
    val products: LiveData<List<Truyen>> = _novels

    private val _selected = MutableLiveData<Truyen?>()
    val selected: LiveData<Truyen?> = _selected

    fun setProducts(list: List<Truyen>) { _novels.value = list }
    fun select(novel: Truyen) { _selected.value = novel }
    fun toggleFavorite(productId: Int) {
        _novels.value = _novels.value?.map {
            if (it.id == productId) it.copy(isFavorite = !it.isFavorite) else it
        }

        // also update selected if matches
        _selected.value = _selected.value?.let { sel ->
            if (sel.id == productId) sel.copy(isFavorite = !sel.isFavorite) else sel
        }
    }
}
