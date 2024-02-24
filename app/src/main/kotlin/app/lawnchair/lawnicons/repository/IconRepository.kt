package app.lawnchair.lawnicons.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
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
import app.lawnchair.lawnicons.util.hasContent
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

inline fun <reified MLD> lazyMutableLiveData(): Lazy<MutableLiveData<MLD>> =
    lazy { MutableLiveData<MLD>() }

class IconRepository @Inject constructor(application: Application) {

    private var iconInfo: List<IconInfo>? = null
    private var iconInfoAppfilter: List<IconInfoAppfilter>? = null
    private var iconInfoAppfilterModel = MutableStateFlow<IconInfoAppfilterModel?>(value = null)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var packageList = listOf<IconInfoAppfilter>()

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

            packageList = application.getIconInfoPackageList().also {
                packageList.sortedWith(InstalledAppsComparator())
            }
        }
    }

    fun getRequestedIcons() {
        var iconRequest: List<IconRequest>

        val lawniconsData = iconInfoAppfilterModel.value?.iconInfo
        val systemData = packageList

        val temp = lawniconsData?.intersect(systemData.toSet())

        if (temp != null) {
            iconRequest = temp.map {
                IconRequest(it.name, it.componentName, it.id)
            }
        } else {
            iconRequest = listOf()
        }

        requestedIconList.value = IconRequestModel(
            requestedIcons = iconRequest,
            iconCount = iconRequest.size,
        )
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

class InstalledAppsComparator(): Comparator<IconInfoAppfilter> {
    override fun compare(a: IconInfoAppfilter, b: IconInfoAppfilter): Int {
        try {
            val sa = a.name
            val sb = b.name

            if (!sa.hasContent() && !sb.hasContent()) return 0
            if (!sa.hasContent()) return -1
            if (!sb.hasContent()) return 1
            return sa.compareTo(sb)

        }
        catch (e: Exception ) {
            return 0
        }
    }
}
