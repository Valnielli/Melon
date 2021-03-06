/*
 * Copyright (c) 2018.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.forcetower.uefs.feature.syncregistry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.forcetower.uefs.R
import com.forcetower.uefs.core.injection.Injectable
import com.forcetower.uefs.core.vm.UViewModelFactory
import com.forcetower.uefs.databinding.FragmentSyncRegistryBinding
import com.forcetower.uefs.feature.shared.UFragment
import com.forcetower.uefs.feature.shared.provideViewModel
import javax.inject.Inject

class SyncRegistryFragment : UFragment(), Injectable {
    @Inject
    lateinit var factory: UViewModelFactory

    private lateinit var binding: FragmentSyncRegistryBinding
    private lateinit var viewModel: SyncRegistryViewModel
    private lateinit var syncAdapter: SyncRegistryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = provideViewModel(factory)
        return FragmentSyncRegistryBinding.inflate(inflater, container, false).also {
            binding = it
        }.apply {
            incToolbar.apply {
                textToolbarTitle.text = getString(R.string.label_sync_registry)
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        syncAdapter = SyncRegistryAdapter()
        binding.recyclerRegistry.apply {
            adapter = syncAdapter
            addItemDecoration(DividerItemDecoration(context, VERTICAL))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.registry.observe(this, Observer { syncAdapter.submitList(it) })
    }
}