package de.lrabe.gymweighttracker

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.lrabe.gymweighttracker.adapters.TabPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import de.lrabe.gymweighttracker.ui.main.SharedViewModel
import de.lrabe.gymweighttracker.util.InjectorUtils
import android.os.Parcelable
import androidx.lifecycle.Observer
import de.lrabe.gymweighttracker.data.Exercise
import de.lrabe.gymweighttracker.ui.main.SharedViewModel.NfcStatus.*
import de.lrabe.gymweighttracker.util.observeOnce

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelFactory =
            InjectorUtils.provideSharedViewModelFactory(applicationContext)
        viewModel = this.run {
            ViewModelProvider(this, viewModelFactory)[SharedViewModel::class.java]
        }

        val viewPagerAdapter = TabPagerAdapter(applicationContext, supportFragmentManager)
        viewPager = findViewById(R.id.viewpager)
        viewPager.adapter = viewPagerAdapter

        tab_layout.setupWithViewPager(viewpager)

        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        NfcAdapter.getDefaultAdapter(this)?.let { nfcAdapter ->
            if (nfcAdapter.isEnabled) {
                val launchIntent = Intent(applicationContext, this.javaClass)
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) // Don't start another instance

                // Supply this launch intent as the PendingIntent, set to cancel
                // one if it's already in progress. It never should be.
                val pendingIntent = PendingIntent.getActivity(
                    applicationContext, 0, launchIntent, PendingIntent.FLAG_CANCEL_CURRENT
                )

                // Define your filters and desired technology types
                val filters = arrayOf(
                    IntentFilter(ACTION_NDEF_DISCOVERED).apply {
                        addDataScheme("http")
                        addDataScheme("https")
                    },
                    IntentFilter(ACTION_NDEF_DISCOVERED).apply {
                        addDataType("*/*")
                    }
                )
                val techTypes = arrayOf(
                    arrayOf(Ndef::class.java.name),
                    arrayOf(NfcA::class.java.name),
                    arrayOf(NdefFormatable::class.java.name)
                )

                // enable Activity to receive NFC events
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techTypes)
                viewModel.setNfcStatus(ENABLED)
            } else {
                viewModel.setNfcStatus(DISABLED)
            }

        } ?: run {
            viewModel.setNfcStatus(MISSING)
        }
    }

    override fun onPause() {
        super.onPause()
        NfcAdapter.getDefaultAdapter(this)?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        intent = newIntent // very important, see https://stackoverflow.com/a/36942185
        handleIntent(newIntent)
    }

    private fun handleIntent(newIntent: Intent) {
        Toast.makeText(this, newIntent.action, Toast.LENGTH_SHORT).show()
        Log.d("TEST", "handleIntent()")
        when (newIntent.action) {
            ACTION_NDEF_DISCOVERED -> {
                Log.d("TEST", "intent ACTION_NDEF_DISCOVERED")
                newIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.let {
                    handleNdefMessages(it)
                }
            }
            else -> Log.d("TEST", "other intent: ${newIntent.action}")
        }
    }

    private fun handleNdefMessages(rawMessages: Array<Parcelable>) {
        val ndefMessage = rawMessages[0] as NdefMessage
        val payload = String(ndefMessage.records[0].payload)
        Toast.makeText(this, "payload: $payload", Toast.LENGTH_SHORT).show()

        viewModel.get(payload).observeOnce(this, Observer {
            if (it == null) {
                val newExercise = Exercise(payload, "", 0, 0, 0)
                viewModel.insert(newExercise)
                viewModel.selectExercise(newExercise)
            } else {
                viewModel.selectExercise(it)
            }
        })

        /*val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
        messages.forEach { message ->
            message.records.forEach { record ->
                val payload = String(record.payload)
                Log.d("TEST", "payload: $payload")
                Toast.makeText(this, "payload: $payload", Toast.LENGTH_SHORT).show()


            }
        }*/
    }

    fun setCurrentTab(index: Int) {
        viewPager.currentItem = index
    }
}
