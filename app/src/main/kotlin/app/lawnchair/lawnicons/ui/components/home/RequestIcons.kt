package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestIcons(
    requestedIcons: ImmutableList<IconRequest>,
    iconCount: Int,
    showRequestedIcons: Boolean = false,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    if (showRequestedIcons) {
        BottomSheet(
            iconCount = iconCount,
            requestedIcons = requestedIcons,
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun BottomSheet(
    iconCount: Int,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    requestedIcons: ImmutableList<IconRequest>,
) {
    val requests = requestedIcons.joinToString("%0A") { it.component }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        LazyColumn {
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        UnthemedIconsInfo(iconCount = iconCount, request = buildForm(requests))
                        Text(
                            text = "Apps with no icons",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 32.dp, top = 12.dp),
                        )
                    }
                }
            }
            itemsIndexed(requestedIcons) { index, it ->
                SimpleListRow(
                    label = it.name,
                    description = it.component,
                    background = true,
                    first = index == 0,
                    last = index == requestedIcons.lastIndex,
                    divider = index != requestedIcons.lastIndex,
                )
            }
        }
    }
}

private fun buildForm(string: String): String {
    return "https://docs.google.com/forms/d/e/1FAIpQLSe8ItNYse9f4z2aT1QgXkKeueVTucRdUYNhUpys5ShHPyRijg/viewform?entry.1759726669=$string"
}

@PreviewLawnicons
@Composable
private fun RequestIconsPreview() {
    LawniconsTheme {
        RequestIcons(
            requestedIcons = listOf(
                IconRequest(
                    name = "Example",
                    component = "example.component/example.activity",
                ),
                IconRequest(
                    name = "Example 2",
                    component = "example.componenttwo/example.activity",
                ),
            ).toImmutableList(),
            iconCount = 2,
            showRequestedIcons = true,
            onDismissRequest = {},
        )
    }
}
