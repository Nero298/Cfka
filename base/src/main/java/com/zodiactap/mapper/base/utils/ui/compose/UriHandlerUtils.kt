package com.zodiactap.mapper.base.utils.ui.compose

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.UriHandler
import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.base.utils.ui.str

fun UriHandler.openUriSafe(ctx: Context, uri: String) {
    try {
        openUri(uri)
    } catch (e: IllegalArgumentException) {
        Toast.makeText(ctx, ctx.str(R.string.error_no_app_to_open_url), Toast.LENGTH_SHORT).show()
    }
}
