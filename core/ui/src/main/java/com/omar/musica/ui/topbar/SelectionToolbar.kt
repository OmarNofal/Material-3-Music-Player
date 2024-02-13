package com.omar.musica.ui.topbar


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omar.musica.ui.menu.MenuActionItem
import com.omar.musica.ui.common.MultiSelectState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopAppBarScaffold(
    modifier: Modifier,
    multiSelectState: MultiSelectState,
    isMultiSelectEnabled: Boolean,
    actionItems: List<MenuActionItem>,
    numberOfVisibleIcons: Int,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    content: @Composable () -> Unit // the TopAppBar which is visible when user is not in selection mode
) {
    AnimatedContent(
        targetState = isMultiSelectEnabled, label = "",
        transitionSpec = {
            if (targetState) {
                scaleIn(initialScale = 0.8f) + fadeIn() togetherWith scaleOut(targetScale = 1.2f) + fadeOut()
            } else {
                scaleIn(initialScale = 1.2f) + fadeIn() togetherWith scaleOut(targetScale = 0.8f) + fadeOut()
            }
        }
    ) {
        if (it)
            SelectionToolbar(
                modifier = modifier,
                numberOfSelected = multiSelectState.selected.size,
                actionItems = actionItems,
                numberOfVisibleIcons = numberOfVisibleIcons,
                onNavigationIconClicked = { multiSelectState.clear() },
                scrollBehavior = scrollBehavior
            )
        else
            content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionToolbar(
    modifier: Modifier = Modifier,
    numberOfSelected: Int,
    actionItems: List<MenuActionItem>,
    numberOfVisibleIcons: Int = 2,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavigationIconClicked: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = "$numberOfSelected selected", fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClicked) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
            }
        },
        actions = {
            val visibleItems = actionItems.take(numberOfVisibleIcons)
            val invisibleItems = actionItems.drop(numberOfVisibleIcons)

            val overflowShown = invisibleItems.isNotEmpty()

            visibleItems.forEach {
                PlainTooltipBox(tooltip = { Text(text = it.title) }) {
                    IconButton(modifier = Modifier.tooltipAnchor(), onClick = it.callback) {
                        Icon(imageVector = it.icon, contentDescription = null)
                    }
                }
            }

            if (overflowShown) {
                OverflowMenu(actionItems = invisibleItems)
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun OverflowMenu(
    actionItems: List<MenuActionItem>,
    showIcons: Boolean = true
) {
    var visible by remember { mutableStateOf(false) }
    Box {

        IconButton(onClick = { visible = !visible }) {
            Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
        }

        DropdownMenu(
            expanded = visible,
            onDismissRequest = { visible = false }
        ) {
            actionItems.forEach {
                DropdownMenuItem(
                    leadingIcon =
                        if (showIcons)
                        { { Icon(imageVector = it.icon, contentDescription = null) } }
                        else {
                          null
                        },
                    text = { Text(text = it.title) },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    onClick = { visible = false; it.callback() }
                )
            }
        }

    }
}