package com.tanfra.shopmob.smob.ui.admin.select.groups.groupMemberList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentAdminGroupMemberDetailsBinding
import com.tanfra.shopmob.smob.data.local.utils.SmobMemberItem
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent


class AdminGroupMemberDetailsFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin service locator)
    override val _viewModel: AdminViewModel by sharedViewModel()

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
                _viewModel.currGroupMember?.id?.let {

                    // new member list
                    val updatedMemberList = daGroup.members.toMutableList().apply {
                        add(SmobMemberItem(
                            it,
                            SmobItemStatus.OPEN,
                            daGroup.members.size.toLong() + 1)
                        )
                    }

                    // update group with new member list
                    daGroup.members = updatedMemberList

                    // update smob Group in DB
                    _viewModel.updateSmobGroupItem(daGroup)

                }  // currGroupMember?

            }  // currGroup?

            // return to selected group list
            _viewModel.navigationCommand.postValue(
                NavigationCommand.BackTo(R.id.smobAdminGroupMemberListFragment)
            )
        }

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClearGroup()
    }

}
