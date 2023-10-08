package com.tanfra.shopmob.features.smobPlanning.presentation

import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Action
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Event
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobGroupRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class UserActionProcessor(
    private val listRepository: SmobListRepository,
    private val groupRepository: SmobGroupRepository,
) : ActionProcessor<Action, Mutation, Event> {

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when (action) {
                is Action.LoadGroups -> loadGroups()
                is Action.ConfirmSwipe -> confirmSwipeAction(action.item)
                else -> {
                    //no-op
                }
            }
        }




    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------

    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.loadGroups() {
        groupRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("group flow collection returns empty")
                    emit(null to Event.GroupsLoaded(groups = listOf()))
                }
                is Resource.Failure -> {
                    Timber.i("group flow collection returns error")
                    emit(null to Event.GroupsLoaded(groups = listOf()))
                }
                is Resource.Success -> {
                    Timber.i("group flow collection successful")
                    emit(null to Event.GroupsLoaded(groups = it.data))
                }
            }
        }
    }


    // valid swipe transition --> handle it
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.confirmSwipeAction(
        item: SmobListATO,
    ) {
        // TODO --> emit something??? ( or remove unused Receiver)
        // TODO --> emit something??? ( or remove unused Receiver)
        // TODO --> emit something??? ( or remove unused Receiver)

        // consolidate list item data (prior to writing to the DB)
        val itemAdjusted = if(item.status != ItemStatus.DELETED) {
            // user swiped right --> marking all sub-entries as "IN_PROGRESS" + aggregating here
            consolidateListItem(item)
        } else {
            // user swiped left --> delete list (by marking it as DELETED)
            item
        }

        // update (PUT) adjusted smobList item
        // ... also used to "DELETE" a list (marked as DELETED, then filtered out)
        // collect SmobList flow
        val updatedList = SmobListATO(
            itemAdjusted.id,
            itemAdjusted.status,
            itemAdjusted.position,
            itemAdjusted.name,
            itemAdjusted.description,
            // replace list of products on smob list with updated list of products
            itemAdjusted.items.map { product ->
                if(product.id == itemAdjusted.id) {
                    // set new status (list property)
                    SmobListItem(
                        product.id,
                        itemAdjusted.status,
                        product.listPosition,
                        product.mainCategory,
                    )
                } else {
                    product
                }
            },
            itemAdjusted.groups,
            itemAdjusted.lifecycle,
        )

        // store updated smobList in local DB
        // ... this also triggers an immediate push to the backend (once stored locally)
        listRepository.updateSmobItem(updatedList)

    }

}