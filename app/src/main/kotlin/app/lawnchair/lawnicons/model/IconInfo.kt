package app.lawnchair.lawnicons.model

data class IconInfo(
    val name: String,
    val drawableName: String,
    val packageName: String,
    val id: Int,
)

data class IconInfoAppfilter(
    val name: String,
    val drawableName: String,
    val componentName: String,
    val id: Int,
)
