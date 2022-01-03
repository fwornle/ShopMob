package com.tanfra.shopmob.smob.activities.administration

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.databinding.ActivityAdministrationBinding
import com.tanfra.shopmob.smob.activities.details.createIntent
import timber.log.Timber


/**
 * Activity that collects administrative tasks (new/edit user, group, list)
 */
class SmobAdministrationActivity : AppCompatActivity() {

    // (singleton) object to facilitate navigation to this activity (and it's fragments)
    companion object {

        // intent 'extra' data specifier
        private const val EXTRA_SmobAdminTask = "EXTRA_SmobAdminTask"

        // caller (typically outside this activity) can create an intent with a SmobAdminTask
        fun newIntent(context: Context, smobTask: SmobAdminTask): Intent {
            return context.createIntent<SmobAdministrationActivity>(EXTRA_SmobAdminTask to smobTask)
        }

    }

    // data binding
    private lateinit var binding: ActivityAdministrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate layout
        binding = DataBindingUtil.setContentView(
            this,
            com.tanfra.shopmob.R.layout.activity_administration
        )

        // fetch data from intent provided by triggering notification
        var intentTask = SmobAdminTask.UNKNOWN

        // attempt to read extra data from incoming intent
        val extras: Bundle? = intent.extras
        extras?.let {
            if (it.containsKey(EXTRA_SmobAdminTask)) {
                // extract the extra-data in the intent
                intentTask = it.get("EXTRA_SmobAdminTask") as SmobAdminTask
            }
        }

        // navigate to the requested fragment
        when(intentTask) {
            SmobAdminTask.NEW_LIST -> Timber.i("Create a new Smob list (one day).")
            SmobAdminTask.EDIT_LIST -> Timber.i("Edit an existing Smob list (one day).")
            SmobAdminTask.NEW_USER -> Timber.i("Create a new Smob list (one day).")
            SmobAdminTask.EDIT_USER -> Timber.i("Edit an existing Smob list (one day).")
            SmobAdminTask.NEW_GROUP -> Timber.i("Create a new Smob list (one day).")
            SmobAdminTask.EDIT_GROUP -> Timber.i("Edit an existing Smob list (one day).")
            else -> Timber.i("Show the Administration selection screen (one day).")
        }

    }
}
