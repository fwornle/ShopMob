package com.tanfra.shopmob.features.smobAdmin.presentation.view.ego

import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminMutation
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminAction
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminEvent
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobUserRepository
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import timber.log.Timber
import java.util.UUID

class AdminProfileActionProcessor(
    private val userRepository: SmobUserRepository,
) : ActionProcessor<AdminAction, AdminMutation, AdminEvent> {

    override fun invoke(action: AdminAction): Flow<Pair<AdminMutation?, AdminEvent?>> =
        flow {
            when (action) {
                is AdminAction.LoadUser -> loadUser(action.userId)
                is AdminAction.SetCurrentUser -> setCurrentUser(action.user)
                is AdminAction.NavigateToUserDetails -> navigateToUserDetails(action.user)
                is AdminAction.SaveNewUserItem ->
                    saveNewSmobUser(
                        action.name,
                        action.description,
                    )
                else -> {
                    //no-op
                    Timber.i("MVI.UI: ${action.toString().take(50)}... not found in " +
                            "AdminProfileActionProcessor")
                }
            }
        }



    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------

    // valid swipe transition on SmobUser --> handle it
    private suspend fun confirmUserSwipeAction(
        item: SmobUserATO,
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

        // update (PUT) adjusted smobUser item
        // ... also used to "DELETE" a list (marked as DELETED, then filtered out)
        // collect SmobUser flow
        val updatedUser = SmobUserATO(
            itemAdjusted.id,
            itemAdjusted.status,
            itemAdjusted.position,
            itemAdjusted.username,
            itemAdjusted.name,
            itemAdjusted.email,
            itemAdjusted.imageUrl,
            itemAdjusted.groups,
        )

        // store updated smobList in local DB
        // ... this also triggers an immediate push to the backend (once stored locally)
        userRepository.updateSmobItem(updatedUser)

    }


    // load specific user
    private suspend fun FlowCollector<Pair<AdminMutation?, AdminEvent?>>.loadUser(
        userId: String,
    ) {
        emit(AdminMutation.ShowLoader to null)

        // fetch selected user contents
        userRepository.getSmobItem(userId).collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("user flow collection returns empty")
                    emit(AdminMutation.ShowUserDetails(user = SmobUserATO()) to null)
                    emit(AdminMutation.ShowError(
                        Exception("Collection of user details from backend returns empty")
                    ) to null)
                }
                is Resource.Failure -> {
                    Timber.i("user flow collection returns error")
                    emit(AdminMutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("user flow collection successful")
                    emit(AdminMutation.ShowUserDetails(user = it.data) to null)
                }
            }  // when
        }  // userRepository
    }

    // set specific user as current user in viewModel
    private suspend fun FlowCollector<Pair<AdminMutation?, AdminEvent?>>.setCurrentUser(
        user: SmobUserATO,
    ) = emit(AdminMutation.ShowUserDetails(user) to null)

    // trigger navigation to user details screen
    private suspend fun FlowCollector<Pair<AdminMutation?, AdminEvent?>>
            .navigateToUserDetails(user: SmobUserATO) =
        emit(null to AdminEvent.NavigateToUser(user))


    // save newly created SmobUser and navigate to wherever 'onSaveDone' takes us...
    private suspend fun FlowCollector<Pair<AdminMutation?, AdminEvent?>>.saveNewSmobUser(
        name: String = "mystery user",
        description: String = "something exciting",
    ) {
        // at least the list name has to be specified for it to be saved
        if (name.isNotEmpty()) {

            // Deferred (position for the new user)
            userRepository.getSmobItems()
                .take(1)
                .collect {
                    when(it) {
                        Resource.Empty -> {
                            Timber.i("user flow collection returns empty")
                            emit(AdminMutation.ShowUsers(users = listOf()) to null)
                        }
                        is Resource.Failure -> {
                            Timber.i("user flow collection returns error")
                            emit(AdminMutation.ShowError(exception = it.exception) to null)
                        }
                        is Resource.Success -> {
                            Timber.i("user flow collection successful")
                            emit(AdminMutation.ShowUsers(users = it.data) to null)

                            // initialize new SmobUser data record to be written to DB
                            val daSmobUserATO = SmobUserATO(
                                UUID.randomUUID().toString(),
                                ItemStatus.NEW,
                                it.data.size + 1L,
                                name,
                                description,
                                "",
                            )

                            // store smob User in DB
                            userRepository.saveSmobItem(daSmobUserATO)

                            // travel back
                            emit(null to AdminEvent.NavigateBack)

                        }
                    }
                }

        }  // name not empty (should never happen)

    }   // saveNewSmobUser

}