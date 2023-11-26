package com.tanfra.shopmob.features.smobPlanning.presentation.view.products

import android.icu.text.SimpleDateFormat
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobProductRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import timber.log.Timber
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

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
                is PlanningAction.SaveNewProductOnListItem ->
                    saveProductOnList(
                        action.selectedListId,
                        action.productName,
                        action.productDescription,
                        action.productCategory,
                        action.productInShop,
                    )
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
        listRepository.getSmobItem(listId).collect { daList ->
            when(daList) {
                Resource.Empty -> {
                    Timber.i("selected list flow collection returns empty")
                    emit(PlanningMutation.ShowError(
                        Exception("Collection of lists from backend returns empty")
                    ) to null)
                }
                is Resource.Failure -> {
                    Timber.i("selected list flow collection returns error")
                    emit(PlanningMutation.ShowError(exception = daList.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("selected list flow collection successful")
                    val selList = daList.data

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
                                        list = selList,
                                        products = it.data
                                            .filter { item -> item.status != ItemStatus.DELETED }
                                            .sortedBy { item -> item.position },
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
                    emit(PlanningMutation.ShowError(
                        Exception("Collection of product details from backend returns empty")
                    ) to null)
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

    // save newly created SmobList and navigate to wherever 'onSaveDone' takes us...
    private suspend fun FlowCollector<Pair<PlanningMutation?, PlanningEvent?>>.saveProductOnList(
        selectedListId: String,
        newProductName: String,
        newProductDescription: String,
        newProductCategory: ProductCategory,
        newProductInShop: InShop,
    ) {
        // at least the product name has to be specified for it to be saved
        if (newProductName.isNotEmpty()) {

            // [1] store new product in table SmobProducts (local + backend)

            // fetch all products to determine next free product position
            productRepository.getSmobItems()
                .take(1)
                .collect { productsRes ->
                    when(productsRes) {
                        Resource.Empty -> {
                            Timber.i("product flow collection returns empty")
                            emit(
                                PlanningMutation.ShowError(
                                    Exception("Collection of products flow from backend returns empty")
                                ) to null
                            )
                        }
                        is Resource.Failure -> {
                            Timber.i("products flow collection returns error")
                            emit(PlanningMutation.ShowError(productsRes.exception) to null)
                        }
                        is Resource.Success -> {
                            Timber.i("products flow collection successful")

                            val validProducts = productsRes.data
                                .filter { itm -> itm.status != ItemStatus.DELETED }
                            val validProductsMaxPosition =
                                validProducts.fold(0L) { max, item ->
                                    if (item.position > max) {
                                        item.position
                                    } else {
                                        max
                                    }
                                }

                            val sdf = SimpleDateFormat(
                                "dd/M/yyyy hh:mm:ss",
                                Locale.GERMANY
                            )
                            val currentDate = sdf.format(Date())

                            // initialize new SmobList data record to be written to DB
                            val daSmobProductATO = SmobProductATO(
                                id = UUID.randomUUID().toString(),
                                status = ItemStatus.OPEN,
                                position = validProductsMaxPosition + 1L,
                                name = newProductName,
                                description = newProductDescription,
                                imageUrl = "http://there.should.be.a.picture.url",
                                category = newProductCategory,
                                activity = ActivityStatus(currentDate, 0),
                                inShop = newProductInShop,
                            )

                            // store new SmobProduct in DB
                            productRepository.saveSmobItem(daSmobProductATO)



                            // [2] add new product onto current SmobList

                            // fetch current shopping list from ID
                            listRepository.getSmobItem(selectedListId)
                                .take(1)
                                .collect {

                                    // valid data? (making sure...)
                                    when (it) {
                                        Resource.Empty -> {
                                            Timber.i("(current shopping) list flow collection returns empty")
                                            emit(
                                                PlanningMutation.ShowError(
                                                    Exception("Collection of (current shopping) list from backend returns empty")
                                                ) to null
                                            )
                                        }

                                        is Resource.Failure -> {
                                            Timber.i("(current shopping) list flow collection returns error")
                                            emit(PlanningMutation.ShowError(exception = it.exception) to null)
                                        }

                                        is Resource.Success -> {
                                            Timber.i("(current shopping) list flow collection successful")

                                            val currentList = it.data
                                            val prodsOnList = currentList.items

                                            val validItems = prodsOnList
                                                .filter { itm -> itm.status != ItemStatus.DELETED }
                                            val nValidItems = validItems.size
                                            val itemMaxPosition =
                                                currentList.items.fold(0L) { max, item ->
                                                    if (item.listPosition > max) {
                                                        item.listPosition
                                                    } else {
                                                        max
                                                    }
                                                }


                                            // add smob item to the currently open shopping list
                                            val newItems = currentList.items.toMutableList()
                                            newItems.add(
                                                SmobListItem(
                                                    daSmobProductATO.id,
                                                    daSmobProductATO.status,
                                                    itemMaxPosition + 1L,
                                                    daSmobProductATO.category.main,
                                                )
                                            )

                                            // create updated smobList (to be sent to the DB/backend)
                                            val updatedListEntry = currentList.copy(
                                                items = newItems,
                                                lifecycle = SmobListLifecycle(
                                                    if (currentList.lifecycle.status.ordinal <= ItemStatus.OPEN.ordinal) {
                                                        ItemStatus.OPEN
                                                    } else {
                                                        currentList.lifecycle.status
                                                    },
                                                    when (nValidItems) {
                                                        0 -> 0.0
                                                        else -> {
                                                            val doneItems = validItems
                                                                .filter { daItem ->
                                                                    daItem.status == ItemStatus.DONE
                                                                }.size
                                                            (100.0 * doneItems / nValidItems).roundToInt()
                                                                .toDouble()
                                                        }
                                                    }
                                                )
                                            )

                                            // update List entry in DB (items, statistics)
                                            listRepository.updateSmobItem(updatedListEntry)

                                            // travel back
                                            emit(null to PlanningEvent.NavigateBack)

                                        }  // Resource.Success (list)
                                    }  // when (list)
                                }  // collect (list)

                        }  // Success (productsRes)
                    }  // when (productsRes)
                }  // collect (productsRes)

        }  // name not empty (should never happen)
    }   // saveProductOnList

}