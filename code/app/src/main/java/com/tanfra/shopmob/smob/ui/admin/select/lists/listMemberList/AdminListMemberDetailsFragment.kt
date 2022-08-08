package com.tanfra.shopmob.smob.ui.admin.select.lists.listMemberList

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


class AdminListMemberDetailsFragment : BaseFragment(), KoinComponent {

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

            // add newly selected member to list
            _viewModel.currList?.let { daList ->

                // append member ID to list of members
                _viewModel.currListMember?.id?.let {

                    // new member list
                    val updatedMemberList = daList.members.toMutableList().apply {
                        add(SmobMemberItem(
                            it,
                            SmobItemStatus.OPEN,
                            daList.members.size.toLong() + 1)
                        )
                    }

                    // update list with new member list
                    daList.members = updatedMemberList

                    // update smob list in DB
                    _viewModel.updateSmobListItem(daList)

                }  // currListMember?

            }  // currList?

            // return to selected SmobList list
            _viewModel.navigationCommand.postValue(
                NavigationCommand.BackTo(R.id.smobAdminListMemberListFragment)
            )
        }

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClearList()
    }

}
