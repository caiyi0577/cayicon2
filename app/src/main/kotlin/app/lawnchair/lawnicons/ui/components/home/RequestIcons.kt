package app.lawnchair.lawnicons.ui.components.home


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.ui.components.ExternalLinkRow
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestIcons(
    requestedIcons: List<IconRequest>,
    iconCount: Int
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Box(
        modifier = Modifier
            .zIndex(1f)
            .padding(top = 96.dp)
    ) {
        SimpleListRow(
            label = "You have $iconCount apps without icons.",
            description = "Tap for more information",
            background = true,
            onClick = {
                showBottomSheet = true
            }
        )
        if (showBottomSheet) {
            BottomSheet(
                requestedIcons = requestedIcons,
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    requestedIcons: List<IconRequest>
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        LazyColumn {
            item {
                ExternalLinkRow(
                    name = "Open icon request form",
                    url = "https://forms.gle/xt7sJhgWEasuo9TR9"
                )
            }
            item {
                SimpleListRow(
                    label = "Copy items",
                    modifier = Modifier.clickable {
                        clipboardManager.setText(
                            AnnotatedString(requestedIcons.joinToString("\n") { it.component })
                        )
                    },
                )
            }
            stickyHeader {
                Text("Unthemed icons")
            }
            items(requestedIcons) {
                SimpleListRow(
                    label = it.name,
                    description = it.component
                )
            }
        }
    }
}

@PreviewLawnicons
@Composable
fun RequestIconsPreview() {
    LawniconsTheme {
        RequestIcons(
            requestedIcons = listOf(
                IconRequest(
                    name = "Example",
                    component = "example.component/example.activity",
                    id = 0
                ),
                IconRequest(
                    name = "Example 2",
                    component = "example.componenttwo/example.activity",
                    id = 1
                )
            ),
            iconCount = 2
        )
    }
}
