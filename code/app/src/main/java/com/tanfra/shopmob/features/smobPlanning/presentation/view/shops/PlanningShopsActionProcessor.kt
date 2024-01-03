package com.tanfra.shopmob.features.smobPlanning.presentation.view.shops

import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobShopRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import timber.log.Timber
import java.util.UUID

class PlanningShopsActionProcessor(
    private val shopRepository: SmobShopRepository,
) : ActionProcessor<PlanningAction, PlanningMutation, PlanningEvent> {

    override fun invoke(action: PlanningAction): Flow<Pair<PlanningMutation?, PlanningEvent?>> =
        flow {
            when (action) {
                is PlanningAction.ConfirmShopSwipe -> confirmShopSwipeAction(action.item)
                is PlanningAction.NavigateToShopDetails -> navigateToShopDetails(action.shop)
                is PlanningAction.SaveNewShopItem ->
                    saveNewSmobShop(
                        action.name,
                        action.description,
                    )
                else -> {
                    //no-op
                }
            }
        }



    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------

    // valid swipe transition on SmobShop --> handle it
    private suspend fun confirmShopSwipeAction(
        item: SmobShopATO,
    ) {

        /*
         * CURRENTLY NOT DOING ANYTHING (fw-240301)
         */

        // consolidate list item data (prior to writing to the DB)
        val itemAdjusted = if(item.status != ItemStatus.DELETED) {
            // user swiped right --> marking all sub-entries as "IN_PROGRESS" + aggregating here
//            consolidateListItem(item)
            item
        } else {
            // user swiped left --> delete list (by marking it as DELETED)
            item
        }

        // update (PUT) adjusted smobShop item
        // ... also used to "DELETE" a list (marked as DELETED, then filtered out)
        // collect SmobShop flow
        val updatedShop = SmobShopATO(
            itemAdjusted.id,
            itemAdjusted.status,
            itemAdjusted.position,
            itemAdjusted.name,
            itemAdjusted.description,
            itemAdjusted.imageUrl,
            itemAdjusted.location,
            itemAdjusted.type,
            itemAdjusted.category,
            itemAdjusted.business,
        )

        // store updated smobList in local DB
        // ... this also triggers an immediate push to the backend (once stored locally)
        shopRepository.updateSmobItem(updatedShop)

    }


    // trigger navigation to shop details screen
    private suspend fun FlowCollector<Pair<PlanningMutation?, PlanningEvent?>>
            .navigateToShopDetails(shop: SmobShopATO) =
        emit(null to PlanningEvent.NavigateToShop(shop))


    // save newly created SmobShop and navigate to wherever 'onSaveDone' takes us...
    private suspend fun FlowCollector<Pair<PlanningMutation?, PlanningEvent?>>.saveNewSmobShop(
        name: String = "mystery shop",
        description: String = "something exciting",
    ) {
        // at least the list name has to be specified for it to be saved
        if (name.isNotEmpty()) {

            // Deferred (position for the new shop)
            shopRepository.getSmobItems()
                .take(1)
                .collect {
                    when(it) {
                        Resource.Empty -> {
                            Timber.i("shop flow collection returns empty")
                            emit(PlanningMutation.ShowShops(shops = listOf()) to null)
                        }
                        is Resource.Failure -> {
                            Timber.i("shop flow collection returns error")
                            emit(PlanningMutation.ShowError(exception = it.exception) to null)
                        }
                        is Resource.Success -> {
                            Timber.i("shop flow collection successful")
                            emit(PlanningMutation.ShowShops(shops = it.data) to null)

                            // initialize new SmobShop data record to be written to DB
                            val daSmobShopATO = SmobShopATO(
                                UUID.randomUUID().toString(),
                                ItemStatus.NEW,
                                it.data.size + 1L,
                                name,
                                description,
                                "",
                            )

                            // store smob Shop in DB
                            shopRepository.saveSmobItem(daSmobShopATO)

                            // travel back
                            emit(null to PlanningEvent.NavigateBack)

                        }
                    }
                }

        }  // name not empty (should never happen)

    }   // saveNewSmobShop

}