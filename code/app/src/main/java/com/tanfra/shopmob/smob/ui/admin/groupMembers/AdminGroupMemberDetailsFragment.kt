package com.tanfra.shopmob.smob.ui.admin.groupMembers

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.databinding.FragmentAdminGroupMemberDetailsBinding
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import com.tanfra.shopmob.smob.ui.planning.utils.closeSoftKeyboard
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent
import java.util.*


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

            // ToDo: add to group

            // return to selected group list
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminGroupMemberDetailsFragmentDirections
                        .actionSmobAdminGroupMemberDetailsFragmentToSmobAdminGroupMembersListFragment()
                )
            )

        }

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClearGroup()
    }

}
