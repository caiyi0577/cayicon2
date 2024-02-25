package app.lawnchair.lawnicons.util

import android.content.Context
import android.util.Log
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.IconInfoAppfilter
import org.xmlpull.v1.XmlPullParser

fun Context.getIconInfoFromMap(): List<IconInfo> {
    val iconInfo = mutableListOf<IconInfo>()

    try {
        val xmlId = R.xml.grayscale_icon_map
        if (xmlId != 0) {
            val parser = resources.getXml(xmlId)
            val depth = parser.depth
            var type: Int
            while (
                (
                    parser.next()
                        .also { type = it } != XmlPullParser.END_TAG || parser.depth > depth
                    ) &&
                type != XmlPullParser.END_DOCUMENT
            ) {
                if (type != XmlPullParser.START_TAG) continue
                if ("icon" == parser.name) {
                    val pkg = parser.getAttributeValue(null, "package")
                    val iconName = parser.getAttributeValue(null, "name")
                    val iconId = parser.getAttributeResourceValue(null, "drawable", 0)
                    val iconDrawable = resources.getResourceEntryName(iconId)
                    if (iconId != 0 && pkg.isNotEmpty()) {
                        iconInfo += IconInfo(iconName, iconDrawable, pkg, iconId)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return iconInfo
}

fun Context.getIconInfoFromAppfilter(): List<IconInfoAppfilter> {
    val iconInfo = mutableListOf<IconInfoAppfilter>()

    val componentInfoPrefixLength = "ComponentInfo{".length

    try {
        val xmlId = R.xml.appfilter
        if (xmlId != 0) {
            val parser = resources.getXml(xmlId)
            val depth = parser.depth
            var type: Int
            while (
                (
                    parser.next()
                        .also { type = it } != XmlPullParser.END_TAG || parser.depth > depth
                    ) &&
                type != XmlPullParser.END_DOCUMENT
            ) {
                if (type != XmlPullParser.START_TAG) continue
                if ("item" == parser.name) {
                    val component = parser.getAttributeValue(null, "component")
                    val iconName = parser.getAttributeValue(null, "name")
                    val iconId = parser.getAttributeResourceValue(null, "drawable", 0)

                    var actualComponent = ""

                    if (component != "") {
                        val parsedComponent =
                            component.substring(componentInfoPrefixLength, component.length - 1)

                        if (parsedComponent.hasContent() && !parsedComponent.startsWith("/")
                            && !parsedComponent.endsWith("/")) {
                            actualComponent = parsedComponent
                        }
                    }

                    if (actualComponent != "") {
                        iconInfo += IconInfoAppfilter(iconName, "", actualComponent, iconId)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return iconInfo
}

