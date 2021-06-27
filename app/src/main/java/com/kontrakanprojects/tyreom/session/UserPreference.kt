package com.kontrakanprojects.tyreom.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.kontrakanprojects.tyreom.db.Login
import com.kontrakanprojects.tyreom.db.User

internal class UserPreference(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_FULLNAME = "user_fullname"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_LOGIN = "userlogin"
    }

    fun setUser(values: User) {
        preferences.edit {
            putInt(KEY_USER_ID, values.idUser!!)
            putInt(KEY_USER_ROLE, values.idRole!!)
            putString(KEY_USER_FULLNAME, values.namaUser)
        }
    }

    fun setLogin(values: Login) {
        preferences.edit {
            putBoolean(KEY_LOGIN, values.isRememberLogin)
        }
    }

    fun getUser(): User {
        return User(
            idUser = preferences.getInt(KEY_USER_ID, 0),
            idRole = preferences.getInt(KEY_USER_ROLE, 0),
            namaUser = preferences.getString(KEY_USER_FULLNAME, "")
        )
    }

    fun getLogin(): Login {
        return Login(
            isRememberLogin = preferences.getBoolean(KEY_LOGIN, false)
        )
    }

    fun removeUser() {
        preferences.edit {
            remove(KEY_USER_ID)
            remove(KEY_USER_ROLE)
            remove(KEY_USER_FULLNAME)
        }
    }

    fun removeLogin() {
        preferences.edit {
            remove(KEY_LOGIN)
        }
    }
}