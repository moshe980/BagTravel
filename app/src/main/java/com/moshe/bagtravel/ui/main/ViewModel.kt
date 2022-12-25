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
    private val maxTravelWeightThreshold = 3.0
    private val minTravelWeightThreshold = 1.01
    private val _resultUIState = MutableStateFlow<List<Travel>>(emptyList())
    val resultUIState: StateFlow<List<Travel>> = _resultUIState


    fun addBag(bag: Bag) {
        adapter.setBag(bag)
        submitTravels2()
    }

    private fun submitTravels2() {
        val travels = mutableListOf<Travel>()
        val bagsList = adapter.getBagsList().map { Bag(it.id, it.weight, false) }

        addAllBigBags(bagsList, travels)

        val lightBags =
            bagsList.filter { maxTravelWeightThreshold - it.weight >= minTravelWeightThreshold }
                .sortedWith(compareByDescending { it.weight })

        for (i in lightBags.indices) {//Choose first bag
            if (!lightBags[i].isTaken) {
                lightBags[i].isTaken = true
                val currentBag = lightBags[i]
                val bags = mutableListOf<Bag>()
                if (i == lightBags.size - 1) {//Last bag in the list
                    addTravelOfOneBag(bags, lightBags, i, travels)
                    break
                }
                if (lightBags.size == 1) {
                    travels.add(Travel(lightBags))
                    break
                } else {
                    for (j in lightBags.indices) {//Choose second bag
                        if (!lightBags[j].isTaken && (lightBags[j].weight + currentBag.weight) <= maxTravelWeightThreshold) {
                            lightBags[j].isTaken = true
                            addTravelOfTwoBags(lightBags, j, bags, currentBag, travels)
                            break
                        }
                    }
                }
            }
        }
        if (travels.isNotEmpty())
            _resultUIState.value = travels
    }

    private fun addTravelOfTwoBags(
        lightBags: List<Bag>,
        j: Int,
        bags: MutableList<Bag>,
        currentBag: Bag,
        travels: MutableList<Travel>
    ) {
        bags.add(currentBag)
        bags.add(lightBags[j])
        travels.add(Travel(ArrayList(bags)))
        bags.clear()
    }

    private fun addTravelOfOneBag(
        bags: MutableList<Bag>,
        lightBags: List<Bag>,
        i: Int,
        travels: MutableList<Travel>
    ) {
        bags.add(lightBags[i])
        travels.add(Travel(ArrayList(bags)))
        bags.clear()
    }

    private fun addAllBigBags(
        bagsList: List<Bag>,
        travels: MutableList<Travel>
    ) {
        bagsList.filter { maxTravelWeightThreshold - it.weight < minTravelWeightThreshold }
            .forEach {
                val bags = mutableListOf<Bag>()
                bags.add(it.copy())
                travels.add(Travel(bags))
            }
    }

    private fun submitTravels() {
        val travels = mutableListOf<Travel>()
        val bagsList = adapter.getBagsList()
        val allTravels = mutableListOf<Travel>()

        bagsList.filter { maxTravelWeightThreshold - it.weight < minTravelWeightThreshold }
            .forEach {
                val bags = mutableListOf<Bag>()
                bags.add(it)
                travels.add(Travel(bags))
            }

        val lightBags =
            bagsList.filter { maxTravelWeightThreshold - it.weight >= minTravelWeightThreshold }


        lightBags.powerSet().forEach {
            allTravels.add(Travel(ArrayList(it), it.sumOf { it.weight }))
        }

        val sortedTravels = allTravels.sortedWith(compareByDescending { it.sum })

        sortedTravels.forEach {
            if (it.sum < maxTravelWeightThreshold && it.sum > 0 && !isTravelsContainBag(
                    it.bags,
                    travels
                )
            ) {
                travels.add(it.copy())
            }
        }

        _resultUIState.value = travels


    }

    private fun isTravelsContainBag(bags: List<Bag>, travelList: List<Travel>): Boolean {
        travelList.forEach { travel ->
            travel.bags.forEach { bag ->
                bags.forEach {
                    if (it == bag) {
                        return true
                    }
                }
            }
        }
        return false

    }

    private fun <T> Collection<T>.powerSet(): Set<Set<T>> = when {
        isEmpty() -> setOf(setOf())
        else -> drop(1).powerSet().let { it + it.map { it + first() } }
    }


    fun getAdapter() = adapter
    override fun callback(position: Int) {
        submitTravels2()
        adapter.notifyItemRemoved(position)
    }

}