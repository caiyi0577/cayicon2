package app.lawnchair.lawnicons.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import app.lawnchair.lawnicons.model.IconInfoAppfilter
import app.lawnchair.lawnicons.model.IconRequest
import okhttp3.internal.toImmutableList

fun Context.appIcon(): Bitmap = packageManager.getApplicationIcon(packageName).toBitmap()

fun Context.getPackagesList(): List<ResolveInfo> {
    val packagesList = try {
        packageManager.queryIntentActivitiesCompat(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
            PackageManager.GET_RESOLVED_FILTER
        )
    } catch (e: Exception) {
        listOf()
    }
    return packagesList
}

fun Context.getIconInfoPackageList(): List<IconInfoAppfilter> {
    val resolveInfo = getPackagesList()
    var list: MutableList<IconInfoAppfilter> = mutableListOf()

    for (ri in resolveInfo) {
        val riPkg = ri.activityInfo.packageName
        val component = riPkg + "/" + ri.activityInfo.name

        val name: CharSequence? = try {
            ri.loadLabel(packageManager)
        } catch (e: Exception) {
            riPkg
        }

        list.add(
            IconInfoAppfilter(
                name = name.toString(),
                componentName = component,
                id = 0,
                drawableName = ""
            )
        )
    }

    return list.toImmutableList()
}

fun String?.hasContent(): Boolean = orEmpty().isNotBlank() && orEmpty().isNotEmpty()
