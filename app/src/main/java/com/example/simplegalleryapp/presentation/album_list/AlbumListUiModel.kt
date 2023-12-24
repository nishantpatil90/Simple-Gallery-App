package com.example.simplegalleryapp.presentation.album_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.simplegalleryapp.R
import com.example.simplegalleryapp.domain.model.Album
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
class AlbumListUiModel(
    private val uiAction: (Album) -> Unit,
    private val albumListFlow: StateFlow<List<Album>>
) {

    @Composable
    fun UiLayout() {
        Scaffold(
            topBar = { TopBarUi() },
            content = { ItemList(it) }
        )
    }

    @Composable
    private fun TopBarUi() {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.app_name))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.purple_500),
                titleContentColor = Color.White,
            )
        )
    }

    @Composable
    private fun ItemList(paddingValues: PaddingValues) {
        val albumList = albumListFlow.collectAsState()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(items = albumList.value) {
                AlbumItem(album = it)
            }
        }
    }

    @Composable
    private fun AlbumItem(album: Album) {
        val interactionSource = remember { MutableInteractionSource() }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { uiAction(album) }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_folder_24),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(text = album.label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
