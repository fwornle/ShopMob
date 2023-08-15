package com.tanfra.shopmob.smob.ui.admin.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.RowContactBinding
import com.tanfra.shopmob.databinding.RowContactDataBinding
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.SmobContactATO
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import org.koin.core.component.KoinComponent
import java.util.*


// use data binding to show the smob item on the RV item
class AdminContactsImportTableAdapter(rootView: View, callBack: (selectedSmobContactATO: SmobContactATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobContactATO>(rootView, callBack), KoinComponent {

    // SearchView widget can be used to preFilter the list using user input
    override fun getSearchViewItems(items: List<SmobContactATO>, charSearch: String)
    : MutableList<SmobContactATO> {

        // ignore case
        val searchP = charSearch.lowercase(Locale.ROOT)

        // filter contact list according to user provided search string (SearchView)
        return items.filter { contact ->
            contact.name.lowercase(Locale.ROOT).contains(searchP) ||
                    contact.emails
                        .map { it.lowercase(Locale.ROOT) }
                        .any { it.contains(searchP) }
        }.toMutableList()

    }

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobContactATO>): List<SmobContactATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.status != ItemStatus.DELETED }
            .sortedWith(
                compareBy(
                    { it.name },
                )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.row_contact

    // called, when the user action has been confirmed and the local DB / backend needs updated
    // ... this is the point where the list can be consolidated, if needed (eg. aggregate status)
    override fun uiActionConfirmed(item: SmobContactATO, rootView: View) {
        // TODO: add/remove contact from app
    }  // uiActionConfirmed

    // dynamically adjust view item content
    override fun adjustViewItem(binding: ViewDataBinding, item: SmobContactATO) {

        // fetch layoutInflater (from context/SystemService)
        val layoutInflater = binding.root.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // concrete binding (with specific type, in line with getLayoutRes - see above)
        val rowContactBinding = binding as RowContactBinding

        // extend contact 'cards' by phone numbers and emails (optional elements, variable size)
        // contact details --> LinearLayout within row_contact
        rowContactBinding.llContactDetails.removeAllViews()

        // append all phone numbers (dynamically --> don't really know how many there are)
        item.numbers.forEach {

            val rowContactDataBinding =
                RowContactDataBinding.inflate(
                    layoutInflater,
                    rowContactBinding.llContactDetails,
                    false
                )

            rowContactDataBinding.imgIcon.setImageResource(R.drawable.ic_baseline_local_phone_24)
            rowContactDataBinding.tvContactData.text = it
            rowContactBinding.llContactDetails.addView(rowContactDataBinding.root)
        }

        // append all email addresses (dynamically --> don't really know how many there are)
        item.emails.forEach {

            val rowContactDataBinding =
                RowContactDataBinding.inflate(
                    layoutInflater,
                    rowContactBinding.llContactDetails,
                    false
                )

            rowContactDataBinding.imgIcon.setImageResource(R.drawable.ic_baseline_email_24)
            rowContactDataBinding.tvContactData.text = it
            rowContactBinding.llContactDetails.addView(rowContactDataBinding.root)
        }

    }  // adjustViewItem

}
