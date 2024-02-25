package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
