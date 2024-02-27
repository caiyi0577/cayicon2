package app.lawnchair.lawnicons.ui.destinations

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import app.lawnchair.lawnicons.ui.components.home.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.LawniconsSearchBar
import app.lawnchair.lawnicons.ui.components.home.PlaceholderSearchBar
import app.lawnchair.lawnicons.ui.components.home.RequestIcons
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel

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

    Crossfade(
        targetState = iconInfoModel != null,
        label = "",
    ) { targetState ->
        if (targetState) {
            searchedIconInfoModel?.let {
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
                    iconInfoModel = it,
                    onNavigate = onNavigate,
                    isExpandedScreen = isExpandedScreen,
                )
            }
            requestedIcons.let {
                if (it != null) {
                    RequestIcons(
                        requestedIcons = it.requestedIcons,
                        iconCount = it.iconCount
                    )
                } else {
                    Button(onClick = { lawniconsViewModel.getRequestedIcons() }, modifier = Modifier.zIndex(1f).padding(top = 96.dp)) {
                        Text(text = "Get requested icons")
                    }
                }
            }
            iconInfoModel?.let {
                IconPreviewGrid(iconInfo = it.iconInfo, isExpandedScreen = isExpandedScreen)
            }
        } else {
            PlaceholderSearchBar()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@PreviewLawnicons
@Composable
private fun HomePreview() {
    var searchTerm by remember { mutableStateOf(value = "") }
    val iconInfo = SampleData.iconInfoList

    LawniconsTheme {
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
        IconPreviewGrid(iconInfo = iconInfo, isExpandedScreen = false)
    }
}
