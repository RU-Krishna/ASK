package com.features.ask.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.data.model.firebaseData.Chat
import com.data.model.firebaseData.ChatRecord
import com.features.ask.operationState.OperationState
import com.features.ask.viewmodel.AskViewModel
import com.features.gemini.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar


//Main Home Screen composable for all the UI elements, screens in the app...
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GeminiHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AskViewModel,
    isLandingPage: Boolean,
    modifyLandingPage: () -> Unit,
    records: List<ChatRecord>,
    onRecordClick: (String) -> Unit,
    currentChatId: String,
    changeCurrentChat: (String) -> Unit,
    chat: List<Chat>,
    onDeleteRecord: (String) -> Unit,
    logOut: () -> Unit = {},
    addNewChat: () -> Unit = {},
    answerThis: (String) -> Unit = {},
    renameTitle: (String, String) -> Unit = {_, _ -> },
    userName: String = "",
    askSettings:() -> Unit = {}
) {


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()


    var onLongClickRenameId by remember {
        mutableStateOf("")
    }



    //How system back handler works when navDrawer is open.
    if (drawerState.isOpen) {
        BackHandler {
            coroutineScope.launch {
                onLongClickRenameId = ""
                drawerState.close()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
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
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            ) {
                                                AnimatedVisibility(
                                                    visible = onLongClickRenameId != record.chatId,
                                                    enter = fadeIn(
                                                        tween(
                                                            durationMillis = 200,
                                                            easing = FastOutLinearInEasing
                                                        )
                                                    ),
                                                    exit = fadeOut(
                                                        tween(
                                                            durationMillis = 200,
                                                            easing = FastOutLinearInEasing
                                                        )
                                                    )
                                                ) {
                                                    Text(
                                                        text = record.title.ifEmpty { record.chatId },
                                                        fontFamily = FontFamily.Serif,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 16.sp,
                                                        maxLines = 1,
                                                        minLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = modifier
                                                            .combinedClickable(
                                                                enabled = true,
                                                                onLongClickLabel = "Rename the Title",
                                                                onLongClick = {
                                                                    onLongClickRenameId =
                                                                        record.chatId
                                                                }) {
                                                                onLongClickRenameId = ""
                                                            }
                                                            .clip(RoundedCornerShape(16.dp))
                                                    )
                                                }
                                                AnimatedVisibility(
                                                    visible = onLongClickRenameId == record.chatId,
                                                    enter = fadeIn(
                                                        tween(
                                                            durationMillis = 200,
                                                            easing = FastOutLinearInEasing
                                                        )
                                                    ),
                                                    exit = fadeOut(
                                                        tween(
                                                            durationMillis = 200,
                                                            easing = FastOutLinearInEasing
                                                        )
                                                    )
                                                ) {
                                                    ChangeTitleDialog(
                                                        modifier = modifier
                                                            .wrapContentSize(),
                                                        changeTitle = { name ->
                                                            renameTitle(
                                                                record.chatId,
                                                                name
                                                            )

                                                        },
                                                        onDoneCallback = {
                                                            onLongClickRenameId = ""
                                                        }
                                                    )
                                                }
                                            }
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
                                            AnimatedVisibility(
                                                visible = onLongClickRenameId != record.chatId,
                                                enter = fadeIn(
                                                    tween(
                                                        durationMillis = 200,
                                                        easing = FastOutLinearInEasing
                                                    )
                                                ),
                                                exit = fadeOut(
                                                    tween(
                                                        durationMillis = 200,
                                                        easing = FastOutLinearInEasing
                                                    )
                                                )
                                            ) {
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
                                            }
                                        },
                                        shape = RoundedCornerShape(
                                            16.dp
                                        )
                                    )
                                    HorizontalDivider(
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = modifier.padding(vertical = 2.dp)
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
            scrimColor = MaterialTheme.colorScheme.scrim,
            modifier = modifier
                .clickable {
                    coroutineScope.launch {
                        if (drawerState.isOpen)
                            onLongClickRenameId = ""
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
                errorOperationStateRelax = viewModel::errorOperationStateRelax,
                addNewChat = addNewChat,
                answerThis = answerThis,
                userName = userName,
                askSettings = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    askSettings()
                }
            )
        }
    }


}


@Composable
fun GeminiScaffold(
    modifier: Modifier = Modifier,
    viewModel: AskViewModel,
    isLandingPage: Boolean,
    modifyLandingPage: () -> Unit,
    showRecords: () -> Unit,
    currentChatId: String,
    changeCurrentChat: (String) -> Unit,
    chats: List<Chat>,
    listenChanges: (String) -> Unit,
    operationState: OperationState,
    errorOperationStateRelax: () -> Unit,
    addNewChat: () -> Unit,
    answerThis: (String) -> Unit = {},
    userName: String,
    askSettings: () -> Unit,
    context: Context = LocalContext.current
) {

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    var progressVisibility by remember {
        mutableStateOf(false)
    }

    var alertDialog by remember {
        mutableStateOf(false)
    }

    val askImage = remember {
         mutableStateListOf<Uri>()
    }

    val askImageBitmap = remember {
        mutableListOf<Bitmap>()
    }

    var cameraPermission by remember {
        mutableStateOf(
            PermissionChecker.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )
            == PermissionChecker.PERMISSION_GRANTED
        )
    }


    var uri: Lazy<Uri> = context.createUri()



    val askImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) {
        askImage.addAll(it)
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            it.forEach { uri ->
                askImageBitmap
                    .add(
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    )
            }
        }

    }

    val captureImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        askImage.add(uri.value)
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            askImageBitmap
                .add(
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri.value)
                )
        }
    }


    val captureImagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermission = isGranted
        if(!isGranted)
            Toast.makeText(
                context,
                "Camera Permission Denied🚫",
                LENGTH_SHORT
            )
                .show()
    }




    when (operationState) {
        is OperationState.Idle -> {
            progressVisibility = false
        }

        is OperationState.Performing -> {
            progressVisibility = true
            askImage.clear()
        }

        is OperationState.Done -> {
            listenChanges(currentChatId)
            if (chats.isNotEmpty() && chats.size > 1) {
                LaunchedEffect(key1 = Unit) {
                    lazyListState.scrollToItem(chats.size - 1)
                }
            }
            progressVisibility = false
            askImageBitmap.clear()
        }

        is OperationState.Error -> {
            progressVisibility = false
            alertDialog = true
            askImageBitmap.clear()
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
                showRecords = showRecords,
                askSettings = askSettings
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
                            Log.d("Images", "Images: $askImage")

                                if (isLandingPage) {
                                    //If no chat is open and is landing page of the app, then execute this if block
                                    val chatId = Calendar.getInstance().time.toString()
                                    changeCurrentChat(chatId)
                                    modifyLandingPage()
                                    if(askImage.isEmpty())
                                        viewModel.raiseQuery(chatId = chatId, it)
                                    else {
                                        viewModel.raiseMultiModalQuery(chatId = chatId, it, askImageBitmap)

                                    }
                                } else {
                                    //Else execute this else block, if it's not first time or landing page of the app.
                                    if(askImage.isEmpty())
                                        viewModel.raiseQuery(chatId = currentChatId, it)
                                    else {
                                        viewModel.raiseMultiModalQuery(chatId = currentChatId, it, askImageBitmap)

                                    }

                                }
                            }
                    },
                    enabled = { viewModel.enabled.value },
                    images = { askImage },
                    pickImage = {
                        coroutineScope.launch(
                            Dispatchers.IO
                        ) {
                            askImageLauncher
                                .launch(
                                    PickVisualMediaRequest(
                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                        }
                    },
                    onRemoveImage = { removedImage ->
                        askImage.remove(removedImage)
                    },
                    captureImage = {
                        coroutineScope.launch(
                            Dispatchers.IO
                        ) {
                            if(cameraPermission) {
                                uri = context.createUri()
                                captureImageLauncher.launch(uri.value)
                            }
                            else
                                captureImagePermissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )

                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                          addNewChat()
                          },
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
                        GeminiMidSection(
                            name = userName.replace("@", "")
                        )
                        GeminiRandomQuestions(
                            onClickCallback = { question ->
                                    answerThis(question)
                            }
                        )
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
                enter = fadeIn() + slideInVertically(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut() + slideOutVertically(
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

fun Context.createImageFile(): File {
    val timeStamp = Calendar.getInstance().time.toString()
    val image = File.createTempFile(
        timeStamp, /* prefix */
        ".jpg", /* suffix */
        cacheDir      /* directory */
    )
    return image
}

fun Context.createUri(): Lazy<Uri> = lazy {
    FileProvider.getUriForFile(
        this,
        ContextCompat.getString(this, R.string.fileprovider),
        createImageFile()
    )
}
