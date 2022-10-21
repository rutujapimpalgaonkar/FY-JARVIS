package com.plgpl.jarvis.helpers

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract


class ContactHelper {
    companion object {
        fun getPhoneNumbers(ctx: Context): HashMap<String, String> {
            val namePhoneMap = HashMap<String, String>()

            val phones: Cursor = ctx.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )!!

            // Loop Through All The Numbers
            while (phones.moveToNext()) {
                val name =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var phoneNumber =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                // Cleanup the phone number
                phoneNumber = phoneNumber.replace("[()\\s-]+".toRegex(), "")

                namePhoneMap.put(name.toLowerCase(), phoneNumber)
            }
            phones.close()
            return namePhoneMap;
        }
    }
}