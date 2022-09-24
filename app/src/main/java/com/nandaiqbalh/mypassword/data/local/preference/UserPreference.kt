package com.nandaiqbalh.mypassword.data.local.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreference(context: Context) {

    // step 1: define share preference
    private val preference: SharedPreferences = context.getSharedPreferences(NAME, MODE)

    companion object {
        const val NAME = "myPassword"
        const val MODE = Context.MODE_PRIVATE
    }

    // step 3: define a variable for setter and getter the preference
    var appKey: String?
        get() = preference.getString(
            PreferenceKey.PREF_USER_APP_KEY.first,
            PreferenceKey.PREF_USER_APP_KEY.second
        )

    set(value) = preference.edit {
        this.putString(PreferenceKey.PREF_USER_APP_KEY.first, value)
    }

}

object PreferenceKey {
    // step 2: set preference key and default value
    val PREF_USER_APP_KEY = Pair("PREF_USER_APP_KEY", null)
}
