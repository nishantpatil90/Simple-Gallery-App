package com.example.simplegalleryapp.presentation.album_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.simplegalleryapp.domain.model.Album
import com.example.simplegalleryapp.ui.MainActivity
import com.example.simplegalleryapp.presentation.album_detail.AlbumDetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumListFragment : Fragment() {

    private val viewModel: AlbumListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AlbumListUiModel(
                    this@AlbumListFragment::launchAlbumImageFragment,
                    viewModel.albumsList
                ).UiLayout()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.fetchAlbums()
    }

    private fun launchAlbumImageFragment(album: Album) {
        (activity as? MainActivity?)?.replaceFragment(
            AlbumDetailFragment.newInstance(
                album.id,
                album.label
            )
        )
    }

    companion object {
        fun newInstance() = AlbumListFragment()
    }
}
