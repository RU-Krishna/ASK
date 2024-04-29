package com.features.ask.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val name: String = "",
    val icon: ImageVector
)

val menuItems = listOf(
    MenuItem(
        name = "Rename",
        icon = Icons.Default.Edit
    ),
    MenuItem(
        name = "Delete",
        icon = Icons.Default.Delete
    )
)
