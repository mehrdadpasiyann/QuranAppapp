package com.quranapp.android.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quranapp.android.utils.sharedPrefs.SPFavouriteChapters

class FavChaptersViewModel : ViewModel() {
    private val _favChapters = MutableLiveData<List<Int>>()
    val favChapters: LiveData<List<Int>> = _favChapters

    fun refreshFavChapters(ctx: Context) {
        _favChapters.value = SPFavouriteChapters.getFavouriteChapters(ctx)
    }

    fun isAddedToFavorites(ctx: Context, chapterNo: Int): Boolean {
        return SPFavouriteChapters.isAddedToFavorites(ctx, chapterNo)
    }

    fun addToFavourites(ctx: Context, chapterNo: Int) {
        SPFavouriteChapters.addToFavorites(ctx, chapterNo)
        refreshFavChapters(ctx)
    }

    fun removeFromFavourites(ctx: Context, chapterNo: Int) {
        SPFavouriteChapters.removeFromFavorites(ctx, chapterNo)
        refreshFavChapters(ctx)
    }
}