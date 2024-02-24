package app.lawnchair.lawnicons.model

import kotlinx.collections.immutable.ImmutableList

data class IconInfoModel(
    val iconInfo: ImmutableList<IconInfo>,
    val iconCount: Int,
)

data class IconInfoAppfilterModel(
    val iconInfo: List<IconInfoAppfilter>,
    val iconCount: Int,
)
