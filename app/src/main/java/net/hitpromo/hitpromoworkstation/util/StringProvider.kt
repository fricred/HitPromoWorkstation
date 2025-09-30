package net.hitpromo.hitpromoworkstation.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for providing string resources.
 *
 * This abstraction allows data and domain layers to access string resources
 * without direct dependency on Android Context.
 */
interface StringProvider {
    /**
     * Get a string from resources.
     *
     * @param resId The resource ID of the string
     * @return The string value
     */
    fun getString(@StringRes resId: Int): String

    /**
     * Get a formatted string from resources.
     *
     * @param resId The resource ID of the string
     * @param formatArgs The format arguments
     * @return The formatted string value
     */
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

/**
 * Android implementation of StringProvider.
 *
 * Provides access to string resources using Android Context.
 */
@Singleton
class AndroidStringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : StringProvider {

    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}