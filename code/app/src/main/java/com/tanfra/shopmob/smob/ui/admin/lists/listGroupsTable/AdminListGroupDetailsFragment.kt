package com.tanfra.shopmob.smob.ui.admin.lists.listGroupsTable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentAdminListGroupDetailsBinding
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.app.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent


class AdminListGroupDetailsFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin service locator)
    override val viewModel: AdminViewModel by activityViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentAdminListGroupDetailsBinding

    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_list_group_details,
                container,
                false
            )

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = viewModel

        return binding.root
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // set onClick handler for 'Add to Group' button
        // ... navigate back to the main app
        binding.btAddToGroup.setOnClickListener {

            // Todo: change from member to group logic

            // back to default: button invisible
            viewModel.enableAddButton = false

            // add newly selected group item to groups of list
            viewModel.currList?.let { daList ->

                // append group ID (and status / position) to list of groups
                viewModel.currGroupWithListData?.id?.let { currGroupId ->

                    // assemble new groups list
                    val updatedMemberList = daList.groups.toMutableList().apply {
                        add(
                            SmobGroupItem(
                                currGroupId,
                                ItemStatus.OPEN,
                                daList.groups.size.toLong() + 1
                            )
                        )
                    }

                    // update groups list in selected SmobList
                    daList.groups = updatedMemberList

                    // update smob list in DB
                    viewModel.updateSmobListItem(daList)

                }  // currGroupId

            }  // daList

            // return to SmobList table
            viewModel.navigationCommand.postValue(
                NavigationCommand.BackTo(R.id.smobAdminListGroupsTableFragment)
            )
        }

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        viewModel.onClearList()
    }

}
