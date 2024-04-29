package com.features.ask.ui

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.data.model.firebaseData.Chat

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chat: List<Chat>,
    deleteChat: (String) -> Unit = { },
    lazyListState: LazyListState
) {

    LaunchedEffect(key1 = chat) {
        if (chat.isNotEmpty()) {
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
    deleteChat: () -> Unit = {},
    context: Context = LocalContext.current
) {

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                4.dp,
                alignment = Alignment.CenterVertically
            ),
            modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                        .align(Alignment.CenterStart),
                    style = TextStyle(
                        brush = brush
                    )

                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.End),
                    verticalAlignment = Alignment.Top,
                    modifier = modifier
                        .align(Alignment.CenterEnd)
                )
                {
                    IconButton(
                        onClick = {
                                  shareThisChat(
                                      userPrompt,
                                      modelResponse,
                                      context
                                  )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share this Chat"
                        )

                    }
                    IconButton(
                        onClick = {
                            deleteChat()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Chat"
                        )

                    }
                }
            }
            SelectionContainer(
                modifier = modifier
            ) {
                Text(
                    text = userPrompt,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = Italic
                )
            }
            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "Model:",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = Italic,
                style = TextStyle(
                    brush = brush
                )
            )
            SelectionContainer {
                Text(
                    text = modelResponse
                        .replace("**", "\"")
                        .replace("*", "\n•"),
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = Italic
                )
            }
        }
    }
}

internal val brush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF5FC3E4),
        Color(0xFFE55D87),
    )
)


private fun shareThisChat(
    userPrompt: String,
    modelReply: String,
    context: Context
)  {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, toSharableChat(userPrompt, modelReply))
    }
    context.startActivity(shareIntent)
}

private fun toSharableChat(userPrompt: String, modelReply: String): String =
    "\n\"✍🏻User\": \n$userPrompt\n\n\"💻Model\": \n$modelReply"





