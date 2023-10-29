package com.tanfra.shopmob.features.smobPlanning.presentation

import android.content.Context
import android.os.Vibrator
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Action
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Event
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobGroupRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobProductRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem
import com.tanfra.shopmob.smob.ui.zeUtils.vibrateDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import timber.log.Timber
import java.util.UUID

class ActionProcessorSmobLists(
    private val context: Context,
    private val listRepository: SmobListRepository,
    private val productRepository: SmobProductRepository,
    private val groupRepository: SmobGroupRepository,
) : ActionProcessor<Action, Mutation, Event> {

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when (action) {
                is Action.LoadGroups -> loadGroups()
                is Action.ConfirmListSwipe -> confirmListSwipeAction(action.item)
                is Action.ConfirmProductOnListSwipe -> confirmProductSwipeAction(
                    action.list,
                    action.product
                )
                is Action.IllegalSwipe -> illegalSwipeAction()
                is Action.NavigateToProductsOnList -> navigateToProductsOnList(action.list)
                is Action.LoadProductList -> loadProductList(action.listId)
                is Action.SaveNewItem ->
                    saveNewSmobList(
                        action.name,
                        action.description,
                        action.group
                    )
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
                    emit(Mutation.ShowFormWithGroups(groups = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("group flow collection returns error")
                    emit(Mutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("group flow collection successful")
                    emit(Mutation.ShowFormWithGroups(groups = it.data) to null)
                }
            }
        }
    }


    // valid swipe transition on SmobList --> handle it
    private suspend fun confirmListSwipeAction(
        item: SmobListATO,
    ) {
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


    // valid swipe transition on SmobProduct --> handle it
    private suspend fun confirmProductSwipeAction(
        list: SmobListATO,
        product: SmobProductATO,
    ) {

        // change product status in selected list and re-compute list statistics
        val updatedList = consolidateListItem(
            list.copy(
                items = list.items.map { item ->
                    if(item.id == product.id) {
                        // set new status (list property)
                        SmobListItem(
                            item.id,
                            product.status,
                            item.listPosition,
                            item.mainCategory,
                        )
                    } else {
                        item
                    }
                }
            )  // copy
        )  // consolidate

        // store updated smobList in local DB
        // ... this also triggers an immediate push to the backend (once stored locally)
        listRepository.updateSmobItem(updatedList)

    }


    // illegal swipe transition --> vibrate phone
    private fun illegalSwipeAction() {
        val vib = context.getSystemService(Vibrator::class.java)
        vibrateDevice(vib, 150)
    }


    // save newly created SmobList and navigate to wherever 'onSaveDone' takes us...
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.navigateToProductsOnList(
        list: SmobListATO,
    ) = emit(null to Event.NavigateToList(list))


    // load list of products associated with currently selected SmobList
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.loadProductList(
        listId: String,
    ) {
        emit(Mutation.ShowLoader to null)

        // fetch selected list contents
        listRepository.getSmobItem(listId).collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("selected list flow collection returns empty")
                    // TODO: should there be a user perceivable reaction as in Failure?
                }
                is Resource.Failure -> {
                    Timber.i("list flow collection returns error")
                    emit(Mutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("list flow collection successful")
                    val selList = it.data

                    // fetch products on selected list
                    productRepository.getSmobProductsByListId(listId).collect {
                        when (it) {
                            Resource.Empty -> {
                                Timber.i("product list flow collection returns empty")
                                emit(
                                    Mutation.ShowProductsOnList(
                                        list = selList, products = listOf()
                                    ) to Event.NavigateToList(selList)
                                )
                            }

                            is Resource.Failure -> {
                                Timber.i("list flow collection returns error")
                                emit(Mutation.ShowError(exception = it.exception) to null)
                            }

                            is Resource.Success -> {
                                Timber.i("list flow collection successful")
                                emit(
                                    Mutation.ShowProductsOnList(
                                        list = selList, products = it.data
                                    ) to Event.NavigateToList(selList)
                                )
                            }
                        }  // when
                    }  // productRepository
                }
            }  // when
        }  // listRepository

    }


    // save newly created SmobList and navigate to wherever 'onSaveDone' takes us...
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.saveNewSmobList(
        name: String = "mystery list",
        description: String = "something exciting",
        group: Pair<String, String> = Pair("", ""),
    ) {
        // at least the list name has to be specified for it to be saved
        if (name.isNotEmpty()) {

            // Deferred (position for the new list)
            listRepository.getSmobItems()
                .take(1)
                .collect {
                when(it) {
                    Resource.Empty -> {
                        Timber.i("list flow collection returns empty")
                        emit(Mutation.ShowLists(lists = listOf()) to null)
                    }
                    is Resource.Failure -> {
                        Timber.i("list flow collection returns error")
                        emit(Mutation.ShowError(exception = it.exception) to null)
                    }
                    is Resource.Success -> {
                        Timber.i("list flow collection successful")
                        emit(Mutation.ShowLists(lists = it.data) to null)

                        // initialize new SmobList data record to be written to DB
                        val daSmobListATO = SmobListATO(
                            UUID.randomUUID().toString(),
                            ItemStatus.NEW,
                            it.data.size + 1L,
                            name,
                            description,
                            listOf(),
                            listOf(
                                SmobGroupItem(
                                    group.first,
                                    ItemStatus.NEW,
                                    0L,
                                )
                            ),
                            SmobListLifecycle(ItemStatus.NEW, 0.0),
                        )

                        // store smob List in DB
                        listRepository.saveSmobItem(daSmobListATO)

                        // travel back
                        emit(null to Event.NavigateBack)

                    }
                }
            }

        }  // name not empty (should never happen)

    }   // saveNewSmobList

}