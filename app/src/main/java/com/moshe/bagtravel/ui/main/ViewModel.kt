package com.moshe.bagtravel.ui.main

import androidx.lifecycle.ViewModel
import com.moshe.bagtravel.model.Bag
import com.moshe.bagtravel.model.Travel
import com.moshe.bagtravel.ui.main.adapter.BagsAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor() : ViewModel(), BagsAdapter.OnBagClickListener {
    private val adapter = BagsAdapter(this)
    private val travelWeightThreshold = 3.0
    private val _resultUIState = MutableStateFlow<List<Travel>>(emptyList())
    val resultUIState: StateFlow<List<Travel>> = _resultUIState


    fun addBag(bag: Bag) {
        adapter.setBag(bag)
        submitTravels()
    }

    private fun submitTravels() {
        val travels = mutableListOf<Travel>()
        val bagsList = adapter.getBagsList().sortedWith(compareByDescending { it.weight })
        val stack = Stack<Bag>()
        stack.addAll(bagsList)

        val bags = mutableListOf<Bag>()
        while (stack.isNotEmpty()) {
            bags.add(stack.pop())
            val sum = bags.sumOf { it.weight }

            if (sum == travelWeightThreshold) {
                addTravel(travels, bags)
            } else if (sum > travelWeightThreshold) {
                returnLastBag(bags, stack)
                addTravel(travels, bags)
            }

            if (stack.isEmpty() && bags.isNotEmpty()) {
                travels.add(Travel(ArrayList(bags)))
            }
        }

        _resultUIState.value = travels
    }

    private fun returnLastBag(
        bags: MutableList<Bag>,
        stack: Stack<Bag>
    ) {
        val tmp = bags.last()
        bags.removeLast()
        stack.push(tmp)
    }

    private fun addTravel(
        travels: MutableList<Travel>,
        bags: MutableList<Bag>
    ) {
        travels.add(Travel(ArrayList(bags)))
        bags.clear()
    }

    fun getAdapter() = adapter
    override fun callback(position: Int) {
        submitTravels()
        adapter.notifyItemRemoved(position)
    }

}