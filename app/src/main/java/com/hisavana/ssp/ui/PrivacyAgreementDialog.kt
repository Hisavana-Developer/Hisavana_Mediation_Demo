package com.hisavana.ssp.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.cloud.sdk.commonutil.control.AdxPreferencesHelper
import com.hisavana.ssp.R
import com.hisavana.ssp.util.DemoConstants
import kotlin.system.exitProcess


/**
 * Created  ON 2023/7/7
 * @author :fangxuhui
 */
class PrivacyAgreementDialog(var onClickListener: OnClickListener) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomDialog)
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.hisavana_privacy_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = dialog?.window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val dialogWidth = (screenWidth * 0.8).toInt() // 设置宽度为屏幕宽度的五分之四
        layoutParams?.width = dialogWidth

        dialog?.window?.attributes = layoutParams
        val content = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getString(R.string.privacy_content),Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(getString(R.string.privacy_content))
        }
        val contentTextView = view.findViewById<TextView>(R.id.content)
        contentTextView.text = content
        contentTextView.movementMethod = LinkMovementMethod.getInstance()

        view.findViewById<TextView>(R.id.tv_exit).setOnClickListener {
            exitProcess(0)
        }

        view.findViewById<TextView>(R.id.tv_agree).setOnClickListener {
            AdxPreferencesHelper.getInstance().putBoolean(DemoConstants.USER_AGREE_PRIVACY,true)
            onClickListener.agree()
            dismiss()
        }
        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    interface OnClickListener {
        fun agree()
    }
}