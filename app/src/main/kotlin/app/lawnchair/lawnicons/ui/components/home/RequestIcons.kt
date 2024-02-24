package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.runtime.Composable
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel

@Composable
fun RequestIcons(
    requestedIcons: List<IconRequest>,
    iconCount: Int
) {
    SimpleListRow(
        label = "You have $iconCount requested icons.",
        description = "Icons: $requestedIcons"
    )
}
