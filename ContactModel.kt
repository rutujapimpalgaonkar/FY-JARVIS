package com.plgpl.jarvis.domain

import android.graphics.Bitmap
import android.net.Uri


class ContactModel {
    var id: String? = null
    var name: String? = null
    var mobileNumber: String? = null
    var photo: Bitmap? = null
    var photoURI: Uri? = null
}