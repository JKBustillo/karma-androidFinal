package com.jabustillo.karma.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message (val id: Int? = 0, val text: String? = "", val user: String? = "")