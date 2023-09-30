package com.tanfra.shopmob.smob.ui.planning.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentPlanningListsAddNewItemBinding
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.zeUtils.closeSoftKeyboard
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import java.util.*

class PlanningListsAddNewItemFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin service locator) ... shared with PlanningListsFragment
    override val viewModel: PlanningViewModel by activityViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentPlanningListsAddNewItemBinding

    // assemble smobList data item
    private lateinit var daSmobListATO: SmobListATO

    // new lists are created at the highest position (+1)
    private var listPosMax: Long = 0L


    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_planning_lists_add_new_item, container, false)

        // fetch currently highest list position from incoming bundle
        listPosMax = arguments?.getLong("listPosMax") ?: 0L

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = viewModel

        return binding.root
//        // construct view (compose)
//        return ComposeView(requireContext()).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent { PlanningScaffold(this.context, viewModel) }
//        }
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
                ItemStatus.INVALID,
                listPosMax + 1,
                viewModel.smobListName.value ?: "mystery list",
                viewModel.smobListDescription.value ?: "something exciting",
                listOf(),
                listOf(),
                SmobListLifecycle(ItemStatus.INVALID, 0.0),
            )

            // store smob List in DB
            // ... this also takes the user back to the SmobListsFragment
            viewModel.validateAndSaveSmobList(daSmobListATO)

        }  // onClickListener (FAB - save)

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        viewModel.onClearProduct()
    }

}
