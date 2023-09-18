package com.tanfra.shopmob.smob.ui.zeUtils

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import timber.log.Timber

/**
 * combine the two flows (eg. products on shopping list with the list itself)
 */
@ExperimentalCoroutinesApi
inline fun <reified P: Ato, reified C: Ato, reified T: Ato> combineFlows(
    parentResFlow: Flow<Resource<P>>,
    childListResFlow: Flow<Resource<List<C>>>,
    crossinline combineBlock: (parentItem: P, childList: List<C>) -> List<T>,
): Flow<List<T>> {

    return childListResFlow.combine(parentResFlow) { childListRes, parentItemRes ->

        // unwrap list (from Resource)
        when (parentItemRes) {
            is Resource.Failure -> {
                Timber.i("combineFlows: Couldn't retrieve list from remote")
                listOf()
            }
            is Resource.Empty -> {
                Timber.i("combineFlows: list still loading")
                listOf()
            }
            is Resource.Success -> {
                parentItemRes.data.let { parentItem ->

                    // evaluate/unwrap Resource
                    when (childListRes) {
                        is Resource.Failure -> {
                            Timber.i("combineFlows: Couldn't retrieve list items from remote")
                            listOf()
                        }
                        is Resource.Empty -> {
                            Timber.i("combineFlows: list items still loading")
                            listOf()
                        }
                        is Resource.Success -> {
                            combineBlock(parentItem, childListRes.data)
                        }
                    }

                }  // let...
            }  // Resource.Success
        }  // when(list)

    }

}  //  combineFlows
