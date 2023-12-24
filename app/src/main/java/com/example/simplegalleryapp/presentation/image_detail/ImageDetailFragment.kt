package com.example.simplegalleryapp.presentation.image_detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.simplegalleryapp.ui.MainActivity
import com.example.simplegalleryapp.R
import com.example.simplegalleryapp.databinding.ImageDetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDetailFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: ImageDetailFragmentBinding? = null

    private var uri: Uri? = null
    private var imageTitle: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.image_detail_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBundleData()
        initToolbar()
        Glide.with(binding.photoView)
            .load(uri)
            .fitCenter()
            .into(binding.photoView)
    }

    private fun initToolbar() {
        val backClickListener = View.OnClickListener {
            (activity as? MainActivity?)?.popFragment()
        }
        binding.toolbar.apply {
            title = imageTitle
            (activity as? AppCompatActivity?)?.setSupportActionBar(this)
            setNavigationIcon(R.drawable.baseline_arrow_back_24)
            setNavigationOnClickListener(backClickListener)
        }
    }

    private fun getBundleData() {
        arguments?.apply {
            uri = getParcelable(IMAGE_URI) as? Uri?
            imageTitle = getString(IMAGE_NAME, "")
        }
        if (uri == null || imageTitle.isNullOrBlank()) {
            (activity as? MainActivity?)?.popFragment()
        }
    }

    companion object {

        private const val IMAGE_URI = "image_uri"
        private const val IMAGE_NAME = "image_name"

        fun newInstance(uri: Uri, albumName: String): ImageDetailFragment {
            val args = Bundle().apply {
                putParcelable(IMAGE_URI, uri)
                putString(IMAGE_NAME, albumName)
            }
            return ImageDetailFragment().apply {
                arguments = args
            }
        }
    }
}
