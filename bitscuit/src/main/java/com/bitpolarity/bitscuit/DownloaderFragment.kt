package com.bitpolarity.bitscuit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class DownloaderFragment : Fragment() {

    companion object {
        fun newInstance() = DownloaderFragment()
    }

    private lateinit var viewModel: DownloaderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_downloader, container, false)
    }


}