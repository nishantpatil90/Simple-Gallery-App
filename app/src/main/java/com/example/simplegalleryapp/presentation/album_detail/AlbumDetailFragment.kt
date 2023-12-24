package com.example.simplegalleryapp.presentation.album_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplegalleryapp.domain.model.AlbumImage
import com.example.simplegalleryapp.util.DividerItemDecoration
import com.example.simplegalleryapp.ui.MainActivity
import com.example.simplegalleryapp.R
import com.example.simplegalleryapp.databinding.AlbumDetailFragmentBinding
import com.example.simplegalleryapp.presentation.album_detail.adapter.AlbumDetailAdapter
import com.example.simplegalleryapp.presentation.album_detail.ui_state.AlbumDetailState
import com.example.simplegalleryapp.presentation.image_detail.ImageDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {

    private val viewModel: AlbumDetailViewModel by viewModels()

    private var _binding: AlbumDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private val albumDetailAdapter by lazy { AlbumDetailAdapter(viewModel::handleImageClick) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.album_detail_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRecyclerView()
        initObservers()
    }

    private fun initRecyclerView() {
        binding.albumDetailRecyclerview.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = albumDetailAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), R.dimen.spacing_1dp))
        }
    }

    private fun initToolbar() {
        val backClickListener = View.OnClickListener {
            (activity as? MainActivity?)?.popFragment()
        }
        binding.toolbar.apply {
            title = viewModel.albumName
            (activity as? AppCompatActivity?)?.setSupportActionBar(this)
            setNavigationIcon(R.drawable.baseline_arrow_back_24)
            setNavigationOnClickListener(backClickListener)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.albumImageList.collectLatest {
                    albumDetailAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.state.collectLatest {
                    val state = it.getContentIfNotHandled()
                    state ?: return@collectLatest
                    when (state) {
                        is AlbumDetailState.LaunchImageDetailScreen ->
                            launchImageDetailFragment(state.albumImage)
                    }
                }
            }
        }
    }

    private fun launchImageDetailFragment(albumImage: AlbumImage) {
        (activity as? MainActivity?)?.replaceFragment(
            ImageDetailFragment.newInstance(
                albumImage.uri,
                albumImage.label
            )
        )
    }

    companion object {
        fun newInstance(albumId: Long, title: String): AlbumDetailFragment {
            val args = Bundle().apply {
                putLong(AlbumDetailViewModel.ALBUM_ID, albumId)
                putString(AlbumDetailViewModel.ALBUM_NAME, title)
            }
            return AlbumDetailFragment().apply {
                arguments = args
            }
        }
    }
}
