package com.skrb7f16.thehelpingspoon.ui.feeds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is feeds Fragment"
    }
    val text: LiveData<String> = _text
}