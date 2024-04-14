package com.ashim_bari.tildesu.view.screens.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.viewmodel.chat.ChatViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update




@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChatBotScreen(navController: NavHostController) {
    val chatViewModel = viewModel<ChatViewModel>()
    val chatState = chatViewModel.chatState.collectAsState().value

    val uriState = remember { MutableStateFlow<Uri?>(null) }
    val context = LocalContext.current

    // Change from GetContent to PickVisualMedia for consistency with previous code
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uriState.value = it
        }
    }

    val bitmap: Bitmap? = getBitmap(context, uriState.value)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name),
                    color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    // This should only be shown if there is a destination to pop back to
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                itemsIndexed(chatState.chatList) { index, chat ->
                    if (chat.isFromUser) {
                        UserChatItem(
                            prompt = chat.prompt, bitmap = chat.bitmap
                        )
                    } else {
                        ModelChatItem(response = chat.prompt)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    bitmap?.let {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(bottom = 2.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentDescription = "picked image",
                            contentScale = ContentScale.Crop,
                            bitmap = it.asImageBitmap()
                        )
                    }

                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                // Use imagePickerLauncher here
                                imagePickerLauncher.launch("image/*")
                            },
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    modifier = Modifier
                        .weight(1f),
                    value = chatState.prompt,
                    onValueChange = {
                        chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                    },
                    placeholder = {
                        Text(text = stringResource(id = R.string.type_prompt))
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            chatViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap))
                            uriState.update { null }
                        },
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send prompt",
                    tint = MaterialTheme.colorScheme.primary
                )

            }

        }
    }

}

@Composable
fun UserChatItem(prompt: String, bitmap: Bitmap?) {
    Column(
        modifier = Modifier.padding(start = 100.dp, bottom = 16.dp)
    ) {

        bitmap?.let {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(bottom = 2.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                bitmap = it.asImageBitmap()
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            text = prompt,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}

@Composable
fun ModelChatItem(response: String) {
    Column(
        modifier = Modifier.padding(end = 100.dp, bottom = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            text = response,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}

@Composable
fun getBitmap(context: Context, uri: Uri?): Bitmap? {
    uri?.let {
        val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(it)
                .size(Size.ORIGINAL)
                .build()
        ).state

        if (imageState is AsyncImagePainter.State.Success) {
            return imageState.result.drawable.toBitmap()
        }
    }
    return null
}