package com.features.ask.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.SecureFlagPolicy
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.features.gemini.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun GeminiAppBar(
    modifier: Modifier = Modifier,
    showRecords: () -> Unit = {},
    askSettings: () -> Unit = {}
) {

    TopAppBar(
        title = {
            Text(
                text = "ASK",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic
            )
        },
        navigationIcon = {

            IconButton(
                onClick = { showRecords() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu Options"
                )

            }

        },
        actions = {
            IconButton(onClick = {
                askSettings()
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )

}


@Preview(showSystemUi = true)
@Composable
fun GeminiMidSection(
    modifier: Modifier = Modifier,
    name: String = ""
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Text(
            text = buildAnnotatedString {

                withStyle(
                    style = SpanStyle(
                        brush = userWelcomeBrush,
                        alpha = 1f
                    )
                ) {
                    append("Hello $name")
                }
                append("\nHow can I help you Today ?")
            },
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            fontStyle = FontStyle.Italic,
            lineHeight = 1.2.em
        )

    }

}

@Preview(showSystemUi = true)
@Composable
fun GeminiRandomQuestions(
    modifier: Modifier = Modifier,
    onClickCallback: (String) -> Unit = {}
) {
    val state = rememberLazyListState()
    LazyRow(
        state = state,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.Start
        ),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(
            items = randomQuestions,
            key = {
                it.id
            }
        ) {

            ElevatedCard(
                onClick = {
                    onClickCallback(it.question)
                },
                modifier = modifier
                    .width(300.dp)
                    .height(176.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = it.question,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = modifier
                        .padding(8.dp),
                    textAlign = TextAlign.Justify
                )

            }

        }


    }

}


@Composable
fun GeminiSearchSection(
    modifier: Modifier = Modifier,
    onSearchCallback: (String) -> Unit = {},
    enabled: () -> Boolean = { true },
    pickImage: () -> Unit = {},
    images: () -> List<Uri> = { listOf() },
    onRemoveImage: (Uri) -> Unit
) {

    var value by remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(color = Color.Transparent)
    ) {
        AnimatedVisibility(
            visible = images().isNotEmpty(),
            enter = slideInVertically(initialOffsetY = {
                -it / 2
            }),
            exit = slideOutVertically(
                targetOffsetY = {
                    it / 2
                }
            )
        ) {
           Box(modifier = modifier
               .background(
                   color = MaterialTheme.colorScheme.surfaceContainer,
                   shape = RoundedCornerShape(16.dp)
               )) {
               ImageRow(
                   imageList = images(),
                   onRemoveImage = onRemoveImage
               )
           }


        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(horizontal = 8.dp)
                .background(color = Color.Transparent)

        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    value = it
                },
                shape = RoundedCornerShape(48.dp),
                placeholder = {
                    Text(
                        text = "Enter Prompt",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Thin,
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic
                    )
                },
                enabled = enabled(),
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.End,
                        modifier = modifier
                            .padding(end = 8.dp)
                            .wrapContentSize()
                    ) {
                        IconButton(onClick = {
                            pickImage()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.add_photo),
                                contentDescription = "Add Photo",
                                modifier = modifier
                                    .size(32.dp)
                            )

                        }
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.photo_camera),
//                            contentDescription = "Take Photo",
//                            modifier = modifier
//                                .size(32.dp)
//                        )
//
//                    }
                        IconButton(
                            onClick = {
                                onSearchCallback(value.trim())
                                value = ""
                                focusManager.clearFocus()

                            },
                            enabled = value.trim().isNotEmpty(),
                            modifier = modifier
                                .border(
                                    width = 1.dp,
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                .align(Alignment.Bottom)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Search",
                                modifier = modifier
                                    .size(48.dp)

                            )

                        }
                    }
                },
                modifier = modifier
                    .weight(0.8f)
                    .defaultMinSize(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Default,
                    autoCorrect = true
                )
            )
        }


    }
}


@Preview(showSystemUi = true)
@Composable
fun ContentSafetyDialog(
    modifier: Modifier = Modifier,
    msg: String = "",
    dismissCallback: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = {
            dismissCallback()
        },
        confirmButton = { /*TODO*/ },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.warning),
                contentDescription = "Warning"
            )
        },
        title = {

            Text(
                text = "Error",
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
            )


        },
        text = {
            Text(
                text = msg,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                maxLines = 4
            )

        },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            securePolicy = SecureFlagPolicy.SecureOn
        )
    )

}

@Preview(showSystemUi = true)
@Composable
fun RecordMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onMenuClick: (String) -> Unit = {}
) {

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            onDismissRequest()
        },
        properties = PopupProperties(),
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(4.dp)
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.name,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    onClick = {
                        onMenuClick(
                            item.name
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            modifier = modifier
                                .padding(horizontal = 2.dp)
                        )
                    }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = modifier
                        .fillMaxWidth()
                )
            }


        }

    }
}

@Composable
fun ChangeTitleDialog(
    modifier: Modifier = Modifier,
    changeTitle: (String) -> Unit = {},
    onDoneCallback: () -> Unit = {},
    focusRequester: FocusRequester = FocusRequester()
) {

    var title by rememberSaveable {
        mutableStateOf("")
    }

    Box(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 100,
                    easing = LinearEasing
                )
            )
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    changeTitle(title)
                    onDoneCallback()
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                autoCorrect = true
            ),
            placeholder = {
                Text(
                    text = "Enter Name"
                )
            },
            singleLine = true,
            modifier = modifier
                .focusRequester(focusRequester)

        )
    }
}


@Composable
fun ImageRow(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    imageList: List<Uri>,
    onRemoveImage: (Uri) -> Unit = {}
) {

    val imageRequest = remember {
        ImageRequest
            .Builder(context)
            .size(240)
            .crossfade(true)
            .crossfade(1000)
    }

    LazyRow(
        modifier =
        modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(
            items = imageList,
            key = {
                it.toString()
            }
        ) { uri: Uri ->
            Box(
                modifier = modifier
                    .wrapContentSize()
            ) {

                AsyncImage(
                    model = imageRequest.data(uri).build(),
                    contentDescription = "Image",
                    clipToBounds = true,
                    onError = {
                        Toast
                            .makeText(context, "Error Loading Image", Toast.LENGTH_SHORT)
                            .show()
                    },
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Delete Image",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = modifier
                        .clickable {
                            onRemoveImage(uri)
                        }
                        .align(alignment = Alignment.TopEnd)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .border(
                            width = 1.dp,
                            shape = CircleShape,
                            color = Color.Black
                        )
                )

            }


        }
    }

}

private val userWelcomeBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF5FC3E4),
        Color(0xFFE55D87),
    )
)



