package com.features.gemini.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.features.gemini.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun GeminiAppBar(
    modifier: Modifier = Modifier,
    showRecords: () -> Unit = {}
) {

    TopAppBar(
        title = {
            Text(
                text = "Gemini",
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
            text = "Bonjour Krishna,\nHow can I help you today ?",
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


@Preview(showSystemUi = true, device = Devices.DESKTOP)
@Composable
fun GeminiSearchSection(
    modifier: Modifier = Modifier,
    onSearchCallback: (String) -> Unit = {},
    enabled: () -> Boolean = { true }
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
        ,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(horizontal = 8.dp)
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
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.add_photo),
//                            contentDescription = "Add Photo",
//                            modifier = modifier
//                                .size(32.dp)
//                        )
//
//                    }
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
                .defaultMinSize()
            ,
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
                   contentDescription = "Warning")
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





