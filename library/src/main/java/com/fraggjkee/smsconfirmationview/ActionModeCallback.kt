package com.fraggjkee.smsconfirmationview

import android.content.ClipDescription.MIMETYPE_TEXT_HTML
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.getSystemService

internal class ActionModeCallback(
    context: Context,
    private val onPaste: (String) -> Unit
) : ActionMode.Callback {

    private val clipboard = requireNotNull(context.getSystemService<ClipboardManager>())

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        return if (clipboard.hasTextClip) {
            mode.menuInflater.inflate(R.menu.context_menu, menu)
            true
        } else {
            false
        }
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (item.itemId == R.id.paste) {
            clipboard.plainTextClip?.let { plainTextClip -> onPaste(plainTextClip) }
        }
        mode.finish()
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false
    override fun onDestroyActionMode(mode: ActionMode?) = Unit
}

private val ClipboardManager.hasTextClip: Boolean
    get() {
        val primaryClipDescription = primaryClipDescription
        return if (hasPrimaryClip().not() || primaryClipDescription == null) {
            false
        } else {
            primaryClipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN) ||
                primaryClipDescription.hasMimeType(MIMETYPE_TEXT_HTML)
        }
    }

private val ClipboardManager.plainTextClip: String?
    get() =
        if (hasTextClip) primaryClip?.getItemAt(0)?.text?.toString()
        else null

