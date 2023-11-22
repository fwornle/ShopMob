package com.tanfra.shopmob.features.smobPlanning.presentation.view.products

import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobProductRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class PlanningProductsActionProcessor(
    private val listRepository: SmobListRepository,
    private val productRepository: SmobProductRepository,
) : ActionProcessor<PlanningAction, PlanningMutation, PlanningEvent> {

    override fun invoke(action: PlanningAction): Flow<Pair<PlanningMutation?, PlanningEvent?>> =
        flow {
            when (action) {
                is PlanningAction.ConfirmProductOnListSwipe -> confirmProductSwipeAction(
                    action.list,
                    action.product
                )
                is PlanningAction.LoadProductsOnList -> loadProductList(action.listId)
                is PlanningAction.LoadProduct -> loadProduct(action.productId)
                else -> {
                    //no-op
                }
            }
        }



    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------

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


    // load list of products associated with currently selected SmobList
    private suspend fun FlowCollector<Pair<PlanningMutation?, PlanningEvent?>>.loadProductList(
        listId: String,
    ) {
        emit(PlanningMutation.ShowLoader to null)

        // fetch selected list contents
        listRepository.getSmobItem(listId).collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("selected list flow collection returns empty")
                    // TODO: should there be a user perceivable reaction as in Failure?
                }
                is Resource.Failure -> {
                    Timber.i("selected list flow collection returns error")
                    emit(PlanningMutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("selected list flow collection successful")
                    val selList = it.data

                    // fetch products on selected list
                    productRepository.getSmobProductsByListId(listId).collect {
                        when (it) {
                            Resource.Empty -> {
                                Timber.i("product list flow collection returns empty")
                                emit(
                                    PlanningMutation.ShowProductsOnList(
                                        list = selList, products = listOf()
                                    ) to PlanningEvent.NavigateToList(selList)
                                )
                            }

                            is Resource.Failure -> {
                                Timber.i("product list flow collection returns error")
                                emit(PlanningMutation.ShowError(exception = it.exception) to null)
                            }

                            is Resource.Success -> {
                                Timber.i("product list flow collection successful")
                                emit(
                                    PlanningMutation.ShowProductsOnList(
                                        list = selList, products = it.data
                                    ) to PlanningEvent.NavigateToList(selList)
                                )
                            }
                        }  // when
                    }  // productRepository
                }
            }  // when
        }  // listRepository

    }


    // load specific product
    private suspend fun FlowCollector<Pair<PlanningMutation?, PlanningEvent?>>.loadProduct(
        productId: String,
    ) {
        emit(PlanningMutation.ShowLoader to null)

        // fetch selected product contents
        productRepository.getSmobItem(productId).collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("product flow collection returns empty")
                    emit(PlanningMutation.ShowProductDetails(product = SmobProductATO()) to null)
                    // TODO: should there be a more useful reaction than displaying an invalid item?
                }
                is Resource.Failure -> {
                    Timber.i("product flow collection returns error")
                    emit(PlanningMutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("product flow collection successful")
                    emit(PlanningMutation.ShowProductDetails(product = it.data) to null)
                }
            }  // when
        }  // productRepository
    }

}