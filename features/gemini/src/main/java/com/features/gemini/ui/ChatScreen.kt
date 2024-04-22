package com.features.gemini.ui

import androidx.compose.animation.Animatable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.data.model.firebaseData.Chat
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chat: List<Chat>,
    deleteChat: (String) -> Unit = { },
    lazyListState: LazyListState
) {

    LaunchedEffect(key1 = chat) {
        if(chat.isNotEmpty()) {
            lazyListState.animateScrollToItem(chat.size - 1)
        }
    }

    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(
            items = chat,
            key = {
            it.id
        }) { chat ->

            val animatable = remember {
                Animatable(0.5f)
            }

            LaunchedEffect(key1 = true) {
                animatable.animateTo(
                    1f, tween(
                        300,
                        easing = FastOutSlowInEasing
                    )
                )

            }
            ChatCard(
                modifier = modifier
                    .graphicsLayer(
                        scaleX = animatable.value,
                        scaleY = animatable.value
                    ),
                userPrompt = chat.userPrompt,
                modelResponse = chat.modelReply,
                deleteChat = {
                    deleteChat(chat.id)
                }
            )
        }

    }

}

@Preview(showSystemUi = true)
@Composable
fun ChatCard(
    modifier: Modifier = Modifier,
    userPrompt: String = "",
    modelResponse: String = "",
    deleteChat: ()-> Unit = {}
) {

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .border(
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp),
                width = 1.dp
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterVertically),
            modifier = modifier.
            padding(horizontal = 8.dp, vertical = 4.dp)
        ) {

            Box(
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = "User Prompt:",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = Italic,
                    modifier = modifier
                        .align(Alignment.CenterStart)

                )
                IconButton(
                    onClick = {
                        deleteChat()
                              },
                    modifier = modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                    
                }
            }
            Text(
                text = userPrompt,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = Italic
            )
            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "Model:",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = Italic
            )
            Text(
                text = modelResponse
                    .replace("**","\"")
                    .replace("*", "\n•"),
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = Italic
            )
        }
    }
}


