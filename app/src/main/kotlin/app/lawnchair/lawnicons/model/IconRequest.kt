package app.lawnchair.lawnicons.model

data class IconRequest(
    val name: String,
    val component: String,
    val id: Int,
)

data class IconRequestModel(
    val requestedIcons: List<IconRequest>,
    val iconCount: Int,
)
