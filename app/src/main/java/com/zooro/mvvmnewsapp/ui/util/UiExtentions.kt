package com.zooro.mvvmnewsapp.ui.util

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.zooro.mvvmnewsapp.R

fun Fragment.showSnackbar(
    message: String,
    actionText: String? = null,
    action: (() -> Unit)? = null,
    dismissAction: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        .setAnchorView(R.id.bottomNavigationView)
        .apply {
            actionText?.let { text ->
                action?.let { callback ->
                    setAction(text) { callback.invoke() }
                }
            }
        }

    snackbar.addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            if (event != DISMISS_EVENT_ACTION) {
                dismissAction?.invoke()
            }
        }
    })

    snackbar.show()
}