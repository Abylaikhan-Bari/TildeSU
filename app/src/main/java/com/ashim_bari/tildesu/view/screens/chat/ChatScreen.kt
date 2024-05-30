package com.ashim_bari.tildesu.view.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.model.userChat.userChat
import com.ashim_bari.tildesu.viewmodel.chat.ChatViewModel
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController, chatViewModel: ChatViewModel = hiltViewModel()) {
    val chats by chatViewModel.chats.observeAsState(emptyList())
    var message by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chat",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
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
                items(chats) { chat ->
                    if (chat.senderId == "user1") { // Replace with actual user ID
                        UserChatItem(chat.senderEmail, chat.message)
                    } else {
                        AdminChatItem(chat.senderEmail, chat.message)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f),
                    value = message,
                    onValueChange = {
                        message = it
                    },
                    placeholder = {
                        Text(text = "Type a message")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        chatViewModel.sendMessage(
                            userChat(
                                senderId = "user1", // Replace with actual user ID
                                senderEmail = "user1@example.com", // Replace with actual user email
                                receiverId = "admin", // Replace with actual receiver ID
                                message = message,
                                timestamp = Timestamp.now()
                            )
                        )
                        message = ""
                    })
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            chatViewModel.sendMessage(
                                userChat(
                                    senderId = "user1", // Replace with actual user ID
                                    senderEmail = "user1@example.com", // Replace with actual user email
                                    receiverId = "admin", // Replace with actual receiver ID
                                    message = message,
                                    timestamp = Timestamp.now()
                                )
                            )
                            message = ""
                        },
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun UserChatItem(email: String, message: String) {
    Column(
        modifier = Modifier
            .padding(start = 100.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "From: $email",
            fontSize = 12.sp,
            color = Color.Gray
        )
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
}

@Composable
fun AdminChatItem(email: String, message: String) {
    Column(
        modifier = Modifier
            .padding(end = 100.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "From: $email",
            fontSize = 12.sp,
            color = Color.Gray
        )
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
}
