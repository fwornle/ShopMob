package com.tanfra.shopmob.smob.ui.planning.listsEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentPlanningListsEditBinding
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.local.utils.SmobListLifecycle
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.ui.planning.utils.closeSoftKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import java.util.*


class PlanningListsEditFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin) this time as a singleton to be shared with another fragment
    override val _viewModel: PlanningListsEditViewModel by viewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentPlanningListsEditBinding

    // assemble smobList data item
    private lateinit var daSmobListATO: SmobListATO


    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_planning_lists_edit, container, false)

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = _viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // clicking on the 'saveSmobList' FAB saves the newly created smobList
        binding.saveSmobList.setOnClickListener {

            // close SoftKeyboard
            closeSoftKeyboard(requireContext(), view)

            // initialize data record to be written to DB
            // ... if no better values have been provided by the user (taken from viewModel), this
            //     is going to be the data record written to the DB
            daSmobListATO = SmobListATO(
                UUID.randomUUID().toString(),
                SmobItemStatus.NEW,
                -1L,
                _viewModel.smobListName.value ?: "mystery list",
                _viewModel.smobListDescription.value ?: "something exciting",
                listOf(),
                listOf(),
                SmobListLifecycle(SmobItemStatus.OPEN, 0.0),
            )

            // store smob List in DB
            // ... this also takes the user back to the SmobListsFragment
            _viewModel.validateAndSaveSmobList(daSmobListATO)

        }  // onClickListener (FAB - save)

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

}
