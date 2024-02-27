package app.lawnchair.lawnicons.ui.destinations

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Text
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.home.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.LawniconsSearchBar
import app.lawnchair.lawnicons.ui.components.home.PlaceholderSearchBar
import app.lawnchair.lawnicons.ui.components.home.RequestIcons
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(
    onNavigate: (String) -> Unit,
    isExpandedScreen: Boolean,
    lawniconsViewModel: LawniconsViewModel = hiltViewModel(),
) {
    val iconInfoModel by lawniconsViewModel.iconInfoModel.collectAsState()
    val searchedIconInfoModel by lawniconsViewModel.searchedIconInfoModel.collectAsState()
    val requestedIcons by lawniconsViewModel.requestedIcons.collectAsState()
    var searchTerm by rememberSaveable { mutableStateOf(value = "") }
    var showRequestedIcons by rememberSaveable { mutableStateOf(false) }

    Crossfade(
        targetState = iconInfoModel != null,
        label = "",
    ) { targetState ->
        if (targetState) {
            searchedIconInfoModel?.let { model ->
                Scaffold(
                    topBar = {
                        LawniconsSearchBar(
                            query = searchTerm,
                            isQueryEmpty = searchTerm == "",
                            onClearAndBackClick = {
                                searchTerm = ""
                                lawniconsViewModel.searchIcons("")
                            },
                            onQueryChange = { newValue ->
                                searchTerm = newValue
                                lawniconsViewModel.searchIcons(newValue)
                            },
                            iconInfoModel = model,
                            onNavigate = onNavigate,
                            isExpandedScreen = isExpandedScreen,
                        )
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            onClick = {
                                lawniconsViewModel.getRequestedIcons().let { showRequestedIcons = true }
                            },
                            icon = { Icon( painterResource(R.drawable.upload), "") },
                            text = { Text(text = "Request icons") }
                        )
                    },
                ) {
                    requestedIcons?.let {
                        RequestIcons(
                            requestedIcons = it.requestedIcons,
                            iconCount = it.iconCount,
                            showRequestedIcons = showRequestedIcons,
                            onDismissRequest = { showRequestedIcons = false }
                        )
                    }
                    iconInfoModel?.let {
                        IconPreviewGrid(
                            iconInfo = it.iconInfo,
                            isExpandedScreen = isExpandedScreen
                        )
                    }
                }
            }

        } else {
            PlaceholderSearchBar()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@PreviewLawnicons
@Composable
private fun HomePreview() {
    var searchTerm by remember { mutableStateOf(value = "") }
    val iconInfo = SampleData.iconInfoList

    LawniconsTheme {
        Scaffold(
            topBar = {
                LawniconsSearchBar(
                    query = searchTerm,
                    isQueryEmpty = searchTerm == "",
                    onClearAndBackClick = {
                        searchTerm = ""
                    },
                    onQueryChange = { newValue ->
                        searchTerm = newValue
                        // No actual searching, this is just a preview
                    },
                    iconCount = 3,
                    iconInfo = iconInfo,
                    onNavigate = {},
                    isExpandedScreen = false,
                )
            } ) {
            IconPreviewGrid(iconInfo = iconInfo, false)
        }
    }
}
