package com.tanfra.shopmob.features.smobPlanning.presentation.view.products

import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Action
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Event
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
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

class SmobProductsActionProcessor(
    private val listRepository: SmobListRepository,
    private val productRepository: SmobProductRepository,
) : ActionProcessor<Action, Mutation, Event> {

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        flow {
            when (action) {
                is Action.ConfirmProductOnListSwipe -> confirmProductSwipeAction(
                    action.list,
                    action.product
                )
                is Action.LoadProductsOnList -> loadProductList(action.listId)
                is Action.LoadProduct -> loadProduct(action.productId)
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
                    Timber.i("selected list flow collection returns error")
                    emit(Mutation.ShowError(exception = it.exception) to null)
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
                                    Mutation.ShowProductsOnList(
                                        list = selList, products = listOf()
                                    ) to Event.NavigateToList(selList)
                                )
                            }

                            is Resource.Failure -> {
                                Timber.i("product list flow collection returns error")
                                emit(Mutation.ShowError(exception = it.exception) to null)
                            }

                            is Resource.Success -> {
                                Timber.i("product list flow collection successful")
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


    // load specific product
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.loadProduct(
        productId: String,
    ) {
        emit(Mutation.ShowLoader to null)

        // fetch selected product contents
        productRepository.getSmobItem(productId).collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("product flow collection returns empty")
                    emit(Mutation.ShowProductDetails(product = SmobProductATO()) to null)
                    // TODO: should there be a more useful reaction than displaying an invalid item?
                }
                is Resource.Failure -> {
                    Timber.i("product flow collection returns error")
                    emit(Mutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("product flow collection successful")
                    emit(Mutation.ShowProductDetails(product = it.data) to null)
                }
            }  // when
        }  // productRepository
    }

}