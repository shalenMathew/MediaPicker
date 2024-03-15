package com.example.mediapicker

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ImageViewModel:ViewModel() {

   val imageUri = mutableStateOf("")

   var videoUri = ""
   var isVideo = false // this is to check if the selected medium is a view or not
   var isVideoPicked = mutableStateOf(false)

}