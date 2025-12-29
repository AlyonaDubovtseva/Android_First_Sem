package com.example.roomapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.roomapp.R
import com.example.roomapp.model.SortType
import com.example.roomapp.ui.theme.PastelPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            val sortOptions = listOf(
                SortOption(
                    type = SortType.NAME_ASC,
                    title = stringResource(R.string.sort_name_asc),
                    icon = Icons.Default.SortByAlpha
                ),
                SortOption(
                    type = SortType.NAME_DESC,
                    title = stringResource(R.string.sort_name_desc),
                    icon = Icons.Default.SortByAlpha
                ),
                SortOption(
                    type = SortType.AGE_ASC,
                    title = stringResource(R.string.sort_age_asc),
                    icon = Icons.Default.AccessTime
                ),
                SortOption(
                    type = SortType.AGE_DESC,
                    title = stringResource(R.string.sort_age_desc),
                    icon = Icons.Default.AccessTime
                ),
                SortOption(
                    type = SortType.RATING,
                    title = stringResource(R.string.sort_rating),
                    icon = Icons.Default.Star
                ),
                SortOption(
                    type = SortType.DATE_NEW,
                    title = stringResource(R.string.sort_date_new),
                    icon = Icons.Default.DateRange
                ),
                SortOption(
                    type = SortType.DATE_OLD,
                    title = stringResource(R.string.sort_date_old),
                    icon = Icons.Default.DateRange
                )
            )

            LazyColumn {
                items(sortOptions) { option ->
                    SortOptionItem(
                        option = option,
                        isSelected = currentSortType == option.type,
                        onClick = { onSortTypeSelected(option.type) }
                    )
                }
            }
        }
    }
}

@Composable
fun SortOptionItem(
    option: SortOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = option.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        leadingContent = {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (isSelected) PastelPink else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            if (isSelected) {
                RadioButton(
                    selected = true,
                    onClick = null,
                    colors = androidx.compose.material3.RadioButtonDefaults.colors(
                        selectedColor = PastelPink
                    )
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick)
    )
}

data class SortOption(
    val type: SortType,
    val title: String,
    val icon: ImageVector
)
