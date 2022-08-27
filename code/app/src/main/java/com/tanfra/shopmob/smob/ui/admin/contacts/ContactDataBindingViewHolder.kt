package com.tanfra.shopmob.smob.ui.admin.contacts

import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.tanfra.shopmob.databinding.RowContactBinding
import com.tanfra.shopmob.smob.data.repo.ato.SmobContactATO

/**
 * View Holder for the 'Contacts' Recycler View to bind the data item to the UI
 */
class ContactDataBindingViewHolder(private val binding: RowContactBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SmobContactATO) {
        binding.setVariable(BR.item , item)
        binding.executePendingBindings()
    }
}
