package app.lawnchair.lawnicons.repository

import android.app.Application
import android.util.Log
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.IconInfoAppfilter
import app.lawnchair.lawnicons.model.IconInfoAppfilterModel
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.model.SearchInfo
import app.lawnchair.lawnicons.util.getIconInfoFromAppfilter
import app.lawnchair.lawnicons.util.getIconInfoFromMap
import app.lawnchair.lawnicons.util.getIconInfoPackageList
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IconRepository @Inject constructor(application: Application) {

    private var iconInfo: List<IconInfo>? = null
    private var iconInfoAppfilter: List<IconInfoAppfilter>? = null
    private var iconInfoAppfilterModel = MutableStateFlow<IconInfoAppfilterModel?>(value = null)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var packageList: List<IconInfoAppfilter>? = null

    val requestedIconList = MutableStateFlow<IconRequestModel?>(value = null)
    val iconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)
    val searchedIconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)

    init {
        coroutineScope.launch {
            iconInfo = application.getIconInfoFromMap()
                .associateBy { it.name }.values
                .sortedBy { it.name.lowercase() }
                .also {
                    iconInfoModel.value = IconInfoModel(
                        iconInfo = it.toPersistentList(),
                        iconCount = it.size,
                    )
                    searchedIconInfoModel.value = IconInfoModel(
                        iconInfo = it.toPersistentList(),
                        iconCount = it.size,
                    )
                }

            iconInfoAppfilter = application.getIconInfoFromAppfilter()
                .associateBy { it.name }.values
                .sortedBy { it.name.lowercase() }
                .also {
                    iconInfoAppfilterModel.value = IconInfoAppfilterModel(
                        iconInfo = it.toPersistentList(),
                        iconCount = it.size
                    )
                }

            packageList = application.getIconInfoPackageList()
                .associateBy { it.name }.values
                .sortedBy { it.name.lowercase() }
        }
    }

    suspend fun getRequestedIcons() = withContext(Dispatchers.Default) {
        requestedIconList.value = packageList?.let {packageList ->
            val lawniconsData = iconInfoAppfilterModel.value?.iconInfo?.map {
                IconInfoAppfilter(
                    it.name,
                    "",
                    it.componentName,
                    -1
                )
            }
            val systemData = packageList.map {
                IconInfoAppfilter(
                    it.name,
                    "",
                    it.componentName,
                    -1
                )
            }

            Log.d("LAWNICONS_DEBUG", "${lawniconsData?.size}")

            val temp = lawniconsData?.intersect(systemData.toSet())

            val iconRequest = temp?.map {
                IconRequest(it.name, it.componentName, it.id)
            } ?: listOf()

            IconRequestModel(
                requestedIcons = iconRequest.toImmutableList(),
                iconCount = iconRequest.size,
            )
        }
    }

    suspend fun search(query: String) = withContext(Dispatchers.Default) {
        searchedIconInfoModel.value = iconInfo?.let {
            val filtered = it.mapNotNull { candidate ->
                val indexOfMatch =
                    candidate.name.indexOf(string = query, ignoreCase = true).also { index ->
                        if (index == -1) return@mapNotNull null
                    }
                val matchAtWordStart = indexOfMatch == 0 || candidate.name[indexOfMatch - 1] == ' '
                SearchInfo(
                    iconInfo = candidate,
                    indexOfMatch = indexOfMatch,
                    matchAtWordStart = matchAtWordStart,
                )
            }.sortedWith(
                compareBy(
                    { searchInfo -> !searchInfo.matchAtWordStart },
                    { searchInfo -> searchInfo.indexOfMatch },
                ),
            ).map { searchInfo ->
                searchInfo.iconInfo
            }.toPersistentList()
            IconInfoModel(
                iconCount = it.size,
                iconInfo = filtered,
            )
        }
    }
}
