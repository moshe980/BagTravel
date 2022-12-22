package com.moshe.bagtravel.ui.main

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.moshe.bagtravel.R
import com.moshe.bagtravel.databinding.ActivityMainBinding
import com.moshe.bagtravel.model.Bag
import com.moshe.bagtravel.model.Travel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var dialog: Dialog

    private val viewModel: ViewModel by viewModels()
    private var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenStarted {
            viewModel.resultUIState.collectLatest {
                displayResult(it)
            }
        }

        configRecyclerView()

        binding.addBagBtn.setOnClickListener {
            openAddDialog()
        }


    }

    private fun configRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModel.getAdapter()
        }
    }

    private fun displayResult(it: List<Travel>) {
        result = ""
        it.forEachIndexed(::display)
        binding.resultTv.text = result
    }

    private fun display(i: Int, travel: Travel) {
        result += "${i + 1}. ${travel.bags} \n\n"
    }

    private fun openAddDialog() {
        configDialog()

        val idET = dialog.findViewById<EditText>(R.id.idET)
        val weightET = dialog.findViewById<EditText>(R.id.weightET)
        val saveBtn = dialog.findViewById<Button>(R.id.saveBtn)
        val cancelBtn = dialog.findViewById<Button>(R.id.cancelBtn)

        saveBtn.setOnClickListener {
            saveTapped(weightET, idET)
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()

        }

        dialog.show()
    }

    private fun configDialog() {
        dialog = Dialog(this)
        dialog.apply {
            setContentView(R.layout.edit_bag_dialog)
            setCanceledOnTouchOutside(false)
            window!!.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
        }
    }

    private fun saveTapped(weightET: EditText, idET: EditText) {
        val weight = weightET.text.toString().toDouble()
        if (weight <= 3.0 && weight >= 1.1) {
            viewModel.addBag(Bag(idET.text.toString().toInt(), weight))
            dialog.dismiss()
        } else {
            weightET.error = "Need to be between 1.1 - 3"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}