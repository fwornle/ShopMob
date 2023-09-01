package com.tanfra.shopmob.smob.ui.admin.groups.groupsTable

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.databinding.FragmentAdminGroupsAddNewItemBinding
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.utils.ui.closeSoftKeyboard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.util.*


class AdminGroupsAddNewItemFragment : BaseFragment(), AdapterView.OnItemSelectedListener, KoinComponent {

    // get the view model (from Koin service locator) ... shared with PlanningListsFragment
    override val viewModel: AdminViewModel by activityViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentAdminGroupsAddNewItemBinding

    // assemble smobList data item
    private lateinit var daSmobGroupATO: SmobGroupATO

    // new lists are created at the highest position (+1)
    private var listPosMax: Long = 0L


    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_admin_groups_add_new_item, container, false)

        // fetch currently highest list position from incoming bundle
        listPosMax = arguments?.getLong("listPosMax") ?: 0L

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = viewModel

        return binding.root
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // clicking on the 'saveSmobList' FAB saves the newly created smobList
        binding.savesmobGroup.setOnClickListener {

            // close SoftKeyboard
            closeSoftKeyboard(requireContext(), view)

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.GERMANY)
            val currentDate = sdf.format(Date())

            // initialize data record to be written to DB
            // ... if no better values have been provided by the user (taken from viewModel), this
            //     is going to be the data record written to the DB
            daSmobGroupATO = SmobGroupATO(
                UUID.randomUUID().toString(),
                ItemStatus.OPEN,
                listPosMax + 1,
                viewModel.smobGroupName.value ?: "mystery group",
                viewModel.smobGroupDescription.value ?: "something exciting",
                viewModel.smobGroupType.value ?: GroupType.OTHER,
                SmobApp.currUser?.let {
                    listOf(SmobMemberItem(it.id, ItemStatus.OPEN, 0))
                } ?: listOf(), // members
                ActivityStatus(currentDate, 0)
            )

            // store smob Group in DB
            // ... this also takes the user back to the SmobGroupsFragment
            viewModel.validateAndSaveSmobGroup(daSmobGroupATO)

        }  // onClickListener (FAB - save)

        // set-up spinner
        setupSpinner()

    }  // onViewCreated


    // set-up spinner (group type)
    private fun setupSpinner() {

        // GroupType
        // create an ArrayAdapter using the enum and a default spinner layout
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, GroupType.values())
            .also {
                // specify the layout to use when the list of choices appears
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // apply the adapter to the spinner
                binding.smobGroupType.adapter = it
            }

        // hook up onItemSelected listener (to this fragment)
        binding.smobGroupType.onItemSelectedListener = this

    }

    // spinner: item selection made
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // we have several spinners...
        when(p0) {
            binding.smobGroupType -> {
                // set main product category
                viewModel.smobGroupType.value = GroupType.values()[p2]
            }
            else -> {
                // should not happen - unless someone added a second spinner
                Timber.i("Unknown spinner found: $p0")
            }
        }
    }

    // spinner: no item selected
    override fun onNothingSelected(p0: AdapterView<*>?) {
        Timber.i("No selection made... (this is never called?!) $p0")
    }


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        viewModel.onClearGroup()
    }

}
