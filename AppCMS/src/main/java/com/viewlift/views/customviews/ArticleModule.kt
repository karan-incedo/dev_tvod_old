package com.viewlift.views.customviews

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.viewlift.models.data.appcms.api.Module
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules
import com.viewlift.presenters.AppCMSPresenter

/**
 * Created by viewlift on 1/11/18.
 */

class ArticleModule<ModuleWithComponents :
    com.viewlift.models.data.appcms.ui.page.ModuleWithComponents?>(context: Context,
                                                                   module: ModuleWithComponents,
                                                                   moduleAPI: Module,
                                                                   jsonValueKeyMap: Map<String, AppCMSUIKeyType>,
                                                                   appCMSPresenter: AppCMSPresenter,
                                                                   viewCreator: ViewCreator,
                                                                   appCMSAndroidModules: AppCMSAndroidModules,
                                                                   articleUrl: String) :
        ModuleView<ModuleWithComponents>(context, module, true) {
    val parentView = LinearLayout(context)
    val articleView = WebView(context)
    val socialMediaView = FrameLayout(context)

    init {
        parentView.orientation = LinearLayout.VERTICAL
        parentView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        articleView.loadUrl(articleUrl)
        articleView.isClickable = false


        addView(parentView)
    }
}