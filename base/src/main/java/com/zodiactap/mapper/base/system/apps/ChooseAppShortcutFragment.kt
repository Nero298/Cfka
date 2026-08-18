package com.zodiactap.mapper.base.system.apps

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.EpoxyRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.base.databinding.FragmentSimpleRecyclerviewBinding
import com.zodiactap.mapper.base.simple
import com.zodiactap.mapper.base.utils.ui.RecyclerViewUtils
import com.zodiactap.mapper.base.utils.ui.SimpleRecyclerViewFragment
import com.zodiactap.mapper.base.utils.ui.launchRepeatOnLifecycle
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.system.apps.AppShortcutInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class ChooseAppShortcutFragment : SimpleRecyclerViewFragment<AppShortcutListItem>() {

    companion object {
        const val REQUEST_KEY = "request_app_shortcut"
        const val EXTRA_RESULT = "extra_choose_app_shortcut_result"
        const val SEARCH_STATE_KEY = "key_app_shortcut_search_state"
    }

    private val args: ChooseAppShortcutFragmentArgs by navArgs()
    override var searchStateKey: String? = SEARCH_STATE_KEY

    private val viewModel: ChooseAppShortcutViewModel by viewModels()

    override val listItems: Flow<State<List<AppShortcutListItem>>>
        get() = viewModel.state

    private val appShortcutConfigLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result ?: return@registerForActivityResult

            if (result.resultCode == Activity.RESULT_OK) {
                result.data ?: return@registerForActivityResult

                viewModel.onConfigureShortcutResult(result.data!!)
            }
        }

    override fun subscribeUi(binding: FragmentSimpleRecyclerviewBinding) {
        super.subscribeUi(binding)

        RecyclerViewUtils.applySimpleListItemDecorations(binding.epoxyRecyclerView)

        viewLifecycleOwner.launchRepeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.returnResult.collectLatest {
                returnResult(EXTRA_RESULT to Json.encodeToString(it))
            }
        }
    }

    override fun populateList(
        recyclerView: EpoxyRecyclerView,
        listItems: List<AppShortcutListItem>,
    ) {
        binding.epoxyRecyclerView.withModels {
            listItems.forEach {
                simple {
                    id(it.id)
                    model(it)

                    onClickListener { _ ->
                        launchShortcutConfiguration(it.shortcutInfo)
                    }
                }
            }
        }
    }

    override fun onSearchQuery(query: String?) {
        viewModel.searchQuery.value = query
    }

    override fun getRequestKey(): String = args.requestKey

    private fun launchShortcutConfiguration(shortcutInfo: AppShortcutInfo) {
        Intent(Intent.ACTION_CREATE_SHORTCUT).apply {
            setClassName(shortcutInfo.packageName, shortcutInfo.activityName)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1)

            try {
                appShortcutConfigLauncher.launch(this)
            } catch (e: SecurityException) {
                Toast.makeText(
                    requireContext(),
                    R.string.error_keymapper_doesnt_have_permission_app_shortcut,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}
