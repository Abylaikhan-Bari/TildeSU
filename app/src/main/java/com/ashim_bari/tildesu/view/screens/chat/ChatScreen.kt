package com.ashim_bari.tildesu.view.screens.chat

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.userChat.userChat
import com.ashim_bari.tildesu.viewmodel.chat.ChatViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController, chatViewModel: ChatViewModel = hiltViewModel()) {
    val chats by chatViewModel.chats.observeAsState(emptyList())
    val currentUserId = chatViewModel.currentUserId
    val currentUserEmail = chatViewModel.currentUserEmail
    var message by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val uriState = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarMessage = stringResource(R.string.please_enter_message)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uriState.value = it
        }
    }

    var bitmapState by remember { mutableStateOf<Bitmap?>(null) }

    if (uriState.value != null) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(uriState.value)
                .size(Size.ORIGINAL)
                .build()
        )
        if (painter.state is AsyncImagePainter.State.Success) {
            bitmapState = (painter.state as AsyncImagePainter.State.Success).result.drawable?.toBitmap()
        }
    }

    LaunchedEffect(Unit) {
        chatViewModel.loadChats()
    }

    LaunchedEffect(chats.size) {
        if (chats.isNotEmpty()) {
            listState.animateScrollToItem(chats.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.chat),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                items(chats) { chat ->
                    if (chat.senderId == currentUserId) {
                        UserChatItem(chat.senderEmail, chat.message, chat.imageUrl)
                    } else {
                        AdminChatItem(chat.senderEmail, chat.message, chat.imageUrl)
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
                    bitmapState?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "picked image",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(bottom = 2.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                imagePickerLauncher.launch("image/*")
                            }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text(text = stringResource(R.string.write_message)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        coroutineScope.launch {
                            if (message.isEmpty() && bitmapState == null) {
                                snackbarHostState.showSnackbar("Please enter a message or select an image")
                            } else {
                                chatViewModel.sendMessage(
                                    userChat(
                                        senderId = currentUserId,
                                        senderEmail = currentUserEmail,
                                        receiverId = "admin",
                                        message = message,
                                        timestamp = Timestamp.now()
                                    )
                                )
                                message = ""
                                bitmapState?.let {
                                    chatViewModel.sendImageMessage(it)
                                    bitmapState = null
                                    uriState.value = null
                                }
                            }
                        }
                    })
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            coroutineScope.launch {
                                if (message.isEmpty() && bitmapState == null) {

                                    snackbarHostState.showSnackbar(snackbarMessage)
                                } else {
                                    chatViewModel.sendMessage(
                                        userChat(
                                            senderId = currentUserId,
                                            senderEmail = currentUserEmail,
                                            receiverId = "admin",
                                            message = message,
                                            timestamp = Timestamp.now()
                                        )
                                    )
                                    message = ""
                                    bitmapState?.let {
                                        chatViewModel.sendImageMessage(it)
                                        bitmapState = null
                                        uriState.value = null
                                    }
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun UserChatItem(email: String, message: String, imageUrl: String?) {
    Column(
        modifier = Modifier
            .padding(start = 100.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = email,
            fontSize = 12.sp,
            color = Color.Gray
        )
        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        imageUrl?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(bottom = 2.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun AdminChatItem(email: String, message: String, imageUrl: String?) {
    Column(
        modifier = Modifier
            .padding(end = 100.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = email,
            fontSize = 12.sp,
            color = Color.Gray
        )
        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(16.dp),
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        imageUrl?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(bottom = 2.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
