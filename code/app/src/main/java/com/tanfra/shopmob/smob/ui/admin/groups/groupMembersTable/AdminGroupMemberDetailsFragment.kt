package com.tanfra.shopmob.smob.ui.admin.groups.groupMembersTable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentAdminGroupMemberDetailsBinding
import com.tanfra.shopmob.smob.data.local.utils.SmobMemberItem
import com.tanfra.shopmob.smob.data.local.utils.ItemStatus
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber


class AdminGroupMemberDetailsFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin service locator)
    override val _viewModel: AdminViewModel by activityViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentAdminGroupMemberDetailsBinding


    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_group_member_details,
                container,
                false
            )

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = _viewModel

        return binding.root
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // set onClick handler for 'Add to Group' button
        // ... navigate back to the main app
        binding.btAddToGroup.setOnClickListener {

            // back to default: button invisible
            _viewModel.enableAddButton = false

            // add newly selected member to group
            _viewModel.currGroup?.let { daGroup ->

                // append member ID to list of members
                _viewModel.currGroupMember?.itemId?.let { newMemberId ->

                    // create new member list (adding newMemberId)
                    val updatedMemberList = daGroup.members
                        // filter out previously "DELETED" entries of this member
                        .filter { member -> member.id != newMemberId }
                        // now add new member (as "OPEN")
                        .toMutableList().apply {
                            add(SmobMemberItem(
                                newMemberId,
                                ItemStatus.OPEN,
                                this.size.toLong() + 1)
                            )
                        }

                    // update group with new member list
                    daGroup.members = updatedMemberList

                    // update smob Group in DB
                    _viewModel.updateSmobGroupItem(daGroup)

                    // ensure consistency in the _viewModel snapshots
                    _viewModel.currGroup = daGroup

                    // also update newly added user's group list
                    _viewModel.currGroupMember?.let { daMember ->

                        val updatedGroupMemberIds =
                            daMember.groups
                                // hygiene: remove accidental empty entries
                                .filter { groupId -> groupId != "" }
                                // remove all occurrences of this group ID from member's group list
                                .filter { groupId -> groupId != daGroup.itemId }
                                // now re-add this group ID (guarantees that it is there only once)
                                .toMutableList().apply { add(daGroup.itemId) }

                        // update purged member's group list
                        daMember.groups = updatedGroupMemberIds

                        // update smob User in DB
                        _viewModel.updateSmobUserItem(daMember)

                        // ensure this is updated too
                        _viewModel.currGroupMember = daMember

                    }

                }  // currGroupMember?

            }  // currGroup?

            // return to selected group list
            Timber.i("_viewModel.backDestinationId = ${_viewModel.backDestinationId}")
            when(_viewModel.backDestinationId) {
                R.id.smobAdminListGroupsTableFragment ->
                    // special case: we came from lists...
                    _viewModel.navigationCommand.postValue(
                        NavigationCommand.BackTo(R.id.smobAdminListGroupsTableFragment)
                    )
                else ->
                    // default: we came from groups...
                    _viewModel.navigationCommand.postValue(
                        NavigationCommand.BackTo(R.id.smobAdminGroupsTableFragment)
                    )
            }

        }

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClearGroup()
    }

}
