package com.features.gemini.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.data.model.firebaseData.Chat
import com.data.model.firebaseData.ChatRecord
import com.features.gemini.R
import com.features.gemini.operationState.OperationState
import com.features.gemini.viewmodel.GeminiViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun GeminiHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: GeminiViewModel,
    isLandingPage: Boolean,
    modifyLandingPage: () -> Unit,
    records: List<ChatRecord>,
    onRecordClick: (String) -> Unit,
    currentChatId: String,
    changeCurrentChat: (String) -> Unit,
    chat: List<Chat>,
    onDeleteRecord: (String) -> Unit,
    logOut: () -> Unit = {}
) {


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()



    if (drawerState.isOpen) {
        BackHandler {
            coroutineScope.launch {
                drawerState.close()
            }
        }
    }


        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = RoundedCornerShape(
                        topEnd = 16.dp,
                        bottomEnd = 16.dp
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                        modifier = modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                            .fillMaxWidth()

                    ) {
                        Text(
                            text = "Chats",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            textAlign = TextAlign.Justify,


                            )
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (records.isNotEmpty()) {
                            LazyColumn {
                                items(items = records, key = {
                                    it.chatId
                                }) { record ->
                                    NavigationDrawerItem(
                                        label = {
                                            Text(
                                                text = record.chatId,
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                minLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        selected = record.chatId == currentChatId,
                                        onClick = {
                                            onRecordClick(record.chatId)
                                            coroutineScope.launch(Dispatchers.IO) {
                                                drawerState.close()
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.chat),
                                                contentDescription = "Chats"
                                            )
                                        },
                                        badge = {
                                            Box(
                                                modifier = modifier
                                                    .wrapContentHeight()
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        onDeleteRecord(record.chatId)
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Record",
                                                    )
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(
                                            16.dp
                                        )
                                    )
                                    HorizontalDivider(
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.outline
                                    )

                                }


                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.8f)
                            ) {
                                Text(
                                    text = "🚫 No Records",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                )
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp)
                    ) {
                        TextButton(
                            onClick = {
                                      logOut()
                            },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    Alignment.Start
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = modifier


                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.logout),
                                    contentDescription = "Log Out"
                                )
                                Text(
                                    text = "Log Out",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                        Text(
                            text = "Version: 1.0",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Thin,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            },
            drawerState = drawerState,
            gesturesEnabled = false,
            modifier = modifier
                .clickable {
                    coroutineScope.launch {
                        if (drawerState.isOpen)
                            drawerState.close()
                    }
                }
        ) {

            val operationState by viewModel.operationState.collectAsStateWithLifecycle()

            GeminiScaffold(
                viewModel = viewModel,
                isLandingPage = isLandingPage,
                modifyLandingPage = modifyLandingPage,
                showRecords = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                currentChatId = currentChatId,
                chats = chat,
                listenChanges = viewModel::listenChanges,
                changeCurrentChat = changeCurrentChat,
                operationState = operationState,
                errorOperationStateRelax = viewModel::errorOperationStateRelax
            )
        }


}


@Composable
fun GeminiScaffold(
    modifier: Modifier = Modifier,
    viewModel: GeminiViewModel,
    isLandingPage: Boolean,
    modifyLandingPage: () -> Unit,
    showRecords: () -> Unit,
    currentChatId: String,
    changeCurrentChat: (String) -> Unit,
    chats: List<Chat>,
    listenChanges: (String) -> Unit,
    operationState: OperationState,
    errorOperationStateRelax: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    var progressVisibility by remember {
        mutableStateOf(false)
    }

    var alertDialog by remember {
        mutableStateOf(false)
    }


    when (operationState) {
        is OperationState.Idle -> {
            progressVisibility = false
        }

        is OperationState.Performing -> {
            progressVisibility = true
        }

        is OperationState.Done -> {
            listenChanges(currentChatId)
            if (chats.isNotEmpty() && chats.size > 1) {
                LaunchedEffect(key1 = Unit) {
                    lazyListState.scrollToItem(chats.size - 1)
                }
            }
            progressVisibility = false
        }

        is OperationState.Error -> {
            progressVisibility = false
            alertDialog = true
            AnimatedVisibility(visible = alertDialog) {
                ContentSafetyDialog(
                    msg = operationState.error,
                    dismissCallback = {
                        alertDialog = false
                        errorOperationStateRelax()
                    }
                )
            }

        }

    }

    Scaffold(
        topBar = {
            GeminiAppBar(
                showRecords = showRecords
            )
        },
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = progressVisibility,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LinearProgressIndicator(
                        modifier = modifier.fillMaxWidth()
                    )
                }
                GeminiSearchSection(
                    onSearchCallback = {
                        coroutineScope.launch(Dispatchers.IO) {
                            if (isLandingPage) {
                                //If no chat is open and is landing page of the app, then execute this if block
                                val chatId = Calendar.getInstance().time.toString()
                                changeCurrentChat(chatId)
                                modifyLandingPage()
                                viewModel.raiseQuery(chatId = chatId, it)
                            } else {
                                //Else execute this else block, if it's not first time or landing page of the app.
                                viewModel.raiseQuery(chatId = currentChatId, it)
                            }
                        }
                    },
                    enabled = { viewModel.enabled.value }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(12.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp
                ),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add ,
                    contentDescription = "Add a new chat",
                    modifier = modifier
                        .size(24.dp)
                )
            }

        }
    ) {

        Column(
            modifier = modifier
                .padding(it)
        ) {
            AnimatedVisibility(
                visible = isLandingPage,
                enter = fadeIn() + slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut() + slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                LazyColumn {
                    item {
                        GeminiMidSection()
                        GeminiRandomQuestions()
                    }
                }
            }

            AnimatedVisibility(
                visible = !isLandingPage,
                enter = fadeIn() + slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut() + slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                ChatScreen(
                    chat = chats,
                    deleteChat = { id ->
                        viewModel.deleteChat(currentChatId, id)
                    },
                    lazyListState = lazyListState
                )
            }

            AnimatedVisibility(
                visible = !isLandingPage && chats.isEmpty() && currentChatId.isNotEmpty() && operationState is OperationState.Idle,
                enter = fadeIn() + slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut() + slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
               Column(
                   verticalArrangement = Arrangement.Center,
                   horizontalAlignment = Alignment.CenterHorizontally,
                   modifier = modifier
                       .fillMaxSize()
               ) {
                   Text(
                       text = "Hello there,\n Ask me some Questions \n✍🏻✍🏻✍🏻",
                       fontWeight = FontWeight.Normal,
                       fontFamily = FontFamily.Serif,
                       fontSize = 32.sp,
                       textAlign = TextAlign.Center
                   )

               }
            }
        }
    }

}

