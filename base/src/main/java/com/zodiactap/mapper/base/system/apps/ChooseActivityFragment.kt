package com.zodiactap.mapper.base.system.apps

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.EpoxyRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.zodiactap.mapper.base.databinding.FragmentSimpleRecyclerviewBinding
import com.zodiactap.mapper.base.simple
import com.zodiactap.mapper.base.utils.ui.RecyclerViewUtils
import com.zodiactap.mapper.base.utils.ui.SimpleRecyclerViewFragment
import com.zodiactap.mapper.common.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class ChooseActivityFragment : SimpleRecyclerViewFragment<AppActivityListItem>() {

    companion object {
        const val EXTRA_RESULT = "extra_activity_info"
        const val SEARCH_STATE_KEY = "key_activity_list_search_state"
    }

    override var searchStateKey: String? = SEARCH_STATE_KEY

    private val args: ChooseActivityFragmentArgs by navArgs()

    private val viewModel: ChooseActivityViewModel by viewModels()

    override val listItems: Flow<State<List<AppActivityListItem>>>
        get() = viewModel.listItems

    override fun onSearchQuery(query: String?) {
        viewModel.searchQuery.value = query
    }

    override fun subscribeUi(binding: FragmentSimpleRecyclerviewBinding) {
        super.subscribeUi(binding)

        RecyclerViewUtils.applySimpleListItemDecorations(binding.epoxyRecyclerView)
    }

    override fun populateList(
        recyclerView: EpoxyRecyclerView,
        listItems: List<AppActivityListItem>,
    ) {
        recyclerView.withModels {
            listItems.forEach {
                simple {
                    id(it.id)
                    model(it)

                    onClickListener { _ ->
                        returnResult(EXTRA_RESULT to Json.encodeToString(it.activityInfo))
                    }
                }
            }
        }
    }

    override fun getRequestKey(): String = args.requestKey
}
