package com.zodiactap.mapper.base.utils

import com.zodiactap.mapper.base.utils.ui.ISearchable
import com.zodiactap.mapper.common.utils.State
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

fun <T : ISearchable> List<T>.filterByQuery(query: String?): Flow<State<List<T>>> = flow {
    if (query.isNullOrBlank()) {
        emit(State.Data(this@filterByQuery))
    } else {
        emit(State.Loading)

        val filteredList = withContext(Dispatchers.Default) {
            this@filterByQuery.filter { model ->
                model.getSearchableString().containsQuery(query)
            }
        }

        emit(State.Data(filteredList))
    }
}

fun String.containsQuery(query: String?): Boolean {
    if (query.isNullOrBlank()) return true

    return lowercase(Locale.getDefault()).contains(query.trim().lowercase(Locale.getDefault()))
}
