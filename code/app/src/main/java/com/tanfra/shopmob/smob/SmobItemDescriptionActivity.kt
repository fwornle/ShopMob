package com.tanfra.shopmob.smob

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.databinding.ActivitySmobItemDescriptionBinding
import com.tanfra.shopmob.smob.data.repo.ato.SmobItemATO


// create inline function and reified type to simplify usage of creating an intent
// ... usage: see below - function 'newIntent'
inline fun <reified T : Activity> Context.createIntent(vararg args: Pair<String, Any>) : Intent {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundleOf(*args))
    return intent
}

//inline fun <reified T : Activity> Context.createIntentSerializable(arg: Pair<String, Serializable>) : Intent {
//    val intent = Intent(this, T::class.java)
//    val bundle = Bundle().apply { putSerializable(arg.first, arg.second) }
//    intent.putExtras(bundle)
//    return intent
//}

/**
 * Activity that displays the smob item details after the user clicks on the notification
 */
class SmobItemDescriptionActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_SmobItem = "EXTRA_SmobItem"

        // receive the smob item object after the user clicks on the notification
        fun newIntent(context: Context, shopMobDataItemATO: SmobItemATO): Intent {
            return context.createIntent<SmobItemDescriptionActivity>(EXTRA_SmobItem to shopMobDataItemATO)
        }

//                // old way of doing this
//                fun newIntent(context: Context, smobDataItem: SmobItem): Intent {
//                    val intent = Intent(context, SmobItemDescriptionActivity::class.java)
//                    intent.putExtra(EXTRA_SmobItem, smobDataItem)
//                    return intent
//                }
    }

    // data binding
    private lateinit var binding: ActivitySmobItemDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate layout
        binding = DataBindingUtil.setContentView(
            this,
            com.tanfra.shopmob.R.layout.activity_smob_item_description
        )

        // fetch data from intent provided by triggering notification
        var smobDataItem = SmobItemATO(
            "<not set>",
            "<not set>",
            "<not set>",
            -1.0,
            -1.0,
            "invalid"
        )

        // attempt to read extra data from notification
        val extras: Bundle? = intent.extras
        extras?.let {
            if (it.containsKey(EXTRA_SmobItem)) {
                // extract the extra-data in the notification
                smobDataItem = it.getSerializable("EXTRA_SmobItem") as SmobItemATO
            }
        }

        // set layout variable 'smobDataItem'
        binding.smobDataItem = smobDataItem

        // set onClick handler for DISMISS button
        // ... navigate back to the main app
        binding.btDismiss.setOnClickListener {
            val intent = Intent(this, SmobActivity::class.java)
            startActivity(intent)
        }

    }
}
