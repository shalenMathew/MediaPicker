package com.example.mediapicker

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.mediapicker.ui.theme.MediaPickerTheme

class MainActivity : ComponentActivity() {
    
  private val imageViewModel:ImageViewModel by viewModels()


    // this is a new way to pick images and videos in gallery as OnActivityResult is deprecated
    private val pickMedia = registerForActivityResult<PickVisualMediaRequest,Uri>(ActivityResultContracts.PickVisualMedia())
    {
       if(it!=null){

           if(imageViewModel.isVideo){
               imageViewModel.videoUri=it.toString()
               imageViewModel.isVideoPicked.value=true
           }else{
               imageViewModel.imageUri.value = it.toString()
           }

       }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaPickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "HomeScreen" ){
                        composable("HomeScreen"){HomeScreen()}
                        composable("VideoScreen"){VideoScreen(imageViewModel)}
                    }


                    if (imageViewModel.isVideoPicked.value){
                        // if video is picked navigate to exo player
                        navController.navigate("VideoScreen")
                        imageViewModel.isVideoPicked.value=false
                    }
                }
            }
        }
    }


    @Composable
    private fun HomeScreen() {
        Column(modifier = Modifier.fillMaxSize(), 
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))
            DisplayImage()
            Spacer(modifier = Modifier.height(20.dp))
            DisplayButton()
        }
    }

    @Composable
    fun DisplayImage() {

        val imagePainter:AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageViewModel.imageUri.value)
                .size(Size.ORIGINAL).build()).state

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(20.dp, 0.dp, 20.dp, 0.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)){

            if(imagePainter is AsyncImagePainter.State.Success){
                Image( modifier = Modifier.fillMaxSize(),
                    bitmap = imagePainter.result.drawable.toBitmap().asImageBitmap(),
                    contentDescription ="img",
                    contentScale = ContentScale.Crop )
            }

        }

    }

   @Composable
    fun DisplayButton() {

        Column(modifier = Modifier.wrapContentSize() ) {
            Button(onClick = {
                imageViewModel.isVideo=false
                pickMedia.launch(PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    .build()
                )
            }) {
                Text(text = "Pick Image")
            }
            Spacer(modifier = Modifier.height(5.dp))

            Button(onClick = {
                imageViewModel.isVideo=true
                pickMedia.launch(PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly).build()) }) {
                Text(text = "Pick Videos")
            }
        }
    }


}

