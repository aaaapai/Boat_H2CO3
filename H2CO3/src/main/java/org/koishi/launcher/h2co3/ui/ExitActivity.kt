package org.koishi.launcher.h2co3.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog

@Keep
class ExitActivity : H2CO3Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = intent.getIntExtra(EXTRA_CODE, -1)

        val exitDialog = H2CO3MessageDialog(this)
            .setMessage("Minecraft exited with code:$code")
            .setPositiveButton("Exit") { _: DialogInterface?, _: Int -> finish() }
            .setOnDismissListener { _: DialogInterface? ->
                finish()
                startActivity(Intent(this, H2CO3MainActivity::class.java))
            } as H2CO3MessageDialog
        exitDialog.show()
    }

    companion object {
        private const val EXTRA_CODE = "code"

        @JvmStatic
        fun showExitMessage(ctx: Context, code: Int) {
            val i = Intent(
                ctx,
                if (code == 0) H2CO3MainActivity::class.java else ExitActivity::class.java
            )
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra(EXTRA_CODE, code)
            ctx.startActivity(i)
        }
    }
}